package com.github.mafelp.utils.exceptions;

public class CommandNotFinishedException extends Exception{
    public CommandNotFinishedException() {
    }

    public CommandNotFinishedException(String message) {
        super(message);
    }
}
