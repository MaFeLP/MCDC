package com.github.mafelp.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import static com.github.mafelp.Settings.prefix;

public class MinecraftChatListener implements Listener {

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        /*
        * Available methods:
        *
        * event.getFormat();
        * event.getMessage();
        * event.getRecipients();
        */

        Player player = event.getPlayer();
        String message = event.getMessage();
        //TODO Add Discord Support
    }
}
