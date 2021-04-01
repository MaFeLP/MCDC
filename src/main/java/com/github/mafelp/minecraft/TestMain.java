package com.github.mafelp.minecraft;

import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;
import com.github.mafelp.utils.Settings;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;

import java.util.Arrays;

import static com.github.mafelp.utils.Settings.prefix;
import static java.lang.System.out;

public class TestMain {
    public static void main(String[] args) {
            String[] asdf = {"set", "asdf",  "fghxfg"};

            Command subCommand1 = CommandParser.parseFromArray(asdf);

            out.println("cmd: " + subCommand1.getCommand());

            for (int i = 0; i < 3; i++) {

                if (subCommand1.getArguments() == null) {
                    System.exit(1);
                }

                // if ONE additional argument was passed
                if (subCommand1.getArguments().length <= 1) {
                    // if only one argument was passed.
                    if (subCommand1.getStringArgument(0).isPresent() && subCommand1.getStringArgument(1).isEmpty()) {
                        if (subCommand1.getStringArgument(0).get().equalsIgnoreCase("help")) {
                            out.println("Please use \"/config set <path> <value>\"");
                            System.exit(0);
                        } else {
                            // if the argument is not "help"
                            System.exit(1);
                        }
                    } else {
                        out.println( "Not enough arguments!");
                        out.println( "Please use \"/config set <path> <value>\"");
                        System.exit(2);
                    }
                }
                // if TOO many arguments were passed
                if (subCommand1.getArguments().length > 2) {
                    out.println("Too many arguments given!");
                    out.println( "Please use \"/config set <path> <value>\"");
                    System.exit(2);
                }

                // checks if an argument is present and if so,
                // tries to get a boolean, long and at last a string from the argument.
                subCommand1.getStringArgument(0).ifPresent(path -> {
                    // Checks if the argument is a boolean
                    if (subCommand1.getBooleanArgument(1).isPresent()) {
                        boolean boolValue = subCommand1.getBooleanArgument(1).get();
                        //Settings.getConfiguration().set(path, boolValue);
                        // Checks if the argument is a number (long)
                        out.println("bool" + boolValue);
                    } else if (subCommand1.getLongArgument(1).isPresent()) {
                        long longValue = subCommand1.getLongArgument(1).get();
                        //Settings.getConfiguration().set(path, longValue);
                        // the last check is, if the argument is a String.
                        out.println("Long: " + longValue);
                    } else if (subCommand1.getStringArgument(1).isPresent()){
                        String stringValue = subCommand1.getStringArgument(1).get();
                        //Settings.getConfiguration().set(path, stringValue);
                        // If the argument could not be parsed, we throw an error.
                        out.println("String: " + stringValue);
                    } else {
                        out.println(
                                "An unknown error occurred. Sorry for the inconvenience...");
                    }
                });

                // Send a success message
                if (subCommand1.getStringArgument(0).isPresent() && subCommand1.getStringArgument(1).isPresent()) {
                    out.println(
                            "Set value " + ChatColor.GRAY + subCommand1.getStringArgument(0).get() +
                            ChatColor.GREEN + " to " + ChatColor.GRAY +
                            subCommand1.getStringArgument(1).get() + ChatColor.GREEN + ".\n" +

                            ChatColor.YELLOW + "Use " + ChatColor.GRAY + "/config save" + ChatColor.YELLOW +
                            " to save the config to the file!"
                    );
                    // commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, this command is currently not implemented.");
                }
            }
    }
}
