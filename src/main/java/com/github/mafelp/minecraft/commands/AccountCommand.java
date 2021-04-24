package com.github.mafelp.minecraft.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.accounts.AccountManager;
import com.github.mafelp.accounts.DiscordLinker;
import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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

            switch (cmd.getCommand().toLowerCase()) {
                case "link" -> {
                    Optional<Integer> optionalLinkID = cmd.getIntegerArgument(0);

                    if (optionalLinkID.isEmpty()) {
                        Link.sendLinkToken(player);
                        return true;
                    }

                    Optional<Account> linkedAccount = DiscordLinker.linkToMinecraft(player, optionalLinkID.get());

                    if (linkedAccount.isEmpty()) {
                        commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, your Link token is invalid. Please try again or use " + ChatColor.GRAY + "/link" + ChatColor.RESET + " to get a link token and instructions.");
                        return true;
                    }

                    commandSender.sendMessage(prefix + ChatColor.GREEN + "Successfully linked this player account to the discord user " + linkedAccount.get().getUsername());

                    return true;

                }
                case "name", "username" -> {
                    Logging.debug("Minecraft user " + player.getName() + " used the command \"/account " + cmd.getCommand() + " " + Arrays.toString(cmd.getArguments()));
                    if (Account.getByPlayer(player).isEmpty()) {
                        commandSender.sendMessage(prefix + ChatColor.RED + "No discord account account if linked to you. Use " + ChatColor.GRAY + "/link" + ChatColor.RED + " to get a link token and link your minecraft account to your discord account.");
                        return true;
                    }

                    if (cmd.getStringArgument(0).isEmpty()) {
                        Logging.info(ChatColor.GRAY + player.getName() + ChatColor.RESET + " requested his account name.");
                        commandSender.sendMessage(prefix + "Your account name is: " + Account.getByPlayer(player).get().getUsername());
                        return true;
                    }

                    // Validate the inputted string
                    String inputName = cmd.getStringArgument(0).get();

                    char[] chars = inputName.toCharArray();

                    int i = 0;
                    for (char c : chars) {
                        if (c == '@' && i != 0) {
                            commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, you are not allowed to have \"@\" in your name!");
                            return true;
                        }
                        if (c == ' ') {
                            commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, you are not allowed to have spaces in your name!");
                            return true;
                        }
                        i++;
                    }

                    // If the first character is an @, do not add one to the start of the name.
                    String nameToSet;
                    if (chars[0] == '@')
                        nameToSet = inputName;
                    else
                        nameToSet = '@' + inputName;

                    // Gets the account and sets its username.
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
