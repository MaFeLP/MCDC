package com.github.mafelp.utils;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.github.mafelp.utils.Logging.info;
import static com.github.mafelp.utils.Logging.logIOException;

/**
 * Class containing all settings:
 * [
 *   Internal variables,
 *   user defined values,
 *   handling configuration files
 * ]
 */
public class Settings {
    // Internal variables:
    /**
     * communication API with Discord, defined in DiscordMain.init()
     */
    public static volatile DiscordApi discordApi = null;

    /**
     * this minecraft server, defined in Main.onEnable()
     */
    public static volatile Server minecraftServer = null;

    /**
     * version number of the plugin - displayed to users
     */
    public static final String version = "v0.13.0-beta";

    /**
     * enables more information being displayed while executing events
     */
    public static boolean debug = true;


    // User defined variables
    // Defined in plugins/MCDC/config.yml
    /**
     * plugin prefix -
     * optionally definable in config.yml -
     * else defined in createDefaultConfig() -
     * Used before log outputs in the console
     */
    public static volatile String prefix =
            ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "MCDC" +
            ChatColor.DARK_GRAY + "]" + ChatColor.BLACK + ": " + ChatColor.RESET;

    /**
     * Use if message prefix should be one (true) or two (true) lines -
     * optionally definable in config.yml -
     * else defined in createDefaultConfig()
     */
    public static volatile boolean shortMsg = true;

    /**
     * Name of the server displayed in footer of discord messages
     * defined in config.yml
     */
    public static volatile String serverName;

    /**
     * The events the server admin has subscribed to.
     */
    public static volatile List<String> events;

    /**
     * API token used to authenticate the bot to discord -
     * defined in config.yml -
     * token can be found on https://discord.com/developers/applications
     */
    private static String API_TOKEN;

    /**
     * Creates a prefix for a minecraft message
     * @param event Message create event made by the discord API containing information about the message and its
     *              source and author
     * @return usable message prefix, one or two lines. two lines include unicode characters
     */
    public static String msgPrefix(MessageCreateEvent event) {
        if (shortMsg || (event.getServer().isEmpty() || event.getServerTextChannel().isEmpty())) {
            String first = ChatColor.DARK_GRAY + "[" +
                    ChatColor.LIGHT_PURPLE + "DC" +
                    ChatColor.DARK_GRAY + "/" +
                    ChatColor.GOLD;
            String last = ChatColor.DARK_GRAY + "]" +
                    ChatColor.BLACK + ": " +
                    ChatColor.RESET;

            return first +
                    event.getMessageAuthor().getDisplayName() +
                    last;
        } else {
            String first = ChatColor.GRAY + "\u2554" +
                    ChatColor.DARK_GRAY + "[" +
                    ChatColor.LIGHT_PURPLE + "DC" +
                    ChatColor.DARK_GRAY + "/" +
                    ChatColor.GOLD;
            String second = ChatColor.DARK_GRAY + " in " +
                    ChatColor.DARK_AQUA;
            String third = ChatColor.DARK_GRAY + " on " +
                    ChatColor.DARK_AQUA;
            String last = ChatColor.DARK_GRAY + "]" +
                    ChatColor.BLACK + ": " +
                    ChatColor.GRAY + "\n\u255A\u25B6" +
                    ChatColor.RESET;

            return first +
                    event.getMessageAuthor().getDisplayName() +
                    second +
                    event.getServerTextChannel().get().getName() +
                    third +
                    event.getServer().get().getName() +
                    last;
        }
    }


    /**
     * Creates a prefix, without special characters, for a console message.
     * @param event Message create event made by the discord API containing information about the message and its
     *              source and author
     * @return usable message prefix, one or two lines. two lines include unicode characters
     */
    public static String consoleMessagePrefix(MessageCreateEvent event) {
        if ((event.getServer().isEmpty() || event.getServerTextChannel().isEmpty())) {
            String first = ChatColor.DARK_GRAY + "[" +
                    ChatColor.LIGHT_PURPLE + "DC" +
                    ChatColor.DARK_GRAY + "/" +
                    ChatColor.GOLD;
            String last = ChatColor.DARK_GRAY + "]" +
                    ChatColor.BLACK + ": " +
                    ChatColor.RESET;

            return first +
                    event.getMessageAuthor().getDisplayName() +
                    last;
        } else {
            String first = ChatColor.DARK_GRAY + "[" +
                    ChatColor.LIGHT_PURPLE + "DC" +
                    ChatColor.DARK_GRAY + "/" +
                    ChatColor.GOLD;
            String second = ChatColor.DARK_GRAY + " in " +
                    ChatColor.DARK_AQUA;
            String third = ChatColor.DARK_GRAY + " on " +
                    ChatColor.DARK_AQUA;
            String last = ChatColor.DARK_GRAY + "]" +
                    ChatColor.BLACK + ": " +
                    ChatColor.RESET;

            return first +
                    event.getMessageAuthor().getDisplayName() +
                    second +
                    event.getServerTextChannel().get().getName() +
                    third +
                    event.getServer().get().getName() +
                    last;
        }
    }

    /**
     * The prefix used to identify commands in the discord chats.
     */
    public static String discordCommandPrefix = ".";


