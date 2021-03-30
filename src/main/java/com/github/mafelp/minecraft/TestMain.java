package com.github.mafelp.minecraft;

import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;

import java.util.Arrays;

public class TestMain {
    public static void main(String[] args) {
        try {
            Command command = CommandParser.parseFromString(".createChannel test");
            System.out.println(command.getCommand());
            System.out.println(Arrays.toString(command.getArguments()));
        } catch (CommandNotFinishedException | NoCommandGivenException e) {
            e.printStackTrace();
        }
    }
}
