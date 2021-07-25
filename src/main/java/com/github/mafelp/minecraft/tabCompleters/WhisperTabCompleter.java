package com.github.mafelp.minecraft.tabCompleters;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.utils.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.github.mafelp.minecraft.tabCompleters.ResultSorter.sortedResults;

public class WhisperTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        final List<String> results = new ArrayList<>();

        if (args.length == 1) {
            for (OfflinePlayer player : Settings.minecraftServer.getOfflinePlayers()) {
                Account.getByPlayer(player).ifPresent(account -> {
                    results.add(account.getUsername());
                    results.add(account.getPlayer().getName());
                });
            }
        } else {
            return new ArrayList<>();
        }

        // return results;
        return sortedResults(args[0], results);
    }
}
