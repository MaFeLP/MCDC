package com.github.mafelp.minecraft.skins;

import com.github.mafelp.utils.Settings;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Class with information about a Player's skin
 */
public class Skin {
    /**
     * The player whose skin is stored
     */
    private final Player player;

    /**
     * The directory which contains the subdirectories skins/ and heads/
     */
    private final File skinDirectory;

    /**
     * The format of the image (minecraft skins are always png)
     */
    private static final String skinFormat = ".png";

    /**
     * The skin image
     */
    private final BufferedImage skin;
    /**
     * The file which contains BufferedImage skin
     */
    private final File skinFile;
    /**
     * The directory which contains all the skin files
     */
    private final File skinDirectorySkins;

    /**
     * A sub image containing the head of a player
     */
    private final BufferedImage head;
    /**
     * The file which contains the head
     */
    private final File headFile;
    /**
     * The directory which contains all the head files
     */
    private final File skinDirectoryHeads;

    /**
     * Main constructor
     * @param player The Player whose skin to store in this class
     * @param getSkinFromMojang If the skin should be downloaded from Mojang
     */
    public Skin(Player player, boolean getSkinFromMojang) {
        this.player = player;
        this.skinDirectory = Settings.getConfigurationFileDirectory();

        // If the skins should be gotten from Mojang or not
        this.skinDirectorySkins = new File(skinDirectory, "skins/");
        if (getSkinFromMojang) {
            // If yes, ask the SkinManager to get the skin and save it
            this.skin = SkinManager.getSkinFromMojang(player);
            this.skinFile = SkinManager.saveImage(skin, skinDirectorySkins, player.getDisplayName());
        } else {
            // If not, get the skin from the files
            this.skinFile = SkinManager.getSkinFile(player);
            this.skin = SkinManager.getSkinFromFile(skinFile);
        }

        // If the skin is not null,
        this.skinDirectoryHeads = new File(skinDirectory, "heads/");
        if (skin != null) {
            // And if the skins should be gotten from Mojang
            if (getSkinFromMojang) {
                // If yes, ask the SkinManager to get the head and save it.
                this.head = SkinManager.getHead(skin);
                this.headFile = SkinManager.saveImage(head, skinDirectoryHeads, player.getDisplayName());
            } else {
                // If not, get it from the files
                this.headFile = SkinManager.getHeadFile(player);
                this.head = SkinManager.getSkinFromFile(headFile);
            }
        } else {
            // If the skin is null, also set the head to null
            this.head = null;
            this.headFile = null;
        }
    }

    /**
     * Creates a skin class and gets the skin and head from the stored files.
     * @param player the player to store its skin in.
     */
    public Skin(Player player){
        this.player = player;
        // Sets the directory for skins to the directory specified in the settings.
        this.skinDirectory = Settings.getConfigurationFileDirectory();

        // Sets the skin directory and ask the skin manager to get the skin from the skin file and save it.
        this.skinDirectorySkins = new File(skinDirectory, "skins/");
        this.skinFile = SkinManager.getSkinFile(player);
        this.skin = SkinManager.getSkinFromFile(skinFile);

        // Sets the head directory and check if the skin is not null
        this.skinDirectoryHeads = new File(skinDirectory, "heads/");
        if (skin != null) {
            // Ask the skin manager to get the head (file)
            this.headFile = SkinManager.getHeadFile(player);
            this.head = SkinManager.getSkinFromFile(headFile);
        } else {
            // If the skin is null, also set the head to null
            this.head = null;
            this.headFile = null;
        }
    }

    /**
     * Getter for the Player whose skin is stored in this class
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Getter for the directory which contains the child directories
     * <code>skin/</code> and <code>head/</code>
     * @return the parent directory as a file
     */
    public File getSkinDirectory() {
        return skinDirectory;
    }

    /**
     * Returns the skinFormat
     * @return ".png"
     */
    public static String getSkinFormat() {
        return skinFormat;
    }

    /**
     * Getter for the image
     * @return The skin as an image
     */
    public BufferedImage getSkin() {
        return skin;
    }

    /**
     * Getter for the skin file
     * @return file in which the skin is stored
     */
    public File getSkinFile() {
        return skinFile;
    }

    /**
     * Getter for the directory with contains the skins
     * @return File of the Directory in which are all the skin files
     */
    public File getSkinDirectorySkins() {
        return skinDirectorySkins;
    }

    /**
     * Getter for the head image
     * @return the head
     */
    public BufferedImage getHead() {
        return head;
    }

    /**
     * Getter for the file which contains the head.
     * @return The file which contains the head
     */
    public File getHeadFile() {
        return headFile;
    }

    /**
     * Getter for the directory in which all the heads are stored
     * @return the directory in which the heads are stored
     */
    public File getSkinDirectoryHeads() {
        return skinDirectoryHeads;
    }
}