    // Configuration
    /**
     * Directory where the configuration files are in
     */
    public static final File configurationFileDirectory = new File("./plugins/MCDC");

    /**
     * The file in which the configurations are specified in
     */
    private static final File configurationFile = new File(configurationFileDirectory, "config.yml");

    /**
     * The configuration which holds all configuration information
     */
    private static volatile YamlConfiguration configuration;

    /**
     * Initializing method of the settings -
     * initializes config and variables
     */
    public static void init() {
        // Checks if the configuration file directory does not exists,
        // if it doesn't: make one
        if (!configurationFileDirectory.exists()) {
            boolean success = configurationFileDirectory.mkdirs();

            // if the configuration file directory was created: log success message
            if (success) {
                info("Created config directory plugins/MCDC");
            }
        }

        // Check if the configuration file does not exists,
        // if it doesn't make one and fill with default configuration
        if (!configurationFile.exists()) {
            boolean success = false;
            // try to create the configuration file
            try {
                success = configurationFile.createNewFile();
            } catch (IOException e) {
                logIOException(e, "something went wrong, while trying to create the configuration file.");
            }

            // if the file was successfully created
            if (success) {
                // log success message
                info("Created config file plugins/MCDC/config.yml");

                // set the configuration to the default configuration
                configuration = createDefaultConfig();
                // save newly created configuration to the config.yml file
                saveConfiguration();
            }
        }

        // Apply the configuration values to their counter parts in this class
        configuration = YamlConfiguration.loadConfiguration(configurationFile);
        configuration.setDefaults(createDefaultConfig());
        API_TOKEN = configuration.getString("apiToken");
        shortMsg = configuration.getBoolean("useShortMessageFormat");
        prefix = configuration.getString("pluginPrefix");
        serverName = configuration.getString("serverName");
        events = configuration.getStringList("events");
        debug = configuration.getBoolean("debug");
        discordCommandPrefix = configuration.getString("discordCommandPrefix");
    }

    /**
     * creates a default and template configuration to set as default and use
     * when user didn't specify a value in config.yml
     * @return full configuration to set a default, when user didn't specify a value in config.yml
     */
    public static YamlConfiguration createDefaultConfig() {
        YamlConfiguration defaultConfiguration;
        defaultConfiguration = YamlConfiguration.loadConfiguration(configurationFile);
        defaultConfiguration.set("apiToken", null);
        defaultConfiguration.set("useShortMessageFormat", true);
        defaultConfiguration.set("pluginPrefix",  ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "MCDC" + ChatColor.DARK_GRAY + "]" + ChatColor.BLACK + ": " + ChatColor.RESET);
        defaultConfiguration.set("serverName", "A Minecraft Server");
        defaultConfiguration.set("debug", false);
        defaultConfiguration.set("discordCommandPrefix", ".");
        defaultConfiguration.set("deleteDiscordCommandMessages", false);
        defaultConfiguration.set("events", Arrays.asList(
                "JoinEvent",
                "LeaveEvent",
                "PlayerAdvancementEvent",
                "PlayerDeathEvent",
                "ServerShutdownEvent",
                "ServerStartupEvent"
        ));
        defaultConfiguration.set("channelIDs", Collections.singletonList(1234L));
        defaultConfiguration.set("roleIDs", Collections.singletonList(1234L));
        defaultConfiguration.set("enableLinking", true);
        defaultConfiguration.set("allowListAllAccounts", true);
        defaultConfiguration.set("showFooterInMessages", true);
        defaultConfiguration.set("activity.enabled", true);
        defaultConfiguration.set("activity.type", "listening");
        defaultConfiguration.set("activity.message", "to your messages 👀");
        defaultConfiguration.set("permission.accountEdit.level", 3);
        defaultConfiguration.set("permission.accountEdit.allowedUserUUIDs", new ArrayList<UUID>());
        defaultConfiguration.set("permission.configEdit.level", 3);
        defaultConfiguration.set("permission.configEdit.allowedUserUUIDs", new ArrayList<UUID>());
        defaultConfiguration.set("permission.discordServerAdmin.allowedUserIDs", new ArrayList<Long>());
        defaultConfiguration.set("permission.discordBotAdmin.allowedUserIDs", new ArrayList<Long>());
        defaultConfiguration.set("saveEscapeCharacterInConfig", true);
        defaultConfiguration.set("sendCommandToDiscord.player", false);
        defaultConfiguration.set("sendCommandToDiscord.server", false);

        return defaultConfiguration;
    }

    /**
     * Getter for the configuration
     * @return main configuration file
     */
    public static YamlConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * saves the configuration (values) in "YamlConfiguration configuration"
     * to the configuration file
     */
    public static void saveConfiguration() {
        try {
            // Saving the configuration and logging an info message
            configuration.save(configurationFile);
            minecraftServer.getLogger().info(prefix + "Successfully saved Config File");
        } catch (IOException e) {
            logIOException(e, "Something went wrong while trying to save the configuration.");
        }
    }

    /**
     * Getter for the API Token
     * @return discord bot API Token specified in the configuration
     */
    public static String getApiToken() {
        return API_TOKEN;
    }

    /**
     * Getter for the configurationFileDirectory
     * @return a File which contains the configuration file directory
     */
    public static File getConfigurationFileDirectory() {
        return configurationFileDirectory;
    }
}
