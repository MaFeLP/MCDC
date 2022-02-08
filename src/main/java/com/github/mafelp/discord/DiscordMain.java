package com.github.mafelp.discord;

import com.github.mafelp.accounts.AccountManager;
import com.github.mafelp.discord.commands.MainSlashCommandListener;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.*;

import java.io.IOException;
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
                    .setWaitForServersOnStartup(true)
                    // set the token, specified in the config.yml or with command "/token <TOKEN>"
                    .setToken(Settings.getApiToken())
                    // register listeners
                    .addListener(DiscordListener::new)
                    .addListener(MainSlashCommandListener::new)
                    .addListener(MessageComponentCreationListener::new)
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

        // Send an Event message that this player has joined
        if (Settings.events.contains("ServerStartupEvent")) {
            DiscordMessageBroadcast discordMessageBroadcast = new DiscordMessageBroadcast(
                    "Server Startup",
                    "The server has been started and is now ready to connect!",
                    null);
            discordMessageBroadcast.setName("StartupEventBroadcaster");
            discordMessageBroadcast.start();
        }
    }


    /**
     * Method to register all slash commands (in bulk).
     */
    private void registerSlashCommands() {
        List<SlashCommandBuilder> accountSlashCommands = new ArrayList<>();
        List<SlashCommandBuilder> adminSlashCommands = new ArrayList<>();

        // global help command. Is always enabled, but needs to be added to the account slash commands list,
        // as the list will be the list of global commands and overwrite any others created before.
        accountSlashCommands.add(SlashCommand.with("help", "A command to give help about this bot and its commands")
                .setDefaultPermission(true)
        );

        // Account command
        accountSlashCommands.add(SlashCommand.with("account", "A command for account management",
                Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "link", "Link your discord and minecraft accounts",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "token", "The OPTIONAL token to link your accounts", false)
                                )),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "name", "Change or get your username for the accounts",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "change to", "The string to change your name to. Leave blank to get current name.", false)
                                )),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "username", "Change or get your username for the accounts",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "change to", "The string to change your name to. Leave blank to get current name.", false)
                                )),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "get", "Gets information about an account",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.USER, "account", "Get information about this user. Leave blank to get yourself.", false)
                                )),
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "list", "List all currently linked accounts"),
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "unlink", "Unlink your discord account from your minecraft account.")
                )));
        // Link command
        accountSlashCommands.add(SlashCommand.with("link", "A command to link your discord and minecraft accounts",
                Collections.singletonList(
                        SlashCommandOption.create(SlashCommandOptionType.LONG, "token", "The token used to link your accounts", false)
                )
        ).setDefaultPermission(true));

        // Unlink command
        accountSlashCommands.add(SlashCommand.with("unlink", "Unlink your discord account from your minecraft account"));

        // Whisper and mcmsg commands
        accountSlashCommands.add(SlashCommand.with("whisper", "Whisper to your friends on the minecraft server!",
                Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.USER, "user", "The user to whisper your message to", true),
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "message", "What you want to whisper", true)
                ))
                .setDefaultPermission(true)
        );
        accountSlashCommands.add(SlashCommand.with("mcmsg", "Whisper to your friends on the minecraft server!",
                Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.USER, "user", "The user to whisper your message to", true),
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "message", "What you want to whisper", true)
                )).setDefaultPermission(true));

        // Create role and create channel commands
        adminSlashCommands.add(SlashCommand.with("create", "Create a channel/role for syncing minecraft and discord messages",
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
        .setDefaultPermission(false)
        );

        // Setup command
        adminSlashCommands.add(SlashCommand.with("setup","Creates and channel and a role for syncing minecraft and discord messages",
                Collections.singletonList(
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "The name of the role and the channel", true)
                ))
        .setDefaultPermission(false)
        );

        // If linking is NOT enabled, set the default permission for all the slash commands to false. No one can use them then
        if (!Settings.getConfiguration().getBoolean("enableLinking", true)) {
            Logging.info("Linking is not enabled. Setting permission for all slash commands to false.");
            int i = 0;
            for (SlashCommandBuilder slashCommandBuilder : accountSlashCommands) {
                if ( i != 0)
                    slashCommandBuilder.setDefaultPermission(false);
                i++;
            }
        }
        discordApi.bulkOverwriteGlobalApplicationCommands(accountSlashCommands).thenAccept(slashCommands ->
                slashCommands.forEach(slashCommand -> {
                    Logging.info("Added global slash command \"/" + slashCommand.getName() + "\"");
                    Logging.debug("Default-Permission for \"/" + slashCommand.getName() + "\": " + slashCommand.getDefaultPermission());
        }));
        for (Server server : discordApi.getServers()) {
            discordApi.bulkOverwriteServerApplicationCommands(server, adminSlashCommands).thenAccept(slashCommands -> {
                // Setup a list with all allowed Users, configured in the config file and the bot owner
                var allowedUserIDs = Settings.getConfiguration().getLongList("permission.discordServerAdmin.allowedUserIDs");
                allowedUserIDs = Settings.getConfiguration().getLongList("permission.discordBotAdmin.allowedUserIDs");
                allowedUserIDs.remove(1234L);
                if (!allowedUserIDs.contains(discordApi.getOwnerId()))
                    allowedUserIDs.add(discordApi.getOwnerId());

                // Register the slash commands for each server
                List<ServerApplicationCommandPermissionsBuilder> updatedSlashCommands = new ArrayList<>();
                List<ApplicationCommandPermissions> permissions = new ArrayList<>();
                Logging.debug("Updating admin slash command permission for server " + server.getName());
                // Check if the server owner of this server is in the allowed lists.
                // If not, add them only for this server and remove them afterwards.
                boolean serverOwnerIsAllowed = allowedUserIDs.contains(server.getOwnerId());
                if (!serverOwnerIsAllowed)
                    allowedUserIDs.add(server.getOwnerId());

                // Create permission to use this slash command for each allowed user
                allowedUserIDs.forEach(userID -> permissions.add(ApplicationCommandPermissions.create(userID, ApplicationCommandPermissionType.USER, true)));
                // Prepare the commands to have the new permissions: Allow all allowed users to use this slash command.
                slashCommands.forEach(slashCommand -> updatedSlashCommands.add(new ServerApplicationCommandPermissionsBuilder(slashCommand.getId(), permissions)));

                // Do the actual updates
                discordApi.batchUpdateApplicationCommandPermissions(server, updatedSlashCommands).thenAccept(serverSlashCommandPermissions ->
                        Logging.info("Updated admin slash command permissions for server " + server.getName()));

                if (!serverOwnerIsAllowed)
                    allowedUserIDs.remove(server.getOwnerId());

                permissions.clear();
                updatedSlashCommands.clear();
            });
        }
        Logging.info(ChatColor.GREEN + "Registered slash Commands.");
        Logging.info(ChatColor.YELLOW + "Information: Due to caching, it can take " + ChatColor.BOLD + "UP TO" + ChatColor.RESET + ChatColor.YELLOW + " an hour");
        Logging.info(ChatColor.YELLOW + "Information: until the slash commands can be used (until they appear or the message");
        Logging.info(ChatColor.YELLOW + "Information: \"InvalidInteractionID\" disappears when trying to use a slash command)");
    }

    /**
     * Shutdown method to disconnect the bot instance
     */
    public static void shutdown() {
        // Send an Event message that this player has joined
        if (Settings.events.contains("ServerShutdownEvent")) {
            DiscordMessageBroadcast discordMessageBroadcast = new DiscordMessageBroadcast(
                    "Server Stopped",
                    "The server has been stopped.",
                    null);
            discordMessageBroadcast.setName("ShutdownEventBroadcaster");
            discordMessageBroadcast.start();
        }

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
