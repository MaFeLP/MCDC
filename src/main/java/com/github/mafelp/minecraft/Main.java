package com.github.mafelp.minecraft;

import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Permissions;
import com.github.mafelp.utils.Settings;
import com.github.mafelp.discord.DiscordMain;
import com.github.mafelp.minecraft.commands.Config;
import com.github.mafelp.minecraft.commands.Link;
import com.github.mafelp.minecraft.commands.Token;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static com.github.mafelp.utils.Logging.debug;
import static com.github.mafelp.utils.Settings.debug;
import static com.github.mafelp.utils.Settings.prefix;
import static java.lang.System.out;

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
        // Logs greeting and version to the console
        Bukkit.getLogger().fine("Plugin MCDC version " + Settings.version + " is being loaded...");

        // Setting minecraftServer for use in other methods that are not in a plugin class.
        Settings.minecraftServer = this.getServer();

        // Initializing settings and loading (default) configuration for further use.
        Settings.init();

        // Plugin startup logic
        // Register Listeners
        listenerRegistration();
        // Register commands
        commandRegistration();

        // Initialize and try starting up the discord bot.
        DiscordMain.init();

        debug("ID: 459019618686730271 has the following permissions: \n" +
                "discordServerAdmin:\t" + CheckPermission.checkPermission(Permissions.discordServerAdmin, 459019618686730271L) + "\n" +
                "discordBotAdmin:\t\t" + CheckPermission.checkPermission(Permissions.discordBotAdmin, 459019618686730271L));
    }

    /**
     * Method called by the server when the plugin is being disabled.
     */
    @Override
    public void onDisable() {
        // Print thank you message to the console
        Bukkit.getLogger().fine(prefix + "Plugin MCDC version " + Settings.version + " is being unloaded...");
        Bukkit.getLogger().fine(prefix + "Thanks for using it!");

        // Safely shut down the Discord bot instance
        DiscordMain.shutdown();

        // Save the configuration
        Settings.saveConfiguration();
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
        Objects.requireNonNull(getCommand("link")).setExecutor(new Link());
        Logging.info("Command \"link\" has been enabled.");
        Objects.requireNonNull(getCommand("token")).setExecutor(new Token());
        Logging.info("Command \"token\" has been enabled.");
        Objects.requireNonNull(getCommand("config")).setExecutor(new Config());
        Logging.info("Command \"config\" has been enabled.");
    }
}
