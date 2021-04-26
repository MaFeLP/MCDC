package com.github.mafelp.minecraft.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.accounts.AccountManager;
import com.github.mafelp.utils.Logging;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.github.mafelp.utils.Settings.prefix;
import static org.bukkit.ChatColor.*;

public class Unlink implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        // Only players can have accounts.
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            Optional<Account> accountOptional = Account.getByPlayer(player);

            if (accountOptional.isEmpty()) {
                commandSender.sendMessage(prefix + RED + "You don't have an account ot unlink! Use " + GRAY + "/link" + RED + " to create one.");
                return true;
            }

            Account account = accountOptional.get();

            if (args == null) {
                Logging.info("Removing account " + account.getUsername());
                commandSender.sendMessage(prefix + YELLOW + "Removing the account" + GRAY + account.getUsername() + YELLOW + "...");
                AccountManager.removeAccount(account);
                commandSender.sendMessage(prefix + GREEN + "Successfully removed your account!");
                return true;
            }

            if (args.length == 0) {
                Logging.info("Removing account " + account.getUsername());
                commandSender.sendMessage(prefix + YELLOW + "Removing the account" + GRAY + account.getUsername() + YELLOW + "...");
                AccountManager.removeAccount(account);
                commandSender.sendMessage(prefix + GREEN + "Successfully removed your account!");
                return true;
            }

            return false;
        } else {
            commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, you can only execute this command as a player!");
            return true;
        }
    }
}
