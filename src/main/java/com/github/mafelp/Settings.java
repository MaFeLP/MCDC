package com.github.mafelp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.IOException;

public class Settings {
    // Internal variables
    public static volatile DiscordApi discordApi = null;
    public static volatile Server minecraftServer = null;
    public static final String version = "v0.3-beta";

    // User defined variables
    public static volatile String prefix;
    public static volatile boolean shortMsg = true;
    public static volatile String serverName;
    private static String API_TOKEN;
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
            String first = ChatColor.GRAY + "╔" +
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
                    ChatColor.GRAY + "\n╚▶" +
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
    private static final File configurationFileDirectory = new File("./plugins/MCDC");
    private static final File configurationFile = new File(configurationFileDirectory, "config.yml");
    private static volatile YamlConfiguration configuration;

    public static void init() {
        if (!configurationFileDirectory.exists()) {
            boolean success = configurationFileDirectory.mkdirs();

            if (success) {
                minecraftServer.getLogger().info(prefix + "Created config directory plugins/MCDC");
            }
        }

        if (!configurationFile.exists()) {
            boolean success = false;
            try {
                success = configurationFileDirectory.createNewFile();
            } catch (IOException e) {
                minecraftServer.getLogger().warning(e.getMessage());
            }

            if (success) {
                minecraftServer.getLogger().info(prefix + "Created config file plugins/MCDC/config.yml");

                configuration = createDefaultConfig();
                saveConfiguration();
            }
        }

        configuration = YamlConfiguration.loadConfiguration(configurationFile);
        configuration.setDefaults(createDefaultConfig());
        API_TOKEN = configuration.getString("apiToken");
        shortMsg = configuration.getBoolean("useShortMessageFormat");
        prefix = configuration.getString("pluginPrefix");
        serverName = configuration.getString("serverName");
    }

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

    public static YamlConfiguration getConfiguration() {
        return configuration;
    }

    public static void saveConfiguration() {
        try {
            configuration.save(configurationFile);
            minecraftServer.getLogger().info(prefix + "Successfully saved Config File");
        } catch (IOException e) {
            minecraftServer.getLogger().warning(e.getMessage());
        }
    }

    public static String getApiToken() {
        return API_TOKEN;
    }
}