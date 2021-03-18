package com.github.mafelp.minecraft.skins;

import com.github.mafelp.Settings;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.io.File;

public class Skin {
    private final Player player;

    private final File skinDirectory;

    private static final String skinFormat = ".png";

    private final BufferedImage skin;
    private final File skinFile;
    private final File skinDirectorySkins;

    private final BufferedImage head;
    private final File headFile;
    private final File skinDirectoryHeads;

    public Skin(Player player, boolean getSkinFromMojang) {
        this.player = player;
        this.skinDirectory = Settings.getSkinDirectory();

        this.skinDirectorySkins = new File(skinDirectory, "skins/");
        if (getSkinFromMojang) {
            this.skin = SkinManager.getSkinFromMojang(player);
            this.skinFile = SkinManager.saveImage(skin, skinDirectorySkins, player.getDisplayName());
        } else {
            this.skinFile = SkinManager.getSkinFile(player);
            this.skin = SkinManager.getSkinFromFile(skinFile);
        }

        this.skinDirectoryHeads = new File(skinDirectory, "heads/");
        if (skin != null) {
            if (getSkinFromMojang) {
                this.head = SkinManager.getHead(skin);
                this.headFile = SkinManager.saveImage(head, skinDirectoryHeads, player.getDisplayName());
            } else {
                this.headFile = SkinManager.getHeadFile(player);
                this.head = SkinManager.getSkinFromFile(headFile);
            }
        } else {
            this.head = null;
            this.headFile = null;
        }
    }

    public Player getPlayer() {
        return player;
    }

    public File getSkinDirectory() {
        return skinDirectory;
    }

    public static String getSkinFormat() {
        return skinFormat;
    }

    public BufferedImage getSkin() {
        return skin;
    }

    public File getSkinFile() {
        return skinFile;
    }

    public File getSkinDirectorySkins() {
        return skinDirectorySkins;
    }

    public BufferedImage getHead() {
        return head;
    }

    public File getHeadFile() {
        return headFile;
    }

    public File getSkinDirectoryHeads() {
        return skinDirectoryHeads;
    }
}
