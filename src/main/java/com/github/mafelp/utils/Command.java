package com.github.mafelp.utils;

import java.util.Optional;

/**
 * Class to parse strings to commands.
 */
public class Command {
    /**
     * The arguments of the command as an argument
     */
    private final String[] arguments;
    /**
     * The command aka. the first argument passed in.
     */
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
     * Gets the argument at the index as a string.
     * @param index the index of the argument.
     * @return the value of the index.
     */
    public Optional<String> getStringArgument(int index) {
        if (argumentIsAvailable(index))
            return Optional.of(arguments[index]);
        else {
            Logging.info("not available");
            return Optional.empty();
        }
    }

    /**
     * Gets the argument at the index as a boolean.
     * @param index index of the argument.
     * @return the value.
     */
    public Optional<Boolean> getBooleanArgument(int index) {
        if (this.getStringArgument(index).isPresent()) {
            String arg = this.getStringArgument(index).get();

            if (arg.equalsIgnoreCase("true"))
                return Optional.of(true);
            else if (arg.equalsIgnoreCase("false"))
                return Optional.of(false);
            else return Optional.empty();
        } else
            return Optional.empty();
    }

    /**
     * Gets the argument at the index as a Long/int.
     * @param index index of the argument.
     * @return the value.
     */
    public Optional<Long> getLongArgument(int index) {
        if (argumentIsAvailable(index))
            // Prevents an abort when not a long was passed.
            try {
                return Optional.of(Long.parseLong(arguments[index]));
            } catch (NumberFormatException numberFormatException) {
                return Optional.empty();
            }
        return Optional.empty();
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

    /**
     * Checks if the index to get the argument from is present. - checking this before trying to get the
     * value of an index helps preventing an array out of bound exception.
     * @param index the index to check if it is present
     * @return success state - is the index available, yes/no
     */
    private boolean argumentIsAvailable(int index) {
        if (arguments == null)
            return false;

        return index < arguments.length;
    }
}
