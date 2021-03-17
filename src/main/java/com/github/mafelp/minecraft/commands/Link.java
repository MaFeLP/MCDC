package com.github.mafelp.minecraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.github.mafelp.Settings.prefix;

/**
 * Class handling the linking of minecraft and discord accounts -
 *
 * Warning This class in currently not implemented.
 */
public class Link implements CommandExecutor {
    /**
     * The Method called when command "/token" is executed.
     *
     * @param sender  The sender of the command
     * @param command the command he/she used
     * @param label the label of the command
     * @param args additional arguments passed: Discord ID and unique identifier.
     * @return command success
     */
    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Sending the player a message that this command is currently not available
        sender.sendMessage(prefix + ChatColor.RED + "This command is currently not available!");

        // returning a successful command execution
        return true;
    }
}