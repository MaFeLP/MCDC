package com.github.mafelp.discord;

import com.github.mafelp.accounts.AccountManager;
import com.github.mafelp.discord.commands.*;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletionException;

import static com.github.mafelp.utils.Settings.discordApi;
import static com.github.mafelp.utils.Settings.prefix;

/**
 * The class that handles initiation and destruction of the discord bot instance(s)
 */
public class DiscordMain extends Thread {
    /**
     * The switch that decides, if the Accounts should be loaded after the login of the Bot instance.
     */
    private final boolean loadAccounts;

    /**
     * Constructor to set the {@link DiscordMain#loadAccounts} switch.
     * @param loadAccounts if the accounts should be loaded after bot startup.
     */
    public DiscordMain(boolean loadAccounts) {
        this.loadAccounts = loadAccounts;
    }

    /**
     * Constructor for normal discord instance startup. Does not load the Accounts in.
     */
    public DiscordMain() {
        this.loadAccounts = false;
    }

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
                .setAllowed(PermissionType.MANAGE_MESSAGES)
                .build();

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
                    //.addListener(CreateChannelListener::new)
                    //.addListener(CreateRoleListener::new)
                    //.addListener(SetupListener::new)
                    .addListener(MainSlashCommandListener::new)
                    // log the bot in and join the servers
                    .login().join();

            registerSlashCommands();

            Logging.info(ChatColor.GREEN + "Successfully started the discord instance!");
            Logging.info(ChatColor.RESET + "The bot invitation token is: " + discordApi.createBotInvite(botPermissions));
            Logging.info(ChatColor.YELLOW + "If you do not see any slash commands in your server, click this link and authorise this bot for your server: https://discord.com/api/oauth2/authorize?client_id=" + discordApi.getClientId() + "&scope=applications.commands");
        } catch (IllegalStateException | CompletionException exception) {
            // If the API creation fails,
            // log an error to the console.
            Logging.logException(exception, ChatColor.RED +
                    "An error occurred whilst trying to create the discord instance! Error: " + exception.getMessage());
            return;
        }

        // Checks the configuration and sets the according activity.
        if (Settings.getConfiguration().getBoolean("activity.enabled", true)) {
            String activityMessage = Objects.requireNonNull(Settings.getConfiguration().getString("activity.message", "to your messages 👀"));
            String activityType = Objects.requireNonNull(Settings.getConfiguration().getString("activity.type", "listening")).toUpperCase(Locale.ROOT);

            discordApi.updateActivity(ActivityType.valueOf(activityType), activityMessage);
            Logging.info("Set the activity to type " + ChatColor.GRAY + activityType + ChatColor.RESET + " and the text to " + ChatColor.GRAY + activityMessage + ChatColor.RESET + ".");
        }

        if (this.loadAccounts) {
            // Loads all the Accounts to memory
            try {
                AccountManager.loadAccounts();
            } catch (IOException e) {
                Logging.logIOException(e, "Could not load the Accounts in. The Account file is not present and it could not be created.");
            }
        }
    }

    private void registerSlashCommands() {
        List<SlashCommandBuilder> slashCommands = new ArrayList<>();

        // Link command
        slashCommands.add(SlashCommand.with("link", "A command to link your discord and minecraft accounts",
                Collections.singletonList(
                        SlashCommandOption.create(SlashCommandOptionType.INTEGER, "token", "The token used to link your accounts", false)
                )
        ));

        // Unlink command
        slashCommands.add(SlashCommand.with("unlink", "Unlink your discord account from your minecraft account"));

        // Whisper and mcmsg commands
        slashCommands.add(SlashCommand.with("whisper", "Whisper to your friends on the minecraft server!",
                Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.USER, "user", "The user to whisper your message to", true),
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "message", "What you want to whisper", true)
                ))
        );
        slashCommands.add(SlashCommand.with("mcmsg", "Whisper to your friends on the minecraft server!",
                Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.USER, "user", "The user to whisper your message to", true),
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "message", "What you want to whisper", true)
                )
        ));

        slashCommands.add(SlashCommand.with("create", "Create a channel/role for syncing minecraft and discord messages",
                Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "channel", "Create a channel to sync minecraft messages to",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "The name the channel should have", true)
                                )),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "role", "Create a role which you can give the permission to read/write to channels",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "The name the channel should have", true)
                                ))
                ))
        //.setDefaultPermission(false)
        );

        // Do the actual registering of the slash commands.
        slashCommands.forEach(slashCommandBuilder ->
                slashCommandBuilder.createGlobal(discordApi).thenAccept(slashCommand ->
                    Logging.info("Added global slash command \"" + slashCommand.getName() + "\"")
        ));
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
        try {
            Settings.discordApi.disconnect();
        } catch (IllegalStateException ignored) {}
        Settings.discordApi = null;
    }
}
