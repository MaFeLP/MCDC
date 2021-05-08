package com.github.mafelp.minecraft.commands;

import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Whisper implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        commandSender.sendMessage(Settings.prefix + ChatColor.RED + "Sorry, this command is currently not available!");
        return true;
    }
}
