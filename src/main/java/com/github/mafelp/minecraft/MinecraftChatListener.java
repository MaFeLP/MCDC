package com.github.mafelp.minecraft;

import com.github.mafelp.discord.ChannelAdmin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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

        ChannelAdmin.broadcastMessage(player, message);
    }
}
