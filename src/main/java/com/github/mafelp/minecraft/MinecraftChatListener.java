package com.github.mafelp.minecraft;

import com.github.mafelp.discord.ChannelAdmin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Class that reacts to chat messages sent on the minecraft server.
 */
public class MinecraftChatListener implements Listener {

    /**
     * Method called by the minecraft server when a chat message is being sent
     * @param event class containing information about the message
     */
    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        // Getting information about the message
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Hand the sending of the message to the dedicated method.
        ChannelAdmin.broadcastMessage(player, message);
    }
}
