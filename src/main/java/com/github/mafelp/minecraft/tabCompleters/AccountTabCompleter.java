package com.github.mafelp.minecraft.tabCompleters;

import com.github.mafelp.accounts.AccountManager;
import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.mafelp.minecraft.tabCompleters.ResultSorter.sortedResults;

/**
 * The method that handles all the tab completion for the /account commands
 */
public class AccountTabCompleter implements TabCompleter {
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
        List<String> results;

        switch (args.length) {
            case 1 -> {
                results = new ArrayList<>(Arrays.asList(
                        "get",
                        "list"
                ));

                if (sender instanceof Player) {
                    results.addAll(Arrays.asList(
                            "link",
                            "unlink",
                            "name", "username"
                    ));

                    if (CheckPermission.checkPermission(Permissions.accountEdit, (Player) sender)) {
                        results.addAll(Arrays.asList(
                                "remove",
                                "save",
                                "reload"
                        ));
                    }
                } else {
                    results.addAll(Arrays.asList(
                            "remove",
                            "save",
                            "reload"
                    ));
                }

                // return results;
                return sortedResults(args[0], results);
            }
            case 2 -> {
                switch (args[0]) {
                    case "remove" -> {
                        if (CheckPermission.checkPermission(Permissions.accountEdit, (Player) sender))
                            return sortedResults(args[1], AccountManager.getAllMinecraftAccountNames());
                        else
                            return new ArrayList<>();
                    }
                    case "get" -> {
                        return sortedResults(args[1], AccountManager.getAllMinecraftAccountNames());
                    }
                    case "link" -> {
                        return Collections.singletonList("(<TOKEN>)");
                    }
                    case "name", "username" -> {
                        return Collections.singletonList("<NAME>");
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
