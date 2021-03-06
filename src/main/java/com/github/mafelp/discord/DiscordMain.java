package com.github.mafelp.discord;

import com.github.mafelp.discord.commands.CreateRoleListener;
import com.github.mafelp.discord.commands.SetupListener;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import com.github.mafelp.discord.commands.CreateChannelListener;
import org.bukkit.ChatColor;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;

import java.util.concurrent.CompletionException;

import static com.github.mafelp.utils.Settings.discordApi;
import static com.github.mafelp.utils.Settings.prefix;

/**
 * The class that handles initiation and destruction of the discord bot instance(s)
 */
public class DiscordMain extends Thread {
    /**
     * Method used to create the bot instance and log it in
     */
    @Override
    public void run() {
        // check if a token was specified
        if (Settings.getApiToken() == null) {
            // if no token was specified:
            // log error message and return
            Settings.minecraftServer.getLogger().warning(prefix + ChatColor.RED +
                    "No token given! Please use \"token <your token>\" to activate the plugin!");
            return;
        }

        // Permissions, the bot needs to have
        Permissions botPermissions = new PermissionsBuilder()
                .setAllowed(PermissionType.VIEW_AUDIT_LOG)
                .setAllowed(PermissionType.MANAGE_ROLES)
                .setAllowed(PermissionType.MANAGE_CHANNELS)
                .setAllowed(PermissionType.CREATE_INSTANT_INVITE)
                .setAllowed(PermissionType.CHANGE_NICKNAME)
                .setAllowed(PermissionType.SEND_MESSAGES)
                .setAllowed(PermissionType.EMBED_LINKS)
                .setAllowed(PermissionType.ATTACH_FILE)
                .setAllowed(PermissionType.READ_MESSAGE_HISTORY)
                .setAllowed(PermissionType.READ_MESSAGES)
                .setAllowed(PermissionType.MENTION_EVERYONE)
                .setAllowed(PermissionType.USE_EXTERNAL_EMOJIS)
                .setAllowed(PermissionType.ADD_REACTIONS)
                .build();

        // TODO Change: make this function a thread / use bukkit scheduler
        // Log that the instance is being started
        Logging.info(ChatColor.DARK_GRAY + "Starting Discord Instance...");
        // try to log the instance in and set it in the settings
        try {
            // Create the API
            Settings.discordApi = new DiscordApiBuilder()
                    // set the token, specified in the config.yml or with command "/token <TOKEN>"
                    .setToken(Settings.getApiToken())
                    // register listeners
                    .addListener(DiscordListener::new)
                    .addListener(CreateChannelListener::new)
                    .addListener(CreateRoleListener::new)
                    .addListener(SetupListener::new)
                    // log the bot in and join the servers
                    .login().join();
                    // TODO Add: activity

            Logging.info(ChatColor.GREEN + "Successfully started the discord instance!");
            Logging.info(ChatColor.RESET + "The bot invitation token is: " + discordApi.createBotInvite(botPermissions));
        } catch (IllegalStateException | CompletionException exception) {
            // If the API creation fails,
            // log an error to the console.
            Logging.logException(exception,  ChatColor.RED +
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
        Logging.info(ChatColor.DARK_GRAY +
                "Shutting down Discord Instance...");
        // Disconnect the bot / shut the bot down
        Settings.discordApi.disconnect();
        Settings.discordApi = null;
    }
}
