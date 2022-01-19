package com.github.mafelp.minecraft.listeners;

import com.github.mafelp.discord.DiscordMessageBroadcast;
import com.github.mafelp.utils.Settings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {
    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent playerAdvancementDoneEvent) {
        // Send an Event message that this player has joined
        if (Settings.events.contains("PlayerAdvancementEvent")) {
            DiscordMessageBroadcast discordMessageBroadcast = new DiscordMessageBroadcast(
                    "Advancement unlocked by " + playerAdvancementDoneEvent.getPlayer().getDisplayName() + "!",
                    "Player " + playerAdvancementDoneEvent.getPlayer().getDisplayName() + " has earned the advancement " + playerAdvancementDoneEvent.getAdvancement() + "!",
                    playerAdvancementDoneEvent.getPlayer());
            discordMessageBroadcast.setName("AdvancementEventBroadcaster");
            discordMessageBroadcast.start();
        }
    }
}
