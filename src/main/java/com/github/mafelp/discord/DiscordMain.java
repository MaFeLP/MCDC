package com.github.mafelp.discord;

import com.github.mafelp.Logging;
import com.github.mafelp.Settings;
import org.bukkit.ChatColor;
import org.javacord.api.DiscordApiBuilder;

import java.util.concurrent.CompletionException;

import static com.github.mafelp.Settings.prefix;

/**
 * The class that handles initiation and destruction of the discord bot instance(s)
 */
public class DiscordMain {
    /**
     * Method used to create the bot instance and log it in
     */
    public static void init() {
        // check if a token was specified
        if (Settings.getApiToken() == null) {
            // if no token was specified:
            // log error message and return
            Settings.minecraftServer.getLogger().warning(prefix + ChatColor.RED +
                    "No token given! Please use \"token <your token>\" to activate the plugin!");
            return;
        }
        // TODO Change: make this function a thread / use bukkit scheduler
        // Log that the instance is being started
        Settings.minecraftServer.getConsoleSender().sendMessage(prefix + ChatColor.DARK_GRAY +
                "Starting Discord Instance...");
        // try to log the instance in and set it in the settings
        try {
            // Create the API
            Settings.discordApi = new DiscordApiBuilder()
                    // set the token, specified in the config.yml or with command "/token <TOKEN>"
                    .setToken(Settings.getApiToken())
                    // register listeners
                    .addListener(new DiscordListener())
                    // lof the bot in and join the servers
                    .login().join();
                    // TODO Add: activity
        } catch (IllegalStateException | CompletionException exception) {
            // If the API creation fails,
            // log an error to the console.
            Settings.minecraftServer.getLogger().warning(prefix + ChatColor.RED +
                    "An error occurred whilst trying to create the discord instance! Error: " + exception.getMessage());
        }
    }

    /**
     * Shutdown method to disconnect the bot instance
     */
    public static void shutdown() {
        // check if the bot is already logged out
        if (Settings.discordApi == null) {
            // if so, log a message and return
            Logging.info("Discord API is already logged out!");
            return;
        }
        // Log that the bot is being shut down
        Logging.info(prefix + ChatColor.DARK_GRAY +
                "Shutting down Discord Instance...");
        // Disconnect the bot / shut the bot down
        Settings.discordApi.disconnect();
        Settings.discordApi = null;
    }
}
