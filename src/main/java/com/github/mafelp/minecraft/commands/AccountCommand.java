package com.github.mafelp.minecraft.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.accounts.AccountManager;
import com.github.mafelp.accounts.DiscordLinker;
import com.github.mafelp.utils.*;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static com.github.mafelp.utils.Settings.prefix;
import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.GREEN;

/**
 * The class that is being called on the execution of the command <code>/account</code>, executed as a minecraft player.
 */
public class AccountCommand implements CommandExecutor {
    /**
     * The method that handles the execution of the command.
     * @param commandSender The Player (or console) who executed this command.
     * @param command The command that the Player (or console) executed. In this case: <code>account</code>
     * @param label The label of the command.
     * @param args Additional arguments passed into the command.
     * @return If the command was executed successfully and if not displays the usage message.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
        // Only allows players to execute this command.
        if (commandSender instanceof Player) {
            // If a player executed this command, we can cast the commandSender to a player.
            Player player = (Player) commandSender;

            if (args == null) {
                commandSender.sendMessage(prefix + ChatColor.RED + "No arguments given into the command! Please use the following syntax.");
                return false;
            }

            Logging.info("Player " + player.getDisplayName() + " has executed command \"/account\"... Parsing subcommands...");
            Command cmd;
            try {
                cmd = CommandParser.parseFromArray(args);
            } catch (NoCommandGivenException exception) {
                commandSender.sendMessage(prefix + ChatColor.RED + "No Command given! Please use the following syntax!");
                return false;
            } catch (CommandNotFinishedException exception) {
                commandSender.sendMessage(prefix + ChatColor.RED + "Could not parse your command. There is an uneven number of quotation marks. Maybe try escaping them with \\");
                return true;
            }

            switch (cmd.getCommand().toLowerCase(Locale.ROOT)) {
                case "link" -> {
                    Logging.debug("Subcommand is link.");
                    // Initiates the linking process.
                    Optional<Integer> optionalLinkID = cmd.getIntegerArgument(0);

                    // If no link token was given, create one and send it to the player.
                    if (optionalLinkID.isEmpty()) {
                        Logging.debug("Link ID is empty... Sending link token.");
                        Link.sendLinkToken(player);
                        return true;
                    }

                    Logging.debug("Linking accounts...");
                    // If a link token was given, try to link the accounts with the token.
                    Optional<Account> linkedAccount = DiscordLinker.linkToMinecraft(player, optionalLinkID.get());

                    // If the account is empty, the linkToken was invalid. Inform the user.
                    if (linkedAccount.isEmpty()) {
                        commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, your Link token is invalid. Please try again or use " + ChatColor.GRAY + "/link" + ChatColor.RESET + " to get a link token and instructions.");
                        Logging.info("Linking failed: Link token is invalid!");
                        return true;
                    }

                    // If the account isPresent, the token was valid and an account was being created.
                    commandSender.sendMessage(prefix + ChatColor.GREEN + "Successfully linked this player account to the discord user " + linkedAccount.get().getUsername());
                    Logging.info("Linked Minecraft account " + linkedAccount.get().getPlayer().getName() + " to Discord user " + linkedAccount.get().getUser().getName() + "\"");
                    return true;
                }
                // Sets you username
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

                    Logging.debug("Player " + player.getName() + " requested a name change.");
                    // Validate the inputted string
                    String inputName = cmd.getStringArgument(0).get();

                    Logging.debug("Checking for wrong characters in requested name.");
                    char[] chars = inputName.toCharArray();
                    char[] forbiddenCharacters = {'\\', ' ', '"', '\'', '<', '>', '#', '!', '$', '%', '^', '&', '*', '(', ')', '+', '=', '[', ']', '{', '}', ';', ':', '?', '/'};

                    int i = 0;
                    for (char c : chars) {
                        if (c == '@' && i != 0) {
                            commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, you are not allowed to have \"@\" in your name!");
                            return true;
                        }

                        for (char forbidden : forbiddenCharacters) {
                            if (forbidden == c) {
                                commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, you are not allowed to have these characters in your name: \\ \"'<>#!@$%^&*()_/+=[]{}|");
                            }
                        }

                        i++;
                    }
                    Logging.debug("The requested name for " + player.getName() + " has passed the character check!");

                    // If the first character is an @, do not add one to the start of the name.
                    String nameToSet;
                    if (chars[0] == '@')
                        nameToSet = inputName;
                    else
                        nameToSet = '@' + inputName;

                    // Checks if the input name meets the specific requirements.
                    Logging.debug("Checking for name length...");
                    if (nameToSet.length() >= 33) {
                        Logging.debug("Name did not pass the length check!");
                        commandSender.sendMessage(prefix + ChatColor.RED + "Your name is too long! The limit is 32 characters!");
                        return true;
                    }

                    // Checks if the name is taken by anybody else
                    Logging.debug("Checking if the name is a duplicate of another name.");
                    for (Account a : AccountManager.getLinkedAccounts()) {
                        if (a.getUsername().equalsIgnoreCase(nameToSet)) {
                            Logging.debug("Name failed the duplicate name check!");
                            commandSender.sendMessage(prefix + ChatColor.RED + "Could not set name: Name is already taken!");
                            return true;
                        }
                    }
                    Logging.debug("Name has passed the duplicate name check!");

                    // Checks if the name is a minecraft name, of a player, who already was online.
                    Logging.debug("Checking if the name is a minecraft user.");
                    for (OfflinePlayer offlinePlayer : Settings.minecraftServer.getOfflinePlayers()) {
                        if (nameToSet.equalsIgnoreCase("@" + offlinePlayer.getName())) {
                            Logging.debug("Name failed the minecraft name check!");
                            commandSender.sendMessage(prefix + ChatColor.RED + "Could not set name: Name is a minecraft name!");
                            return true;
                        }
                    }
                    Logging.debug("Name passed the minecraft username check!");

                    // Gets the account and sets its username.
                    Account account = Account.getByPlayer(player).get();
                    Logging.info("Player " + player.getName() + " changed its username from " + account.getUsername() + " to " + nameToSet + ".");
                    account.setUsername(nameToSet);
                    commandSender.sendMessage(prefix + "Your username has been set to " + Account.getByPlayer(player).get().getUsername());
                    return true;
                }
                // The subcommand used to get your/another players account name.
                case "get" -> {
                    Logging.debug("Player " + commandSender.getName() + " executed subcommand \"get\".");
                    // If no additional arguments were passed, give the player his/her account name.
                    if (cmd.getStringArgument(0).isEmpty()) {
                        if (Account.getByPlayer(player).isEmpty()) {
                            Logging.debug("Player " + ChatColor.GRAY + player.getName() + ChatColor.RESET + " has requested his account name: He/She doesn't have one. Sending help message.");
                            commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, you do not have an account. Use " + ChatColor.GRAY + "/link" + ChatColor.RED + " to get one.");
                            return true;
                        }
                        Logging.debug("Player " + ChatColor.GRAY + player.getName() + ChatColor.RESET + " has requested his account name.");
                        commandSender.sendMessage(prefix + ChatColor.GREEN + "Your account name is: " + ChatColor.GRAY + Account.getByPlayer(player).get().getUsername());
                        // If additional Arguments are passed, get the name of the accounts.
                    } else {
                        Logging.debug("Offline Players:");
                        // Checks all players that were online at least once and if they have an account.
                        for (OfflinePlayer p : Settings.minecraftServer.getOfflinePlayers()) {
                            Logging.debug("`--> " + p.getName());
                            // Check if the name of the player equals the requested name.
                            if (Objects.equals(p.getName(), cmd.getStringArgument(0).get())) {
                                // Get the account for the requested Player.
                                Optional<Account> requestedAccount = Account.getByPlayer(p);

                                // If the player does not have an account, send an error message.
                                if (requestedAccount.isEmpty()) {
                                    commandSender.sendMessage(prefix + ChatColor.RED + "Player " + p.getName() + " doesn't have an account.");
                                    Logging.debug("Player " + player.getName() + " tried to get the account name for " + cmd.getStringArgument(0).get() + ", but he/she does not have an account!");
                                } else {
                                    Logging.debug("Player " + player.getName() + " got the account name for player " + cmd.getStringArgument(0).get() + ". It is: " + requestedAccount.get().getUsername());
                                    commandSender.sendMessage(prefix + ChatColor.GREEN + "The account name for player " + ChatColor.GRAY + p.getName() + ChatColor.GREEN + " is: " + ChatColor.GRAY + requestedAccount.get().getUsername());
                                }
                                return true;
                            }
                        }

                        // If no player could be found, send the player an error message and exit.
                        commandSender.sendMessage(prefix + ChatColor.RED + "Player with the name " + ChatColor.GRAY + cmd.getStringArgument(0).get() + ChatColor.GRAY + " does not exist!");
                    }
                    return true;
                }
                // Removes an account, if the player has the required permissions.
                case "remove" -> {
                    // If the user does not have te required permissions, exit.
                    if (incidentReport(commandSender, cmd)) return true;

                    // Checks if enough arguments were passed.
                    if (cmd.getStringArgument(0).isEmpty()) {
                        commandSender.sendMessage(prefix + ChatColor.RED + "Not enough arguments given!");
                        Logging.debug("Player " + ChatColor.DARK_GRAY + commandSender.getName() + ChatColor.RESET + " tried to execute the command " + ChatColor.DARK_GRAY + "account remove " + Arrays.toString(cmd.getArguments()) + ChatColor.RESET + "! The command did not have enough/too much arguments!");
                        return false;
                    }
                    if (cmd.getStringArgument(0).get().equals("")) {
                        commandSender.sendMessage(prefix + ChatColor.RED + "Not enough arguments given!");
                        Logging.debug("Player " + ChatColor.DARK_GRAY + commandSender.getName() + ChatColor.RESET + " tried to execute the command " + ChatColor.DARK_GRAY + "account remove " + Arrays.toString(cmd.getArguments()) + ChatColor.RESET + "! The command did not have enough/too much arguments!");
                        return false;
                    }

                    // Iterates over all players to find the one that matches the name given as the first argument to
                    // the subcommand. If the names match, it removes the account. If no account could be found it
                    // returns an error.
                    OfflinePlayer[] players = Settings.minecraftServer.getOfflinePlayers();
                    for (OfflinePlayer p : players) {
                        if (Objects.equals(p.getName(), cmd.getStringArgument(0).get())) {
                            if (Account.getByPlayer(p).isPresent()) {
                                String username = Account.getByPlayer(p).get().getUsername();
                                AccountManager.removeAccount(Account.getByPlayer(p).get());
                                commandSender.sendMessage(prefix + ChatColor.GREEN + "Successfully removed account with username: " + username);
                                Logging.info("Player " + commandSender.getName() + "remove the account with the name " + ChatColor.GRAY + username + ChatColor.RESET + ".");
                            } else {
                                commandSender.sendMessage(prefix + ChatColor.RED + "Account with the Minecraft name: " + ChatColor.GRAY + cmd.getStringArgument(0).get() + ChatColor.RED + " does not exist!");
                            }
                            return true;
                        }
                    }

                    // If the player whose name was passed, does not exist, return an error.
                    commandSender.sendMessage(prefix + ChatColor.RED + "Player with the name " + ChatColor.GRAY + cmd.getStringArgument(0).get() + ChatColor.RED + " was never on this server!");
                    return true;
                }
                // The subcommand used to save the accounts file.
                case "save" -> {
                    // If the user does not have te required permissions, exit.
                    if (incidentReport(commandSender, cmd)) return true;

                    try {
                        commandSender.sendMessage(prefix + ChatColor.YELLOW + "Saving accounts file...");
                        Logging.info("Player " + ChatColor.DARK_GRAY + commandSender.getName() + ChatColor.RESET + " is saving the configuration file.");
                        AccountManager.saveAccounts();
                        Logging.info(ChatColor.GREEN + "Successfully saved the accounts file!");
                        commandSender.sendMessage(prefix + ChatColor.GREEN + "Successfully saved the accounts file!");
                    } catch (FileNotFoundException e) {
                        Logging.logIOException(e, "Error saving the account file! (Author of this save: " + commandSender.getName());
                        commandSender.sendMessage(prefix + ChatColor.RED + "A FileNotFoundException occurred! Please see the console for more details!");
                    }
                    return true;
                }
                // The subcommand used to reload the accounts file into memory.
                case "reload" -> {
                    // If the user does not have te required permissions, exit.
                    if (incidentReport(commandSender, cmd)) return true;

                    // Try to reload the accounts
                    try {
                        commandSender.sendMessage(prefix + ChatColor.YELLOW + "Reloading the accounts file... Warning! Only edit the file, if you know what you are doing!");
                        AccountManager.loadAccounts();
                        commandSender.sendMessage(prefix + ChatColor.GREEN + "Successfully reloaded the accounts file!");
                    } catch (IOException e) {
                        Logging.logIOException(e, "Error reloading the accounts file (Author of this command: " + commandSender.getName() + ").");
                        commandSender.sendMessage(prefix + ChatColor.RED + "adf");
                    }

                    return true;
                }
                // The subcommand that lists all currently linked accounts
                case "list" -> {
                    boolean allowed = Settings.getConfiguration().getBoolean("allowListAllAccounts");

                    if (CheckPermission.checkPermission(Permissions.accountEdit, commandSender))
                        allowed = true;

                    if (allowed) {
                        String sb = "";

                        sb += prefix + ChatColor.GREEN + "--------Linked Accounts--------\n" + ChatColor.RESET;
                        sb += ChatColor.DARK_GRAY +      "+================================================+\n" + ChatColor.RESET;
                        sb += ChatColor.YELLOW +         "Minecraft Name; Discord Username; Discord Ping Tag\n" + ChatColor.RESET;
                        sb += ChatColor.DARK_GRAY +      "+================================================+\n" + ChatColor.RESET;

                        for (Account a : AccountManager.getLinkedAccounts())
                            sb += ChatColor.AQUA + a.getPlayer().getName() + "; " + a.getUser().getName() + "; " + a.getUsername() + "\n";

                        sb += ChatColor.DARK_GRAY +      "+================================================+" + ChatColor.RESET;

                        commandSender.sendMessage(sb);
                    } else {
                        commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, the Server administrator has disabled the listing of accounts!");
                    }

                    return true;
                }
                // The subcommand that unlinks your account.
                case "unlink" -> {
                    Optional<Account> accountOptional = Account.getByPlayer(player);

                    if (accountOptional.isEmpty()) {
                        commandSender.sendMessage(prefix + RED + "You don't have an account ot unlink! Use " + GRAY + "/link" + RED + " to create one.");
                        return true;
                    }

                    Account account = accountOptional.get();

                    if (cmd.getStringArgument(0).isEmpty()) {
                        Logging.info("Removing account " + account.getUsername());
                        commandSender.sendMessage(prefix + YELLOW + "Removing the account" + GRAY + account.getUsername() + YELLOW + "...");
                        AccountManager.removeAccount(account);
                        commandSender.sendMessage(prefix + GREEN + "Successfully removed your account!");
                        return true;
                    }

                    return false;
                }
                // If no subcommand was specified.
                default -> {
                    return false;
                }
            }
            // If the command was not executed by a player.
        } else {
            commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, this command can only be executed by a player.");
            return true;
        }
    }

    /**
     * The method used to check the {@link com.github.mafelp.utils.Permissions}: {@code accountEdit} of a command Sender.
     * @param commandSender The command sender to check the permission of.
     * @param cmd The command that will be shown into the console, if the permission is denied.
     * @return If the permission was granted.
     */
    private static boolean incidentReport(@NotNull CommandSender commandSender, Command cmd) {
        if (!CheckPermission.checkPermission(Permissions.accountEdit, commandSender)) {
            commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, you don't have the required permissions, to execute this command!\n" +
                    prefix + "This incident will be reported!");
            Logging.info("Player " + ChatColor.DARK_GRAY + commandSender.getName() + ChatColor.RESET + " tried to execute the command " + ChatColor.DARK_GRAY + "account remove " + Arrays.toString(cmd.getArguments()) + ChatColor.RESET + "! This action was denied due to missing permission!");
            return true;
        }
        return false;
    }
}
