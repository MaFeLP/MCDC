package com.github.mafelp.utils.exceptions;

/**
 * The exception class that will be thrown when a command has not ended yet,
 * aka. there is an uneven number of quotation marks.
 */
public class CommandNotFinishedException extends Exception{
    /**
     * The exception that is being created and thrown.
     * @param message The message that should be displayed in non-debug-mode.
     */
    public CommandNotFinishedException(String message) {
        super(message);
    }
}
