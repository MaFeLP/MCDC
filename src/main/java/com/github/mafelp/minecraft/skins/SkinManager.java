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

public class SkinManager {
    private static final boolean debug = true;
    private static final String fileExtension = ".png";

    private static boolean checkDirectories() {

        if (!Settings.getSkinDirectory().exists()) {
            boolean success = Settings.getSkinDirectory().mkdirs();

            if (success)
                Settings.minecraftServer.getLogger().info(Settings.prefix +
                        "Successfully created directory " + Settings.getSkinDirectory().getAbsolutePath());
            else {
                Settings.minecraftServer.getLogger().warning(Settings.prefix +
                        "Could not create directory " + Settings.getSkinDirectory().getAbsolutePath());
                return false;
            }
        }

        File headsDirectory = new File(Settings.getSkinDirectory(), "heads/");
        File skinsDirectory = new File(Settings.getSkinDirectory(), "skins/");

        if (checkSubFolder(headsDirectory)) return false;
        return !checkSubFolder(skinsDirectory);
    }

    private static boolean checkSubFolder(File skinsDirectory) {
        if (!skinsDirectory.exists()) {
            boolean success = skinsDirectory.mkdirs();

            if (success)
                Settings.minecraftServer.getLogger().info(Settings.prefix +
                        "Successfully created directory " + skinsDirectory.getAbsolutePath());
            else {
                Settings.minecraftServer.getLogger().warning(Settings.prefix +
                        "Could not create directory " + skinsDirectory.getAbsolutePath());
                return true;
            }
        }
        return false;
    }

    public static BufferedImage getHead(BufferedImage inputImage) {
        return inputImage.getSubimage(8, 8, 8, 8);
    }

    protected static BufferedImage getSkinFromFile(File skinFile) {
        if (!checkDirectories()) {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + "Error while checking skin directories");
            return null;
        }

        try {
            return ImageIO.read(skinFile);
        } catch (IOException ioException) {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + ChatColor.RED +
                    "Something went wrong whilst trying to load th skin. Error: " + ioException.getMessage());
        }
        return null;
    }

    protected static BufferedImage getSkinFromMojang(Player player) {
        if (!checkDirectories()) {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + "Error while checking skin directories");
            return null;
        }

        try {
            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Getting the URL for player "
                        + player.getDisplayName());
            URL skinUrl = new URL(getSkinUrl(player.getUniqueId().toString()));




            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Reading the skin file in.");
            return ImageIO.read(skinUrl);

        } catch (IOException e) {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + ChatColor.RED +
                    "Something went wrong whilst trying to download th skin. Error: " + e.getMessage());
        }
        return null;
    }

    protected static File saveImage(BufferedImage image, File folder, String imageName) {
        if (!checkDirectories()) {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + "Error while checking skin directories");
            return null;
        }

        try {
            File skinFile = new File(folder, imageName + fileExtension);
            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Creating Skin file " +
                        skinFile.getAbsolutePath());

            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Writing the skin to the file.");

            ImageIO.write(image, "png", skinFile);

            return skinFile;
        } catch (IOException e) {
            String errorCode;
            if (debug)
                errorCode = Arrays.toString(e.getStackTrace());
            else
                errorCode = e.getMessage();

            Settings.minecraftServer.getLogger().warning(Settings.prefix + ChatColor.RED +
                    "Error while trying to save skin image: " + errorCode);

            return null;
        }
    }

    private static String getContent(String link) {
        try {
            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix +
                        "Establishing a connection to Mojang's servers");
            URL url = new URL(link);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder outputLine = new StringBuilder();

            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Reading Response.");
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                outputLine.append(inputLine);
            }
            br.close();
            if (debug)
                Settings.minecraftServer.getLogger().info(Settings.prefix + "Response is: "
                        + outputLine.toString());
            return outputLine.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final JsonParser parser = new JsonParser();

    private static String getSkinUrl(String uuid) {
        String json = getContent("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
        assert json != null;
        JsonObject o = parser.parse(json).getAsJsonObject();

        if (debug)
            Settings.minecraftServer.getLogger().info(Settings.prefix + "Decoding field \"value\"...");
        String jsonBase64 = o.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
        byte[] decodedBytes = Base64.getDecoder().decode(jsonBase64);
        String decoded = new String(decodedBytes);
        if (debug)
            Settings.minecraftServer.getLogger().info(Settings.prefix + "Decoded content is: " + decoded);

        o = parser.parse(decoded).getAsJsonObject();
        String out = o.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
        if (debug)
            Settings.minecraftServer.getLogger().info(Settings.prefix + "Skin url is: " + out);
        return out;
    }

    protected static File getSkinFile(Player player) {
        return new File(Settings.getSkinDirectory(), "skins/" + player.getDisplayName() + fileExtension);
    }

    public static File getHeadFile(Player player) {
        return new File(Settings.getSkinDirectory(), "heads/" + player.getDisplayName() + fileExtension);
    }
}
