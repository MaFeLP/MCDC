package com.github.mafelp.utils;

import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to create new Commands
 */
public class CommandParser {
    /**
     * method for parsing commands and arguments into the command class.
     * @param commandArgumentString command and arguments contained in a string.
     * @throws CommandNotFinishedException if the command has an uneven number of quotation marks.
     * @throws NoCommandGivenException if the input is null.
     * @return the command parsed from the string
     */
    public static Command parseFromString(String commandArgumentString) throws CommandNotFinishedException, NoCommandGivenException {
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
                default -> {
                    currentArgument.append(c);
                    if (characterIndex == commandArgumentString.length())
                        argsList.add(currentArgument.toString());
                }
            } // End of switch
        } // End of for

        // if we are still in an argument, that means that we have an uneven number of of quotation marks.
        // Then throw the exception.
        if (inArgument)
            throw new CommandNotFinishedException("The command is not valid! Still in Parameter!");

        // Create the list of arguments without the command.
        List<String> argumentList = new ArrayList<>();
        for (int i = 1; i < argsList.size(); i++) {
            argumentList.add(argsList.get(i));
        }

        return new Command(argsList.get(0), argumentList.toArray(String[]::new));
    }

    /**
     * method for parsing commands and arguments into the command class.
     * @param inputCommandArray the array to parse the commands from
     * @return the built command
     */
    public static Command parseFromArray(String[] inputCommandArray) {
        if (inputCommandArray == null)
            return new Command(null, null);

        if (inputCommandArray.length == 1)
            return new Command(inputCommandArray[0], null);

        // Create the list of arguments without the command.
        List<String> argumentList =
                new ArrayList<>(Arrays.asList(inputCommandArray).subList(1, inputCommandArray.length));

        return new Command(inputCommandArray[0], argumentList.toArray(String[]::new));
    }
}
