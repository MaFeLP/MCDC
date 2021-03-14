package com.github.mafelp.minecraft.commands;

import com.github.mafelp.Settings;
import com.github.mafelp.discord.DiscordMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Token implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage(Settings.prefix + "Sorry, you are not allowed to use this command!");
            return false;
        }

        if (args.length != 1) {
            commandSender.sendMessage(helpMessage);
            return false;
        }

        if (Settings.discordApi != null) {
            try {
                Settings.discordApi.disconnect();
                Settings.discordApi = null;
                commandSender.sendMessage(Settings.prefix + "Disconnected the Discord bot.");
            } catch (Exception e) {
                commandSender.sendMessage(Settings.prefix + "Could not disconnect the Discord Bot.");
            }
        }

        try {
            Settings.getConfiguration().set("apiToken", args[0]);
            Settings.saveConfiguration();
            Settings.init();
            DiscordMain.init();
            commandSender.sendMessage(Settings.prefix + ChatColor.GREEN + "Successfully saved config file!");
            commandSender.sendMessage(Settings.prefix + ChatColor.GREEN + "Using token: " + ChatColor.GRAY + Settings.getApiToken());
            return true;
        } catch (Exception exception) {
            commandSender.sendMessage(Settings.prefix + Settings.prefix + "An error appeared during the part reload.\n" +
                    "The error has been logged to the console.");
            Settings.minecraftServer.getLogger().warning("An error appeared during the setting of the discord api token:\n"
                    + exception.getMessage());
            return false;
        }
    }

    private static final String helpMessage = Settings.prefix + Settings.prefix
            + "Wrong usage!\nUse \"/token <Your Discord Token>\"!";
}
