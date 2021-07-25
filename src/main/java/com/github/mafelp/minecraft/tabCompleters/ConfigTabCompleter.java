package com.github.mafelp.minecraft.tabCompleters;

import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Permissions;
import com.github.mafelp.utils.Settings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.github.mafelp.minecraft.tabCompleters.ResultSorter.sortedResults;

/**
 * The method that handles all the tab completion for the /config command.
 */
public class ConfigTabCompleter implements TabCompleter {
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
        if (sender instanceof Player)
            if (!CheckPermission.checkPermission(Permissions.configEdit, (Player) sender))
                return new ArrayList<>();

        switch (args.length) {
            case 1 -> {
                return Arrays.asList(
                        "reload",
                        "save",
                        "default",
                        "set",
                        "get",
                        "add",
                        "remove"
                );
            }
            case 2 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "set", "get", "add", "remove" -> {
                        return sortedResults(args[1], new ArrayList<>(Settings.getConfiguration().getKeys(true)));
                    }
                    default -> {
                        return new ArrayList<>();
                    }
                }
            }
            case 3 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "set", "add" -> {
                        return Collections.singletonList("<VALUE>");
                    }
                    case "remove" -> {
                        return sortedResults(args[2], Settings.getConfiguration().getStringList(args[2]));
                    }
                    default -> {
                        return new ArrayList<>();
                    }
                }
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }
}
