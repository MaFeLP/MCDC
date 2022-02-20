package com.github.mafelp.minecraft.listeners;

import com.github.mafelp.discord.DiscordMessageBroadcast;
import com.github.mafelp.utils.Settings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent playerDeathEvent) {
        // Send an Event message that this player has joined
        if (Settings.events.contains("PlayerDeathEvent")) {
            DiscordMessageBroadcast discordMessageBroadcast = new DiscordMessageBroadcast(
                    playerDeathEvent.getEntity().getDisplayName() + " died!",
                    playerDeathEvent.getDeathMessage(),
                    playerDeathEvent.getEntity());
            discordMessageBroadcast.setName("DeathEventBroadcaster");
            discordMessageBroadcast.start();
        }
    }
}
