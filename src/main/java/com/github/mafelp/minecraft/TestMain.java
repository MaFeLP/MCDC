package com.github.mafelp.minecraft;

import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;

import java.util.Arrays;

public class TestMain {
    public static void main(String[] args) {
            String[] asdf = {"set", "asdf", "true", "1234"};
        // try {
        System.out.println(Arrays.toString(asdf));
            Command command = CommandParser.parseFromArray(asdf);
            System.out.println(command.getCommand());
            System.out.println(command.getStringArgument(0).get());
            System.out.println(command.getBooleanArgument(1).get());
            System.out.println(command.getLongArgument(2).get());
        //} catch (CommandNotFinishedException | NoCommandGivenException e) {
          //  e.printStackTrace();
        //}
    }
}
