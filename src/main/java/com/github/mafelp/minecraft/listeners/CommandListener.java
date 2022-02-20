package com.github.mafelp.minecraft.listeners;

import com.github.mafelp.discord.DiscordMessageBroadcast;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * The class that handles the command sending events.
 */
public class CommandListener implements Listener {
    /**
     * The method that handles commands executed by a player.
     * @param playerCommandPreprocessEvent The event passed in by the plugin framework with information about the event.
     */
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        if (!Settings.getConfiguration().getBoolean("sendCommandToDiscord.player", false)) {
            Logging.debug("Sending user commands to the discord server is disabled. Not sending command.");
            return;
        }

        String message = playerCommandPreprocessEvent.getMessage();
        Player player = playerCommandPreprocessEvent.getPlayer();

        DiscordMessageBroadcast discordMessageBroadcast = new DiscordMessageBroadcast(message, player);
        discordMessageBroadcast.start();
    }

    /**
     * The method that handles commands executed in the server's console.
     * @param serverCommandEvent The event passed in by the plugin framework with information about the event.
     */
    @EventHandler
    public void onServerCommand(ServerCommandEvent serverCommandEvent) {
        if (!Settings.getConfiguration().getBoolean("sendCommandToDiscord.server", false)) {
            Logging.debug("Sending server commands to the discord server is disabled. Not sending command.");
            return;
        }

        String command = serverCommandEvent.getCommand();

        DiscordMessageBroadcast discordMessageBroadcast = new DiscordMessageBroadcast(command);
        discordMessageBroadcast.start();
    }
}
