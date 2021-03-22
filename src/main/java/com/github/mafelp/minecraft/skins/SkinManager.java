package com.github.mafelp.minecraft.skins;

import com.github.mafelp.Settings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;

import static com.github.mafelp.Settings.debug;

/**
 * Skin Manager used for managing getting skins from Mojang, saving them and getting head
 * images for the user avatar in Discord Messages.
 */
public class SkinManager {
    /**
     * Sets the file extension for skin files to .png
     */
    private static final String fileExtension = ".png";

    /**
     * Checks the skin and head directories and creates them if they don't exist
     * @return return success state, if all directories are present
     */
    private static boolean checkDirectories() {
        // Checks if main directory exists
        if (!Settings.getConfigurationFileDirectory().exists()) {
            boolean success = Settings.getConfigurationFileDirectory().mkdirs();

            // Processes the result of the making of the directories
            if (success)
                Settings.minecraftServer.getLogger().info(Settings.prefix +
                        "Successfully created directory " + Settings.getConfigurationFileDirectory().getAbsolutePath());
            else {
                Settings.minecraftServer.getLogger().warning(Settings.prefix +
                        "Could not create directory " + Settings.getConfigurationFileDirectory().getAbsolutePath());
                return false;
            }
        }

        File headsDirectory = new File(Settings.getConfigurationFileDirectory(), "heads/");
        File skinsDirectory = new File(Settings.getConfigurationFileDirectory(), "skins/");

        // Check directory for head images
        if (!checkSubFolder(headsDirectory)) return false;
        // Check and return directory for skin images
        return checkSubFolder(skinsDirectory);
    }

    /**
     * Checks if a folder exists and if not creates it. - Only used in checkDirectories()
     * @param directoryToCheck The File of the directory which to check and create
     * @return the present state of the directory
     */
    private static boolean checkSubFolder(File directoryToCheck) {
        // When the directory does not exists
        if (!directoryToCheck.exists()) {
            boolean success = directoryToCheck.mkdirs();

            // Processes the result of the making of the directories
            if (success) {
                Settings.minecraftServer.getLogger().info(Settings.prefix +
                        "Successfully created directory " + directoryToCheck.getAbsolutePath());
                return true;
            }
            else {
                Settings.minecraftServer.getLogger().warning(Settings.prefix +
                        "Could not create directory " + directoryToCheck.getAbsolutePath());
                return false;
            }
        }
        return true;
    }

    /**
     * Create a new BufferedImage in the boundaries of the head of a Minecraft Skin
     * @param inputImage the image to crop
     * @return a sub-image with boundaries (x=8, y=8, width=8, height=8)
     */
    protected static BufferedImage getHead(BufferedImage inputImage) {
        return inputImage.getSubimage(8, 8, 8, 8);
    }

    /**
     * Returns the image of a file
     * @param skinFile The file from which the image to get
     * @return The image of the input file
     */
    protected static BufferedImage getSkinFromFile(File skinFile) {
        // Check the directories
        if (!checkDirectories()) {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + "Error while checking skin directories");
            return null;
        }

