package com.github.mafelp.minecraft.commands;

import com.github.mafelp.minecraft.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import static com.github.mafelp.Settings.prefix;

public class Link implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(prefix + ChatColor.RED + "This command is currently not available!");
        return true;
    }
}
