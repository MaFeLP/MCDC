package com.github.mafelp.minecraft.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.accounts.AccountManager;
import com.github.mafelp.accounts.DiscordLinker;
import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.github.mafelp.utils.Settings.prefix;

public class AccountCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player) {

            Player player = (Player) commandSender;

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

            if (cmd.getArguments() == null) {
                commandSender.sendMessage(prefix + ChatColor.RED + "Could not parse your command. There is an uneven number of quotation marks. Maybe try escaping them with \\");
                return true;
            }

            if (cmd.getArguments().length == 0) {
                commandSender.sendMessage(prefix + ChatColor.RED + "Could not parse your command. There is an uneven number of quotation marks. Maybe try escaping them with \\");
                return true;
            }

            switch (cmd.getCommand().toLowerCase()) {
                case "link" -> {
                    Optional<Integer> optionalLinkID = cmd.getIntegerArgument(0);

                    if (optionalLinkID.isEmpty()) {
                        Link.sendLinkToken(player);
                        return true;
                    }

                    Optional<Account> linkedAccount = DiscordLinker.linkToMinecraft(player, optionalLinkID.get());

                    if (linkedAccount.isEmpty()) {
                        Link.sendLinkToken(player);
                        return true;
                    }

                    commandSender.sendMessage(prefix + ChatColor.GREEN + "Successfully linked this player account to the discord user " + linkedAccount.get().getUsername());

                    return true;

                }
                case "name", "username" -> {
                    if (Account.getByPlayer(player).isEmpty()) {
                        commandSender.sendMessage(prefix + ChatColor.RED + "No discord account account if linked to you. Use " + ChatColor.GRAY + "/link" + ChatColor.RED + " to get a link token and link your minecraft account to your discord account.");
                        return true;
                    }

                    if (cmd.getStringArgument(0).isEmpty()) {
                        commandSender.sendMessage(prefix + "Your account name is: " + Account.getByPlayer(player).get().getUsername());
                        return true;
                    }

                    String nameToSet = cmd.getStringArgument(0).get();

                    Account account = Account.getByPlayer(player).get();

                    account.setUsername(nameToSet);

                    commandSender.sendMessage(prefix + "Your username has been set to " + Account.getByPlayer(player).get().getUsername());

                    return true;
                }
                default -> {
                    return false;
                }
            }
        } else {
            return true;
        }
    }
}
