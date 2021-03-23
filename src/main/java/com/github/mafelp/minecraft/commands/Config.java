package com.github.mafelp.minecraft.commands;

import com.github.mafelp.Logging;
import com.github.mafelp.Settings;
import com.github.mafelp.discord.DiscordMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.github.mafelp.Settings.*;

public class Config implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        // Only execute, if player is op
        if (!commandSender.isOp()) {
            commandSender.sendMessage(prefix + ChatColor.RED + "You can only use this command as operator!");
            return false;
        }

        // Get the arguments
        if (args.length == 0)
            return false;

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                commandSender.sendMessage(prefix + ChatColor.AQUA + "The config is being reloaded...");
                commandSender.sendMessage(prefix + ChatColor.RED + "Warning! The bot is being disconnected for the amount of time it takes to reload!");
                // Shutdown sequence
                DiscordMain.shutdown();
                // Reload sequence
                init();
                DiscordMain.init();
                commandSender.sendMessage(prefix + ChatColor.GREEN + "Reload done!");
                return true;
            }
            case "save" -> {
                commandSender.sendMessage(prefix + ChatColor.YELLOW + "Saving configuration...");
                saveConfiguration();
                commandSender.sendMessage(prefix + ChatColor.GREEN + "Done saving the config!");
                return true;
            }
            case "default" -> {
                if (args.length >= 2) {
                    if (args[1].equalsIgnoreCase("confirm")) {
                        commandSender.sendMessage(prefix + ChatColor.DARK_RED +
                                "Resetting the configuration file...");
                        try {
                            getConfiguration().loadFromString(createDefaultConfig().saveToString());
                        } catch (InvalidConfigurationException e) {
                            Logging.logInvalidConfigurationException(e, "Error whilst resetting the config to the defaults.");
                        }
                    } else {
                        commandSender.sendMessage(prefix + ChatColor.RED +
                                "Confirmation failed!");
                    }
                } else {
                    commandSender.sendMessage(prefix +  ChatColor.RED +
                            "Please type \"config default confirm\" to confirm your actions!");
                }
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
