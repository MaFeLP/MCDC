package com.github.mafelp.minecraft;

import com.github.mafelp.Settings;
import com.github.mafelp.discord.DiscordMain;
import com.github.mafelp.minecraft.commands.Link;
import com.github.mafelp.minecraft.commands.Token;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import static com.github.mafelp.Settings.prefix;
import java.util.Objects;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger().fine("Plugin MCDC version " + Settings.version + " is being loaded...");

        Settings.minecraftServer = this.getServer();

        Settings.init();

        // Plugin startup logic
        listenerRegistration();
        commandRegistration();

        DiscordMain.init();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().fine(prefix + "Plugin MCDC version " + Settings.version + " is being unloaded...");
        Bukkit.getLogger().fine(prefix + "Thanks for using it!");

        DiscordMain.shutdown();
    }

    private void listenerRegistration() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinListener(), this);
    }

    private void commandRegistration() {
        Objects.requireNonNull(getCommand("link")).setExecutor(new Link());
        Objects.requireNonNull(getCommand("token")).setExecutor(new Token());
    }
}
