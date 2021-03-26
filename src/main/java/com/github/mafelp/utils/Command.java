package com.github.mafelp.utils;

import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to parse strings to commands.
 */
public class Command {
    /**
     * The arguments of the command as an argument
     */
    private final String[] arguments;
    private final String command;

    /**
     * default constructor for manual construction.
     * @param command the command
     * @param arguments an Array of arguments for the command.
     */
    public Command(String command, String[] arguments)  {
        this.command = command;
        this.arguments = arguments;
    }

    /**
     * Command constructor for parsing commands and arguments into an array.
     * @param commandArgumentString command and arguments contained in a string.
     * @throws CommandNotFinishedException if the command has an uneven number of quotation marks.
     * @throws NoCommandGivenException if the input is null.
     */
    public Command(String commandArgumentString) throws CommandNotFinishedException, NoCommandGivenException {
        // Checks if a command was even given.
        if (commandArgumentString == null)
            throw new NoCommandGivenException("No command was given!");

        // Variables used in the parsing.
        boolean inArgument = false;
        StringBuilder currentArgument = new StringBuilder();
        List<String> argsList = new ArrayList<>();
        int characterIndex = 0;

        // Parsing of the string into an array.
        for (char c :
                commandArgumentString.toCharArray()) {
            characterIndex++;   // increments the character index for checks, if the command has already ended.

            // Handles different characters: quotes, spaces, all the rest.
            switch (c) {
                // space:
                case ' ' -> {
                    // normally space means new argument.
                    // if we are in an Argument, ignore the space.
                    if (inArgument)
                        currentArgument.append(c);
                    else {
                        // Create a new Argument and  add the old one to the list of arguments.
                        argsList.add(currentArgument.toString());
                        currentArgument = new StringBuilder();
                    }
                }
                // quotation marks mark the beginning/end of an argument.
                case '"', '\'' -> {
                    // check if this is the last character, if yes, append the current argument to the list of arguments.
                    if (characterIndex == commandArgumentString.length())
                        argsList.add(currentArgument.toString());
                    // Mark if the following is part of this argument or not.
                    inArgument = !inArgument;
                }
                // For every other character: just add it to the current argument.
                default -> currentArgument.append(c);
            } // End of switch
        } // End of for

        // if we are still in an argument, that means that we have an uneven number of of quotation marks.
        // Then throw the exception.
        if (inArgument)
            throw new CommandNotFinishedException("The command is not valid! Still in Parameter!");

        // Set the command to the first item of the argument string.
        this.command = argsList.get(0);

        // Create the list of arguments without the command.
        List<String> argumentList = new ArrayList<>();
        for (int i = 1; i < argsList.size(); i++) {
            argumentList.add(argsList.get(i));
        }
        this.arguments = argumentList.toArray(String[]::new);
    }

    /**
     * Gets the argument at the index as a string.
     * @param index the index of the argument.
     * @return the value of the index.
     */
    public String getStringArgument(int index) {
        return arguments[index];
    }

    /**
     * Gets the argument at the index as a boolean.
     * @param index index of the argument.
     * @return the value.
     */
    public boolean getBooleanArgument(int index)     {
        return Boolean.getBoolean(arguments[index]);
    }

    /**
     * Gets the argument at the index as a Long/int.
     * @param index index of the argument.
     * @return the value.
     */
    public long getLongArgument(int index) {
        return Long.getLong(arguments[index]);
    }

    /**
     * Gets the argument array.
     * @return the arguments as a string array
     */
    public String[] getArguments() {
        return arguments;
    }

    /**
     * Gets the command - the first argument in the parsed string.
     * @return the command.
     */
    public String getCommand() {
        return command;
    }
}
