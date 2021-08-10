package com.github.mafelp.minecraft.tabCompleters;

import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HelpTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1)
            return Collections.emptyList();

        List<String> out = new ArrayList<>();

        if (CheckPermission.checkPermission(Permissions.configEdit, sender))
            out.addAll(Arrays.asList("token", "config"));

        out.addAll(Arrays.asList(
                "account",
                "help",
                "link",
                "token",
                "unlink",
                "whisper",
                "dcmsg"
        ));

        return ResultSorter.sortedResults(args[0], out);
    }
}
