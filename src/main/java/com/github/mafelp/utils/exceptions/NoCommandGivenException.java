package com.github.mafelp.utils.exceptions;

/**
 * The Exception thrown if no command is passed into the command parser.
 */
public class NoCommandGivenException extends Exception{
    /**
     * The constructor to create the throwable.
     * @param message The message that is being displayed in non-debug mode.
     */
    public NoCommandGivenException(String message) {
        super(message);
    }
}
