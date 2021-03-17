package com.github.mafelp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.IOException;

/**
 * Class containing all settings:
 * [
 *   Internal variables,
 *   user defined values,
 *   handling configuration files
 * ]
 */
public class Settings {
    // Internal variables::
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
    public static final String version = "v0.3.2-beta";


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


    // Configuration
    /**
     * Directory where the configuration files are in
     */
    private static final File configurationFileDirectory = new File("./plugins/MCDC");

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
                minecraftServer.getLogger().info(prefix + "Created config directory plugins/MCDC");
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
                // if it fails, print the stack trace to the console
                minecraftServer.getLogger().warning(prefix + ChatColor.RED +
                        "something went wrong, while trying to create the configuration file. Error: " +
                        e.getMessage());
            }

            // if the file was successfully created
            if (success) {
                // log success message
                minecraftServer.getLogger().info(prefix + "Created config file plugins/MCDC/config.yml");

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
    }

    /**
     * creates a default and template configuration to set as default and use
     * when user didn't specify a value in config.yml
     * @return full configuration to set a default, when user didn't specify a value in config.yml
     */
    private static YamlConfiguration createDefaultConfig() {
        YamlConfiguration defaultConfiguration;
        defaultConfiguration = YamlConfiguration.loadConfiguration(configurationFile);
        defaultConfiguration.set("apiToken", null);
        defaultConfiguration.set("useShortMessageFormat", true);
        defaultConfiguration.set("pluginPrefix",  ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "MCDC" +
                ChatColor.DARK_GRAY + "]" + ChatColor.BLACK + ": " + ChatColor.RESET);
        defaultConfiguration.set("serverName", "A Minecraft Server");
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
            // if saving fails, log the error message as warning
            minecraftServer.getLogger().warning(prefix + ChatColor.RED +
                    "Something went wrong while trying to save the configuration. Error: " + e.getMessage());
        }
    }

    /**
     * Getter for the API Token
     * @return discord bot API Token specified in the configuration
     */
    public static String getApiToken() {
        return API_TOKEN;
    }
}