        // Returns the image of an error
        try {
            return ImageIO.read(skinFile);
        } catch (IOException ioException) {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + ChatColor.RED +
                    "Something went wrong whilst trying to load th skin. Error: " + ioException.getMessage());
        }
        return null;
    }

    /**
     * Gets the according skin from the Mojang libraries - this should only be used by Skin.java!
     * - this should only be used by Skin.java!
     * @param player The player to get the skin of
     * @return the skin
     */
    protected static BufferedImage getSkinFromMojang(Player player) {
        // Check the directories before downloading the image from Mojang
        if (!checkDirectories()) {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + "Error while checking skin directories");
            return null;
        }

        // Try to download the skin
        try {
            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Getting the URL for player "
                        + player.getDisplayName());
            URL skinUrl = new URL(getSkinUrl(player.getUniqueId().toString()));

            // Read the skin from the URL and return he image
            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Reading the skin file in.");
            return ImageIO.read(skinUrl);

        } catch (IOException e) {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + ChatColor.RED +
                    "Something went wrong whilst trying to download th skin. Error: " + e.getMessage());
        }
        return null;
    }


    /**
     * Saves an image to a folder
     * @param image The image to save to a file
     * @param folder the folder in which the image should be saved
     * @param imageName the name of the image
     * @return Returns the file in which the image was saved
     */
    protected static File saveImage(BufferedImage image, File folder, String imageName) {
        // Check the directories before saving
        if (!checkDirectories()) {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + "Error while checking skin directories");
            return null;
        }

        // Try to save the file
        try {
            // Creates the skin file to return in the end
            File skinFile = new File(folder, imageName + fileExtension);
            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Creating Skin file " +
                        skinFile.getAbsolutePath());

            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Writing the skin to the file.");

            // Write the image to the file
            ImageIO.write(image, "png", skinFile);

            // Return the file
            return skinFile;
        } catch (IOException e) {
            String errorCode;

            // Only when debug is enabled, print the full stack trace.
            if (debug)
                errorCode = Arrays.toString(e.getStackTrace());
            else
                errorCode = e.getMessage();

            Settings.minecraftServer.getLogger().warning(Settings.prefix + ChatColor.RED +
                    "Error while trying to save skin image: " + errorCode);

            return null;
        }
    }

    /**
     * Gets the JSON Data from an URL
     * @param link the link to get the data from
     * @return the data
     */
    private static String getContent(String link) {
        try {
            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix +
                        "Establishing a connection to Mojang's servers");
            // Creates a URL from String
            URL url = new URL(link);
            // Establishes a https connection
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            // Create the reader to read the information from given URL
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder outputLine = new StringBuilder();

            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Reading Response.");
            String inputLine;

            // read all the lines Mojang's API responded
            while ((inputLine = br.readLine()) != null) {
                outputLine.append(inputLine);
            }
            // After reading everything, close the reader
            br.close();
            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Response is: "
                        + outputLine.toString());
            // Return the JSON Data
            return outputLine.toString();

        } catch (IOException e) {
            String errorCode;

            // Only when debug is enabled, print the full stack trace.
            if (debug)
                errorCode = Arrays.toString(e.getStackTrace());
            else
                errorCode = e.getMessage();

            Settings.minecraftServer.getLogger().warning(Settings.prefix + ChatColor.RED +
                    "Error while trying to save skin image: " + errorCode);

            return null;
        }
    }

    /**
     *  JSON Parser used to interpret JSOn Data
     */
    private static final JsonParser parser = new JsonParser();

    /**
     * Gets the URL of a minecraft player UUID
     * @param uuid the UUID to get the URL from
     * @return the url as a String
     */
    private static String getSkinUrl(String uuid) {
        // Gets the Data of the profile
        String json = getContent("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
        assert json != null;
        JsonObject o = parser.parse(json).getAsJsonObject();

        // Decoding field "value" of the JSON data
        if (debug)
            Settings.minecraftServer.getLogger().info(Settings.prefix + "Decoding field \"value\"...");
        // Get the value data
        String jsonBase64 = o.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
        // Decode it and write it to bytes
        byte[] decodedBytes = Base64.getDecoder().decode(jsonBase64);
        // Create a new string from the bytes
        String decoded = new String(decodedBytes);
        if (debug)
            Settings.minecraftServer.getLogger().info(Settings.prefix + "Decoded content is: " + decoded);

        // Get the decoded data (also JSON) as a JSON object
        o = parser.parse(decoded).getAsJsonObject();
        // Gets the value of the field "textures.SKIN.url"
        String out = o.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
        if (debug)
            Settings.minecraftServer.getLogger().info(Settings.prefix + "Skin url is: " + out);
        // Returns the skin URL
        return out;
    }

    /**
     * Gets the File with the corresponding player skin in it
     * @param player The player whose skin to grab
     * @return The file which contains the player's skin
     */
    protected static File getSkinFile(Player player) {
        return new File(Settings.getConfigurationFileDirectory(), "skins/" + player.getDisplayName() + fileExtension);
    }

    /**
     * Gets the file with the player's head in it
     * @param player The player to grab the head of.
     * @return The file which contains the player's head
     */
    protected static File getHeadFile(Player player) {
        return new File(Settings.getConfigurationFileDirectory(), "heads/" + player.getDisplayName() + fileExtension);
    }
}
