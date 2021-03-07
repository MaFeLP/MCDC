package com.github.mafelp.minecraft;

import com.github.mafelp.Settings;
import com.github.mafelp.discord.DiscordMain;
import com.github.mafelp.minecraft.commands.Link;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import static com.github.mafelp.Settings.prefix;
import java.util.Objects;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // setting prefix
        prefix = ChatColor.DARK_GRAY + "[" +
                ChatColor.GOLD + "MCDC" +
                ChatColor.DARK_GRAY + "]" +
                ChatColor.BLACK + ": " +
                ChatColor.RESET;

        Bukkit.getLogger().fine("Plugin MCDC version 0.1-beta is being loaded...");

        Settings.minecraftServer = this.getServer();

        // Plugin startup logic
        listenerRegistration();
        commandRegistration();

        DiscordMain.init();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().fine(prefix + "Plugin MCDC version 0.1-beta is being unloaded...");
        Bukkit.getLogger().fine(prefix + "Thanks for using it!");

        DiscordMain.shutdown();
    }

    private void listenerRegistration() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinListener(), this);
    }

    private void commandRegistration() {
        Objects.requireNonNull(getCommand("link")).setExecutor(new Link());
    }
}
