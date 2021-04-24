package com.github.mafelp.minecraft.commands;

import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.github.mafelp.utils.Settings.prefix;

public class AccountCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
        if (args == null)
            return false;

        Command cmd;
        try {
            cmd = CommandParser.parseFromArray(args);
        } catch (NoCommandGivenException exception) {
            return false;
        } catch (CommandNotFinishedException exception) {
            commandSender.sendMessage(prefix + ChatColor.RED + "Could not parse your command. There is an uneven number of quotation marks. Maybe try escaping them with \\");
            return true;
        }

        switch (cmd.getCommand().toLowerCase()) {
            case "link" -> {

            }
            case "name", "username" -> {

            }
            default -> {
                return false;
            }
        }

        return false;
    }
}
