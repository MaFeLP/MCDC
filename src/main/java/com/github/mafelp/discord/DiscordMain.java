package com.github.mafelp.discord;

import com.github.mafelp.Settings;
import org.bukkit.ChatColor;
import org.javacord.api.DiscordApiBuilder;
import static com.github.mafelp.Settings.prefix;

public class DiscordMain {
    private final String TOKEN = "Your super secret Discord Bot Token: https://discord.com/developers/applications/";

    public static void init() {
        Settings.minecraftServer.getConsoleSender().sendMessage(prefix + ChatColor.DARK_GRAY +
                "Starting Discord Instance...");
        // Settings.minecraftServer.broadcastMessage("Test");
        try {
            Settings.discordApi = new DiscordApiBuilder()
                    .setToken(Settings.getApiToken())
                    .addListener(new DiscordListener())
                    .login().join();
        } catch (IllegalStateException exception) {
            Settings.minecraftServer.getConsoleSender().sendMessage(prefix + ChatColor.RED +
                    "Illegal state exception!");
        }
    }

    public static void shutdown() {
        Settings.minecraftServer.getConsoleSender().sendMessage(prefix + ChatColor.DARK_GRAY +
                "Shutting down Discord Instance...");
        Settings.discordApi.disconnect();
    }
}
