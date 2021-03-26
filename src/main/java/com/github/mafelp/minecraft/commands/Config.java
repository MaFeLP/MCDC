package com.github.mafelp.minecraft.commands;

import com.github.mafelp.utils.Logging;
import com.github.mafelp.discord.DiscordMain;
import com.github.mafelp.utils.Settings;
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
     * @param label The command being passed in, aka. argument 0.
     * @param args Additional arguments passed into the command.
     * @return The success state of the command and if the usage text should be displayed.
     */
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
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
                commandSender.sendMessage(prefix + ChatColor.RED + "Warning! The bot is being disconnected for" +
                        " the amount of time it takes to reload!");
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
                            Logging.logInvalidConfigurationException(e,
                                    "Error whilst resetting the config to the defaults.");
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
                // Return a successful execution of the command.
                return true;
            }
            // subcommand set:
            // sets a value in the configuration to the specified value
            case "set" -> {
                // if ONE additional argument was passed
                if (args.length <= 2) {
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("help")) {
                            commandSender.sendMessage(prefix + "Please use \"/config set <path> <value>\"");
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        commandSender.sendMessage(prefix + ChatColor.RED + "Not enough arguments!");
                        commandSender.sendMessage(prefix + ChatColor.RED + "Please use \"/config set <path> <value>\"");
                        return true;
                    }
                }
                // if TOO many arguments were passed
                if (args.length > 3) {
                    commandSender.sendMessage(prefix + "Too many arguments given!");
                    commandSender.sendMessage(prefix + ChatColor.RED + "Please use \"/config set <path> <value>\"");
                    return true;
                }

                // TODO add boolean and long support for saving:
                // e. g.:                       config set test true;   config set test 123456
                // this should NOT result in:   test: 'true'            test: '123456'
                // TODO add support for arguments with spaces
                // e. g.: config set test "hello world"

                Settings.getConfiguration().set(args[1], args[2]);

                commandSender.sendMessage(prefix + ChatColor.GREEN +
                        "Set value " + ChatColor.GRAY + args[1] + ChatColor.GREEN + " to " + ChatColor.GRAY +
                        args[2] + ChatColor.GREEN + ".\n" +
                        ChatColor.YELLOW + "Use " + ChatColor.GRAY + "/config save" + ChatColor.YELLOW +
                        " to save the config to the file!");
                // commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, this command is currently not implemented.");
                return true;
            }
            // subcommand get:
            // gets the value of a path in the configuration
            case "get" -> {
                commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, this command is currently ot implemented.");
                return true;
            }
            // subcommand add
            // adds a value to a list.
            case "add" -> {
                commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, this command is currently ot implemented. ");
                return true;
            }
            // subcommand remove
            // removes a value from a list
            case "remove" -> {
                commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, this command is currently ot implemented.  ");
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
