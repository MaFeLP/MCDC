package com.github.mafelp.utils.exceptions;

public class NoCommandGivenException extends Exception{
    public NoCommandGivenException() {
    }

    public NoCommandGivenException(String message) {
        super(message);
    }
}
