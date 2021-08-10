package com.github.mafelp.minecraft.commands;

import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Permissions;
import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.github.mafelp.utils.Logging.info;

public class Help implements CommandExecutor {
    private static final String spacerLine = ChatColor.GREEN + "+--------------------" + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "MCDC" + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + "HELP" + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + "--------------------+\n";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String out = spacerLine + "";

        if (args.length == 0) {
            out += normalHelpMessage(sender);
            info("\"" + sender.getName() + "\" executed command \"/help\"; Result: Normal help message.");
        } else {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "account" -> out += accountHelp(sender instanceof Player, CheckPermission.checkPermission(Permissions.accountEdit, sender));
                case "config" -> {
                    if (CheckPermission.checkPermission(Permissions.configEdit, sender))
                        out += configHelp();
                    else {
                        sender.sendMessage(
                                Settings.prefix + ChatColor.RED + "Sorry, you don't have the permission to edit the config,\n" +
                                        Settings.prefix + "So the help would be of no use for you!"
                        );
                        return true;
                    }
                }
                case "help" -> out += ChatColor.RESET + " Displays information about all available commands.";
                case "link" -> {
                }
                case "token" -> {
                }
                case "unlink" -> {
                }
                case "whisper", "dcmsg" -> {
                }
                default -> {
                    out += ChatColor.RED + "Unknown command: \"" + ChatColor.GRAY + args[0] + ChatColor.RED + "\"! See below for a full list!\n";
                    out += normalHelpMessage(sender);
                    info("\"" + sender.getName() + "\" executed command \"/help\"; Result: Normal help message.");
                }
            }
        }

        out += spacerLine;
        sender.sendMessage(out);

        return true;
    }

    private static @NotNull String normalHelpMessage(CommandSender sender) {
        // Message for admin users:
        /*
         * +--------------------------[MCDC/HELP]--------------------------+
         *  General command help. Use "/mcdc:help <COMMAND>"
         *  for more information on a specific command.
         *
         *  Commands:
         *  |-> /account   -> Manage accounts/get account info
         *  |-> /config    -> Configure this plugin
         *  |-> /link      -> Link your minecraft to discord
         *  |-> /mcdc:help -> Lists this help
         *  |-> /dcmsg     -> Sends a private message to a discord user
         *  |-> /token     -> Sets the token for the discord bot
         *  |-> /unlink    -> Unlinks your minecraft and discord accounts
         *  `-> /whisper   -> Sends a private message to a discord user
         * +--------------------------[MCDC/HELP]--------------------------+
         */

        // Message for normal users:
        /*
         * +--------------------------[MCDC/HELP]--------------------------+
         *  General command help. Use "/mcdc:help <COMMAND>"
         *  for more information on a specific command.
         *
         *  Commands:
         *  |-> /account   -> Manage accounts/get account info
         *  |-> /link      -> Link your minecraft to discord
         *  |-> /mcdc:help -> Lists this help
         *  |-> /dcmsg     -> Sends a private message to a discord user
         *  |-> /unlink    -> Unlinks your minecraft and discord accounts
         *  `-> /whisper   -> Sends a private message to a discord user
         * +--------------------------[MCDC/HELP]--------------------------+
         */

        String out = ChatColor.RED + " If you are searching for the vanilla help, use \"" + ChatColor.GRAY + "/minecraft:help" + ChatColor.RED + "\"!\n\n"
                + ChatColor.AQUA + " General command help. Use \"" + ChatColor.GRAY + "/mcdc:help <COMMAND>" + ChatColor.AQUA + "\"\n"
                + " for more information on a specific command.\n\n"
                + ChatColor.GREEN + ChatColor.UNDERLINE + "Commands" + ChatColor.RESET + ChatColor.DARK_GRAY + ":\n"
                + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "/account" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Manage accounts/get account info\n";

        if (!(sender instanceof Player) || CheckPermission.checkPermission(Permissions.configEdit, sender))
            out += ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "/config" + ChatColor.GRAY + "     -> " + ChatColor.BLUE + "Configure this plugin\n";

        if (sender instanceof Player)
            out += ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "/link" + ChatColor.GRAY + "         -> " + ChatColor.BLUE + "Link your minecraft to discord\n";

        out += ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "/mcdc:help" + ChatColor.GRAY + " -> " + ChatColor.BLUE + "Shows this page\n";
        out += ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "/dcmsg" + ChatColor.GRAY + "     -> " + ChatColor.BLUE + "Sends a private message to a discord user\n";

        if (sender instanceof Player)
            out += ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "/unlink" + ChatColor.GRAY + "      -> " + ChatColor.BLUE + "Removes your account\n";

        out += ChatColor.GRAY + " `-> " + ChatColor.DARK_AQUA + "/whisper" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Sends a private message to a discord user\n";

        return out;
    }

    @Contract(pure = true)
    private static @NotNull String commandHelpPageStarter(String description) {
        return ChatColor.RED + " If you are searching for the vanilla help, use \"" + ChatColor.GRAY + "/minecraft:help" + ChatColor.RED + "\"!\n\n"
                + ChatColor.AQUA + " Help for command \"" + ChatColor.GRAY + "/account" + ChatColor.AQUA + "\". Use \"" + ChatColor.GRAY + "/mcdc:help" + ChatColor.AQUA + "\"\n"
                + " to see a list of all available commands.\n\n"
                + ChatColor.GREEN + ChatColor.UNDERLINE + "Description" + ChatColor.RESET + ChatColor.DARK_GRAY + ":\n"
                + ChatColor.RESET + description + "\n"
                + ChatColor.RESET + " The subcommands are structured as followed:\n"
                + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "subcommand 1" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Description\n"
                + ChatColor.GRAY + " | |-> " + ChatColor.DARK_AQUA + "subcommand argument 1" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Description\n"
                + ChatColor.GRAY + " | `-> " + ChatColor.DARK_AQUA + "subcommand argument 2" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Description\n"
                + ChatColor.GRAY + " `-> " + ChatColor.DARK_AQUA + "subcommand 2" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Description\n"
                + ChatColor.GRAY + "   |-> " + ChatColor.DARK_AQUA + "subcommand argument 1" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Description\n"
                + ChatColor.GRAY + "   `-> " + ChatColor.DARK_AQUA + "subcommand argument 2" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Description\n"
                + spacerLine
                + ChatColor.GREEN + ChatColor.UNDERLINE + "Subcommands" + ChatColor.RESET + ChatColor.DARK_GRAY + ":\n" + ChatColor.RESET;
    }

    private static String accountHelp(boolean isPlayer, boolean hasAccountEditPermission) {
        String out = commandHelpPageStarter(" The account command provides lots of useful features for managing\n"
                + " your local minecraft server/discord relationship.\n"
                + ChatColor.RED + ChatColor.BOLD + " THIS ACCOUNT IS NOT OFFICIAL AND ONLY VALID ON THIS SERVER AND WITH THE MCDC BOT!")
                ;

        if (isPlayer)
            out += ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "link" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Initiates the mc/dc linking process.\n"
                 + ChatColor.GRAY + " | `-> " + ChatColor.DARK_AQUA + "token" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "OPTIONAL - the linking token, given from discord\n"
                 + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "name" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Give yourself a new account tag\n"
                 + ChatColor.GRAY + " | `-> " + ChatColor.DARK_AQUA + "name" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "STRING - your new account name\n"
                 + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "username" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Give yourself a new account tag\n"
                 + ChatColor.GRAY + " | `-> " + ChatColor.DARK_AQUA + "name" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "STRING - your new account name\n"
                    ;

        out += ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "get" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Gets the account tag for a user\n"
            + ChatColor.GRAY + " | `-> " + ChatColor.DARK_AQUA + "user" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "The user to get the tag from\n";

        if (hasAccountEditPermission)
            out += ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "remove" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Removes an account from a user\n"
                    + ChatColor.GRAY + " | `-> " + ChatColor.DARK_AQUA + "account tag" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "The account tag of the user whose account shall be removed.\n"
                    + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "save" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Saves all currently linked accounts to the file.\n"
                    + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "reload" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Reloads all accounts from the file.\n"
                    ;

        if (isPlayer)
            out += ChatColor.GRAY + " `-> " + ChatColor.DARK_AQUA + "unlink" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Deletes your account\n";

        return out;
    }

    @Contract(pure = true)
    private static @NotNull String configHelp() {
        return commandHelpPageStarter(" The config command provides functionality to configure\n" +
                " The discord bot and the plugin behavior.")

                + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "add" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Adds a value to the list at PATH\n"
                + ChatColor.GRAY + " | |-> " + ChatColor.DARK_AQUA + "PATH" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "The PATH to add the VALUE to\n"
                + ChatColor.GRAY + " | `-> " + ChatColor.DARK_AQUA + "VALUE" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "The VALUE to add to the list at PATH\n"
                + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "default" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Resets the configuration to its default values.\n"
                + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "get" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Gets the value from the PATH\n"
                + ChatColor.GRAY + " | |-> " + ChatColor.DARK_AQUA + "PATH" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "The PATH to get the value from\n"
                + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "reload" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Reloads the configuration from the file.\n"
                + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "remove" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Removes a value from the list at PATH\n"
                + ChatColor.GRAY + " | |-> " + ChatColor.DARK_AQUA + "PATH" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "The PATH to add the VALUE to\n"
                + ChatColor.GRAY + " | `-> " + ChatColor.DARK_AQUA + "VALUE" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "The VALUE to add to the list at PATH\n"
                + ChatColor.GRAY + " |-> " + ChatColor.DARK_AQUA + "save" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Saves the configuration to the config file\n"
                + ChatColor.GRAY + " `-> " + ChatColor.DARK_AQUA + "set" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "Sets the PATH to VALUE\n"
                + ChatColor.GRAY + "   |-> " + ChatColor.DARK_AQUA + "PATH" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "The PATH to add the VALUE to\n"
                + ChatColor.GRAY + "   `-> " + ChatColor.DARK_AQUA + "VALUE" + ChatColor.GRAY + "   -> " + ChatColor.BLUE + "The VALUE to add to the list at PATH\n"
                + spacerLine
                ;
    }
}
