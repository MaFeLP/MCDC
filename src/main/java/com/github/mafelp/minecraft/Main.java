package com.github.mafelp.minecraft;

import com.github.mafelp.accounts.AccountManager;
import com.github.mafelp.minecraft.commands.*;
import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Permissions;
import com.github.mafelp.utils.Settings;
import com.github.mafelp.discord.DiscordMain;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static com.github.mafelp.utils.Logging.debug;
import static com.github.mafelp.utils.Settings.prefix;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 * The main class which includes enable and
 * disable methods called by the server on startup
 * and shutdown or on reload.
 */
public final class Main extends JavaPlugin {

    /**
     * Method called by the server when plugin is being enabled.
     */
    @Override
    public void onEnable() {
        // Setting minecraftServer for use in other methods that are not in a plugin class.
        Settings.minecraftServer = this.getServer();

        // Logs greeting and version to the console
        Logging.info("Plugin MCDC version " + Settings.version + " is being loaded...");

        // Initializing settings and loading (default) configuration for further use.
        Settings.init();

        // Plugin startup logic
        // Register Listeners
        listenerRegistration();
        // Register commands
        commandRegistration();

        // Initialize and try starting up the discord bot.
        Thread discordInitThread = new DiscordMain(true);
        discordInitThread.setName("Initializing the Discord instance.");
        discordInitThread.start();

    }

    /**
     * Method called by the server when the plugin is being disabled.
     */
    @Override
    public void onDisable() {
        // Print thank you message to the console
        Logging.info("Plugin MCDC version " + Settings.version + " is being unloaded...");
        Logging.info("Thanks for using it!");

        // Safely shut down the Discord bot instance
        DiscordMain.shutdown();

        // Save the configuration
        Settings.saveConfiguration();

        // Saves the current state of the accounts to the file.
        try {
            AccountManager.createAccountsFile();
            AccountManager.saveAccounts();
        } catch (IOException e) {
            Logging.logIOException(e, "Error saving the Accounts file. ALL LINKS WILL BE LOST!");
        }
    }

    /**
     * Here are all listener registered
     */
    private void listenerRegistration() {
        // Setting the plugin manager to register listeners
        PluginManager pluginManager = Bukkit.getPluginManager();

        // All listeners
        // for more information read the Javadoc in the specific classes
        pluginManager.registerEvents(new JoinListener(), this);
        pluginManager.registerEvents(new MinecraftChatListener(), this);
    }

    /**
     * Here are all commands registered
     */
    private void commandRegistration() {
        // All commands
        // for more information read the Javadoc in the specific classes
        Objects.requireNonNull(getCommand("token")).setExecutor(new Token());
        Logging.info("Command \"token\" has been enabled.");
        Objects.requireNonNull(getCommand("config")).setExecutor(new Config());
        Logging.info("Command \"config\" has been enabled.");
        if (Settings.getConfiguration().getBoolean("enableLinking")) {
            Objects.requireNonNull(getCommand("link")).setExecutor(new Link());
            Logging.info("Command \"link\" has been enabled.");
            Objects.requireNonNull(getCommand("account")).setExecutor(new AccountCommand());
            Logging.info("Command \"account\" has been enabled.");
            Objects.requireNonNull(getCommand("unlink")).setExecutor(new Unlink());
            Logging.info("Command \"unlink\" has been enabled.");
        }
    }
}
