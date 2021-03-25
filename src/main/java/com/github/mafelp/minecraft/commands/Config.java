package com.github.mafelp.minecraft.commands;

import com.github.mafelp.utils.Logging;
import com.github.mafelp.discord.DiscordMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.github.mafelp.utils.Settings.*;

/**
 * Class that implements the /config command in the minecraft chat.
 */
public class Config implements CommandExecutor {
    /**
     * The command executing task.
     * @param commandSender The player/console who sent the command.
     * @param command The command sent by the commandSender.
     * @param s ???
     * @param args Additional arguments passed into the command.
     * @return The success state of the command and if the usage text should be displayed.
     */
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

        // The first argument defines the subcommand
        switch (args[0].toLowerCase(Locale.ROOT)) {
            // subcommand reload:
            // Reloads the configuration.
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
            // subcommand save:
            // Saves the current state of the configuration to the configuration file.
            case "save" -> {
                commandSender.sendMessage(prefix + ChatColor.YELLOW + "Saving configuration...");
                saveConfiguration();
                commandSender.sendMessage(prefix + ChatColor.GREEN + "Done saving the config!");
                return true;
            }
            // subcommand default:
            // resets the configuration to its defaults.
            case "default" -> {
                // if the subcommand has arguments, check them
                if (args.length >= 2) {
                    // if the argument is confirm, reset the configuration
                    if (args[1].equalsIgnoreCase("confirm")) {
                        commandSender.sendMessage(prefix + ChatColor.DARK_RED +
                                "Resetting the configuration file...");
                        try {
                            // tries to set the configuration to the defaults.
                            getConfiguration().loadFromString(createDefaultConfig().saveToString());
                        } catch (InvalidConfigurationException e) {
                            Logging.logInvalidConfigurationException(e, "Error whilst resetting the config to the defaults.");
                        }
                    } else {
                        // If not, send a confirmation failed message.
                        commandSender.sendMessage(prefix + ChatColor.RED +
                                "Confirmation failed!");
                    }
                } else {
                    // If no additional arguments were passed,
                    // The player will be asked to confirm his/her choices.
                    commandSender.sendMessage(prefix +  ChatColor.RED +
                            "Please type \"config default confirm\" to confirm your actions!");
                }
                // Return a successfull execution of the command.
                return true;
            }
            // No subcommand is given:
            default -> {
                // Return an unsuccessful execution of the command.
                return false;
            }
        }
    }
}
