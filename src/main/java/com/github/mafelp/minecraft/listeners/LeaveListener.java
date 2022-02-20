package com.github.mafelp.minecraft.listeners;

import com.github.mafelp.discord.DiscordMessageBroadcast;
import com.github.mafelp.utils.Settings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent playerQuitEvent) {
        // Send an Event message that this player has joined
        if (Settings.events.contains("LeaveEvent")) {
            DiscordMessageBroadcast discordMessageBroadcast = new DiscordMessageBroadcast(
                    "Player " + playerQuitEvent.getPlayer().getDisplayName() + " left",
                    "Player " + playerQuitEvent.getPlayer().getDisplayName() + " has disconnected from the game!",
                    playerQuitEvent.getPlayer());
            discordMessageBroadcast.setName("LeaveEventBroadcaster");
            discordMessageBroadcast.start();
        }
    }
}
