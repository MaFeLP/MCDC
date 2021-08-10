package com.github.mafelp.minecraft.commands;

import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import static com.github.mafelp.utils.Logging.info;

public class Help implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendNormalHelpMessage(sender);
            info("\"" + sender.getName() + "\" executed command \"/help\"; Result: Normal help message.");
            return true;
        }

        info(sender.getName() + "Executed command \"/help\"");

        return false;
    }

    private static void sendNormalHelpMessage(CommandSender sender) {
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

        String out = ChatColor.GREEN + "+--------------------" + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "MCDC" + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + "HELP" + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + "--------------------+\n"
                + ChatColor.RED + " If you are searching for the vanilla help, use \"" + ChatColor.GRAY + "/minecraft:help" + ChatColor.RED + "\"!\n\n"
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
        out += ChatColor.GREEN + "+--------------------" + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "MCDC" + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + "HELP" + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + "--------------------+";

        sender.sendMessage(out);
    }
}
