package com.github.mafelp.minecraft.tabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * The method that handles all the tab completion for the /whisper and /dcmsg commands.
 */
public class UnlinkTabCompleter implements TabCompleter {
    /**
     * The Method that handles the getting of of the results.
     * @param sender The sender that is typing the command.
     * @param cmd The command that the sender is trying to type.
     * @param alias The first argument of the command, which also can be an alias.
     * @param args The arguments that are provided until now.
     * @return A list of available results for this command.
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
