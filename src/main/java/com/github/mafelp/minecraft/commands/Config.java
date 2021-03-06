package com.github.mafelp.minecraft.commands;

import com.github.mafelp.utils.*;
import com.github.mafelp.discord.DiscordMain;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
        // Only execute, if player has the required permissions.
        if (!CheckPermission.checkPermission(Permissions.configEdit, commandSender)) {
            commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, you do not have the required permissions to execute this command.");
            return true;
        }

        // Creates the command and checks for errors.
        Command subcommandConstruction;

        try {
            subcommandConstruction = CommandParser.parseFromArray(args);
        } catch (NoCommandGivenException | CommandNotFinishedException exception) {
            Logging.logException(exception, "Command not finished/given. CommandParser: 'config'");
            commandSender.sendMessage(prefix + ChatColor.RED + "Error processing command! Exception " + exception.getMessage() + ". Please make sure, that you have an even amount of quotation marks and a subcommand.");
            return true;
        }

        final Command subCommand = subcommandConstruction;

        Logging.debug("Executing command 'config'. Subcommand is: " + subCommand.getCommand() + " and arguments are: " + Arrays.toString(subCommand.getArguments()));

        // The first argument defines the subcommand
        switch (subCommand.getCommand()) {
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
                Thread discordInitThread = new DiscordMain();
                discordInitThread.setName("Initializing the Discord instance.");
                discordInitThread.start();
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
                subCommand.getStringArgument(0).ifPresent(argument -> {
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

                if (subCommand.getArguments().length == 0)
                    commandSender.sendMessage(prefix + "Please type " + ChatColor.GRAY + "config default confirm" +
                            ChatColor.RESET + " to confirm your actions!");

                // Return a successful execution of the command.
                return true;
            }
            // subcommand set:
            // sets a value in the configuration to the specified value
            case "set" -> {
                if (subCommand.getArguments() == null) {
                    commandSender.sendMessage(prefix + ChatColor.RED + "Wrong usage! Please use " +
                        ChatColor.GRAY + "\"config set <path> <value>\"" + ChatColor.RED + "!"
                    );

                    return true;
                }

                // Prevents IllegalArgumentException and CommandException
                // because a string must be passed in to set the value to.
                if (subCommand.getStringArgument(0).isPresent()) {
                    if (subCommand.getStringArgument(0).get().equalsIgnoreCase("")) {
                        commandSender.sendMessage(prefix + ChatColor.RED + "Wrong usage! Please use " +
                                ChatColor.GRAY + "\"config set <path> <value>\"" + ChatColor.RED + "!"
                        );

                        return true;
                    }
                }

                // if ONE additional argument was passed
                if (subCommand.getArguments().length <= 1) {
                    // if only one argument was passed.
                    if (subCommand.getStringArgument(0).isPresent() && subCommand.getStringArgument(1).isEmpty()) {
                        if (subCommand.getStringArgument(0).get().equalsIgnoreCase("help")) {
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
                if (subCommand.getArguments().length > 2) {
                    commandSender.sendMessage(prefix + "Too many arguments given!");
                    commandSender.sendMessage(prefix + ChatColor.RED + "Please use \"/config set <path> <value>\"");
                    return true;
                }

                // checks if an argument is present and if so,
                // tries to get a boolean, long and at last a string from the argument.
                subCommand.getStringArgument(0).ifPresent(path -> {
                    // Checks if the argument is a boolean
                    if (subCommand.getBooleanArgument(1).isPresent()) {
                        boolean boolValue = subCommand.getBooleanArgument(1).get();
                        Settings.getConfiguration().set(path, boolValue);
                    // Checks if the argument is a number (long)
                    } else if (subCommand.getLongArgument(1).isPresent()) {
                        long longValue = subCommand.getLongArgument(1).get();
                        Settings.getConfiguration().set(path, longValue);
                    // the last check is, if the argument is a String.
                    } else if (subCommand.getStringArgument(1).isPresent()){
                        String stringValue = subCommand.getStringArgument(1).get();
                        Settings.getConfiguration().set(path, stringValue);
                    // If the argument could not be parsed, we throw an error.
                    } else {
                        commandSender.sendMessage(prefix + ChatColor.RED +
                                "An unknown error occurred. Sorry for the inconvenience...");
                    }
                });

                // Send a success message
                if (subCommand.getStringArgument(0).isPresent() && subCommand.getStringArgument(1).isPresent()) {
                    commandSender.sendMessage(prefix + ChatColor.GREEN +
                            "Set value " + ChatColor.GRAY + subCommand.getStringArgument(0).get() +
                            ChatColor.GREEN + " to " + ChatColor.GRAY +
                            subCommand.getStringArgument(1).get() + ChatColor.GREEN + ".\n" +

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
                if (subCommand.getStringArgument(0).isPresent()) {
                    String path = subCommand.getStringArgument(0).get();
                    Object value = Settings.getConfiguration().get(path);

                    if (value != null) {
                        commandSender.sendMessage(prefix + ChatColor.GREEN +
                                "The configuration entry to " + ChatColor.GRAY + path + ChatColor.GREEN + " is: " +
                                ChatColor.GRAY + value
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
                if (subCommand.getArguments() == null) {
                    commandSender.sendMessage(prefix + ChatColor.RED + "Wrong usage! Please use " +
                            ChatColor.GRAY + "\"config add <path> <value>\"" + ChatColor.RED + "!"
                    );
                    return true;
                }

                // if ONE additional argument was passed
                if (subCommand.getArguments().length <= 1) {
                    // if only one argument was passed.
                    if (subCommand.getStringArgument(0).isPresent() && subCommand.getStringArgument(1).isEmpty()) {
                        if (subCommand.getStringArgument(0).get().equalsIgnoreCase("help")) {
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
                if (subCommand.getArguments().length > 2) {
                    commandSender.sendMessage(prefix + "Too many arguments given!");
                    commandSender.sendMessage(prefix + ChatColor.RED + "Please use \"/config add <path> <value>\"");
                    return true;
                }

                // checks if an argument is present and if so,
                // tries to get a boolean, long and at last a string from the argument.
                subCommand.getStringArgument(0).ifPresent(path -> {
                    // Checks if the argument is a boolean
                    if (subCommand.getBooleanArgument(1).isPresent()) {
                        boolean boolValue = subCommand.getBooleanArgument(1).get();
                        List<Boolean> booleanList = Settings.getConfiguration().getBooleanList(path);
                        booleanList.add(boolValue);
                        Settings.getConfiguration().set(path, booleanList);
                    // Checks if the argument is a number (long)
                    } else if (subCommand.getLongArgument(1).isPresent()) {
                        long longValue = subCommand.getLongArgument(1).get();
                        List<Long> longList = Settings.getConfiguration().getLongList(path);
                        longList.add(longValue);
                        Settings.getConfiguration().set(path, longList);

                        Logging.info(longList.toString());
                    // the last check is, if the argument is a String.
                    } else if (subCommand.getStringArgument(1).isPresent()){
                        String stringValue = subCommand.getStringArgument(1).get();
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
                return sendSuccessMessage(commandSender, subCommand);
            }
            // subcommand remove
            // removes a value from a list
            case "remove" -> {
                if (subCommand.getArguments() == null) {
                    commandSender.sendMessage(prefix + ChatColor.RED + "Wrong usage! Please use " +
                            ChatColor.GRAY + "\"config remove <path> <value>\"" + ChatColor.RED + "!"
                    );
                    return true;
                }

                // if ONE additional argument was passed
                if (subCommand.getArguments().length <= 1) {
                    // if only one argument was passed.
                    if (subCommand.getStringArgument(0).isPresent() && subCommand.getStringArgument(1).isEmpty()) {
                        if (subCommand.getStringArgument(0).get().equalsIgnoreCase("help")) {
                            commandSender.sendMessage(prefix + "Please use \"/config remove <path> <value>\"");
                            return true;
                        } else {
                            // if the argument is not "help"
                            return false;
                        }
                    } else {
                        commandSender.sendMessage(prefix + ChatColor.RED + "Not enough arguments!");
                        commandSender.sendMessage(prefix + ChatColor.RED + "Please use \"/config remove <path> <value>\"");
                        return true;
                    }
                }
                // if TOO many arguments were passed
                if (subCommand.getArguments().length > 2) {
                    commandSender.sendMessage(prefix + "Too many arguments given!");
                    commandSender.sendMessage(prefix + ChatColor.RED + "Please use \"/config remove <path> <value>\"");
                    return true;
                }

                // checks if an argument is present and if so,
                // tries to get a boolean, long and at last a string from the argument.
                subCommand.getStringArgument(0).ifPresent(path -> {
                    // Checks if the argument is a boolean
                    if (subCommand.getBooleanArgument(1).isPresent()) {
                        boolean boolValue = subCommand.getBooleanArgument(1).get();
                        List<Boolean> booleanList = Settings.getConfiguration().getBooleanList(path);
                        booleanList.removeAll(Collections.singleton(boolValue));
                        Settings.getConfiguration().set(path, booleanList);
                        // Checks if the argument is a number (long)
                    } else if (subCommand.getLongArgument(1).isPresent()) {
                        long longValue = subCommand.getLongArgument(1).get();
                        List<Long> longList = Settings.getConfiguration().getLongList(path);
                        longList.removeAll(Collections.singleton(longValue));
                        Settings.getConfiguration().set(path, longList);

                        Logging.info(longList.toString());
                        // the last check is, if the argument is a String.
                    } else if (subCommand.getStringArgument(1).isPresent()){
                        String stringValue = subCommand.getStringArgument(1).get();
                        List<String> stringList = Settings.getConfiguration().getStringList(path);
                        stringList.removeAll(Collections.singleton(stringValue));
                        Settings.getConfiguration().set(path, stringList);
                        // If the argument could not be parsed, we throw an error.
                    } else {
                        commandSender.sendMessage(prefix + ChatColor.RED +
                                "An unknown error occurred. Sorry for the inconvenience...");
                    }
                });

                // Send a success message
                return sendSuccessMessage(commandSender, subCommand);
            }
            // No subcommand is given:
            default -> {
                // Return an unsuccessful execution of the command.
                return false;
            }
        }
    }

    /**
     * Sends a success message for the subcommands 'remove' and 'add'
     * @param commandSender The person who initially sent the message.
     * @param subCommand The subcommand the person executed.
     * @return the success of the execution.
     */
    private boolean sendSuccessMessage(CommandSender commandSender, Command subCommand) {
        if (subCommand.getStringArgument(0).isPresent() && subCommand.getStringArgument(1).isPresent()) {
            commandSender.sendMessage(prefix + ChatColor.GREEN +
                    "Added value " + ChatColor.GRAY + subCommand.getStringArgument(0).get() +
                    ChatColor.GREEN + " to " + ChatColor.GRAY +
                    subCommand.getStringArgument(1).get() + ChatColor.GREEN + ".\n" +

                    ChatColor.YELLOW + "Use " + ChatColor.GRAY + "/config save" + ChatColor.YELLOW +
                    " to save the config to the file!"
            );
            // commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, this command is currently not implemented.");
            return true;
        } else {
            return false;
        }
    }
}
