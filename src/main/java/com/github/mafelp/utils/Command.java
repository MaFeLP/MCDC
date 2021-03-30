package com.github.mafelp.utils;

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
