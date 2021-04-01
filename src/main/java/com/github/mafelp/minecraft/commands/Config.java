package com.github.mafelp.minecraft.commands;

import com.github.mafelp.utils.Logging;
import com.github.mafelp.discord.DiscordMain;
import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;

import java.util.List;

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
    public boolean onCommand(CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
        // Only execute, if player is op
        if (!commandSender.isOp()) {
            commandSender.sendMessage(prefix + ChatColor.RED + "You can only use this command as operator!");
            return false;
        }

        // aka. the first argument: e. g. reload
        Command subCommand1 = CommandParser.parseFromArray(args);

        // aka. the second argument> e. g. (reload) CONFIRM
        Command subcommand2 = CommandParser.parseFromArray(subCommand1.getArguments());

        // The first argument defines the subcommand
        switch (subCommand1.getCommand()) {
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
                subCommand1.getStringArgument(0).ifPresent(argument -> {
                    // if the argument is confirm, reset the configuration
                    if (argument.equalsIgnoreCase("confirm")) {
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
                        commandSender.sendMessage(prefix + ChatColor.RED + "Confirmation failed!");
                    }
                });

                if (subCommand1.getArguments().length == 0)
                    commandSender.sendMessage(prefix + "Please type " + ChatColor.GRAY + "config default confirm" +
                            ChatColor.RESET + " to confirm your actions!");

                // Return a successful execution of the command.
                return true;
            }
            // subcommand set:
            // sets a value in the configuration to the specified value
            case "set" -> {
                if (subCommand1.getArguments() == null) {
                    commandSender.sendMessage(prefix + ChatColor.RED + "Wrong usage! Please use " +
                        ChatColor.GRAY + "\"config set <path> <value>\"" + ChatColor.RED + "!"
                    );

                    return true;
                }

                // if ONE additional argument was passed
                if (subCommand1.getArguments().length <= 1) {
                    // if only one argument was passed.
                    if (subCommand1.getStringArgument(0).isPresent() && subCommand1.getStringArgument(1).isEmpty()) {
                        if (subCommand1.getStringArgument(0).get().equalsIgnoreCase("help")) {
                            commandSender.sendMessage(prefix + "Please use \"/config set <path> <value>\"");
                            return true;
                        } else {
                            // if the argument is not "help"
                            return false;
                        }
                    } else {
                        commandSender.sendMessage(prefix + ChatColor.RED + "Not enough arguments!");
                        commandSender.sendMessage(prefix + ChatColor.RED + "Please use \"/config set <path> <value>\"");
                        return true;
                    }
                }
                // if TOO many arguments were passed
                if (subCommand1.getArguments().length > 2) {
                    commandSender.sendMessage(prefix + "Too many arguments given!");
                    commandSender.sendMessage(prefix + ChatColor.RED + "Please use \"/config set <path> <value>\"");
                    return true;
                }

                // checks if an argument is present and if so,
                // tries to get a boolean, long and at last a string from the argument.
                subCommand1.getStringArgument(0).ifPresent(path -> {
                    // Checks if the argument is a boolean
                    if (subCommand1.getBooleanArgument(1).isPresent()) {
                        boolean boolValue = subCommand1.getBooleanArgument(1).get();
                        Settings.getConfiguration().set(path, boolValue);
                    // Checks if the argument is a number (long)
                    } else if (subCommand1.getLongArgument(1).isPresent()) {
                        long longValue = subCommand1.getLongArgument(1).get();
                        Settings.getConfiguration().set(path, longValue);
                    // the last check is, if the argument is a String.
                    } else if (subCommand1.getStringArgument(1).isPresent()){
                        String stringValue = subCommand1.getStringArgument(1).get();
                        Settings.getConfiguration().set(path, stringValue);
                    // If the argument could not be parsed, we throw an error.
                    } else {
                        commandSender.sendMessage(prefix + ChatColor.RED +
                                "An unknown error occurred. Sorry for the inconvenience...");
                    }
                });

                // Send a success message
                if (subCommand1.getStringArgument(0).isPresent() && subCommand1.getStringArgument(1).isPresent()) {
                    commandSender.sendMessage(prefix + ChatColor.GREEN +
                            "Set value " + ChatColor.GRAY + subCommand1.getStringArgument(0).get() +
                            ChatColor.GREEN + " to " + ChatColor.GRAY +
                            subCommand1.getStringArgument(1).get() + ChatColor.GREEN + ".\n" +

                            ChatColor.YELLOW + "Use " + ChatColor.GRAY + "/config save" + ChatColor.YELLOW +
                            " to save the config to the file!"
                    );
                    // commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, this command is currently not implemented.");
                    return true;
                } else {
                    return false;
                }
            }
            // subcommand get:
            // gets the value of a path in the configuration
            case "get" -> {
                // if there is a argument after get, execute.
                if (subCommand1.getStringArgument(0).isPresent()) {
                    String path = subCommand1.getStringArgument(0).get();
                    Object value = Settings.getConfiguration().get(path);

                    if (value != null) {
                        commandSender.sendMessage(prefix + ChatColor.GREEN +
                                "The configuration entry to " + ChatColor.GRAY + path + ChatColor.GREEN + " is: " +
                                ChatColor.GRAY + value.toString()
                        );
                    } else {
                        commandSender.sendMessage(prefix + ChatColor.RED + "There was an error parsing the value.\n" +
                                prefix + "Maybe the value doesn't exist?");
                    }
                // if no argument was parsed into the subcommand, give the person an error message.
                } else {
                    commandSender.sendMessage(prefix + ChatColor.RED + "Wrong usage! Please use " +
                            ChatColor.GRAY + "\"config get <value>\"" + ChatColor.RED + "!");
                }
                return true;
            }
            // subcommand add
            // adds a value to a list.
            case "add" -> {
                if (subCommand1.getArguments() == null) {
                    commandSender.sendMessage(prefix + ChatColor.RED + "Wrong usage! Please use " +
                            ChatColor.GRAY + "\"config add <path> <value>\"" + ChatColor.RED + "!"
                    );
                    return true;
                }

                // if ONE additional argument was passed
                if (subCommand1.getArguments().length <= 1) {
                    // if only one argument was passed.
                    if (subCommand1.getStringArgument(0).isPresent() && subCommand1.getStringArgument(1).isEmpty()) {
                        if (subCommand1.getStringArgument(0).get().equalsIgnoreCase("help")) {
                            commandSender.sendMessage(prefix + "Please use \"/config add <path> <value>\"");
                            return true;
                        } else {
                            // if the argument is not "help"
                            return false;
                        }
                    } else {
                        commandSender.sendMessage(prefix + ChatColor.RED + "Not enough arguments!");
                        commandSender.sendMessage(prefix + ChatColor.RED + "Please use \"/config set <path> <value>\"");
                        return true;
                    }
                }
                // if TOO many arguments were passed
                if (subCommand1.getArguments().length > 2) {
                    commandSender.sendMessage(prefix + "Too many arguments given!");
                    commandSender.sendMessage(prefix + ChatColor.RED + "Please use \"/config add <path> <value>\"");
                    return true;
                }

                // checks if an argument is present and if so,
                // tries to get a boolean, long and at last a string from the argument.
                subCommand1.getStringArgument(0).ifPresent(path -> {
                    // Checks if the argument is a boolean
                    if (subCommand1.getBooleanArgument(1).isPresent()) {
                        boolean boolValue = subCommand1.getBooleanArgument(1).get();
                        List<Boolean> booleanList = Settings.getConfiguration().getBooleanList(path);
                        booleanList.add(boolValue);
                        Settings.getConfiguration().set(path, booleanList);
                    // Checks if the argument is a number (long)
                    } else if (subCommand1.getLongArgument(1).isPresent()) {
                        long longValue = subCommand1.getLongArgument(1).get();
                        List<Long> longList = Settings.getConfiguration().getLongList(path);
                        longList.add(longValue);
                        Settings.getConfiguration().set(path, longList);

                        Logging.info(longList.toString());
                    // the last check is, if the argument is a String.
                    } else if (subCommand1.getStringArgument(1).isPresent()){
                        String stringValue = subCommand1.getStringArgument(1).get();
                        List<String> stringList = Settings.getConfiguration().getStringList(path);
                        stringList.add(stringValue);
                        Settings.getConfiguration().set(path, stringList);
                    // If the argument could not be parsed, we throw an error.
                    } else {
                        commandSender.sendMessage(prefix + ChatColor.RED +
                                "An unknown error occurred. Sorry for the inconvenience...");
                    }
                });

                // Send a success message
                if (subCommand1.getStringArgument(0).isPresent() && subCommand1.getStringArgument(1).isPresent()) {
                    commandSender.sendMessage(prefix + ChatColor.GREEN +
                            "Added value " + ChatColor.GRAY + subCommand1.getStringArgument(0).get() +
                            ChatColor.GREEN + " to " + ChatColor.GRAY +
                            subCommand1.getStringArgument(1).get() + ChatColor.GREEN + ".\n" +

                            ChatColor.YELLOW + "Use " + ChatColor.GRAY + "/config save" + ChatColor.YELLOW +
                            " to save the config to the file!"
                    );
                    // commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, this command is currently not implemented.");
                    return true;
                } else {
                    return false;
                }
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
