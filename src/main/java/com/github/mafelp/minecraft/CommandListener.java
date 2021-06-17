package com.github.mafelp.minecraft;

import com.github.mafelp.discord.DiscordMessageBroadcast;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandListener implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        if (!Settings.getConfiguration().getBoolean("sendCommandToDiscord.player")) {
            Logging.debug("Sending user commands to the discord server is disabled. Not sending command.");
            return;
        }

        String message = playerCommandPreprocessEvent.getMessage();
        Player player = playerCommandPreprocessEvent.getPlayer();

        DiscordMessageBroadcast discordMessageBroadcast = new DiscordMessageBroadcast(message, player);
        discordMessageBroadcast.start();
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent serverCommandEvent) {
        if (!Settings.getConfiguration().getBoolean("sendCommandToDiscord.server")) {
            Logging.debug("Sending server commands to the discord server is disabled. Not sending command.");
            return;
        }

        String command = serverCommandEvent.getCommand();

        DiscordMessageBroadcast discordMessageBroadcast = new DiscordMessageBroadcast(command);
        discordMessageBroadcast.start();
    }
}
