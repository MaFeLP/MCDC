package com.github.mafelp.minecraft.commands;

import com.github.mafelp.accounts.MinecraftLinker;
import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.github.mafelp.utils.Settings.prefix;

/**
 * Class handling the linking of minecraft and discord accounts -
 *
 * Warning This class in currently not implemented.
 */
public class Link implements CommandExecutor {
    /**
     * The Method called when command "/token" is executed.
     *
     * @param sender  The sender of the command
     * @param command the command he/she used
     * @param label the label of the command
     * @param args additional arguments passed: Discord ID and unique identifier.
     * @return command success
     */
    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Sending the player a message that this command is currently not available
        sender.sendMessage(prefix + ChatColor.RED + "This command is currently not available!");

        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

            if (args == null || args[0] == null) {
                int discordLinkToken = MinecraftLinker.getLinkToken(player);

                sender.sendMessage(prefix + ChatColor.RESET + "You link token is: " + ChatColor.GRAY + discordLinkToken + ChatColor.RESET + ". Use the command \"" + ChatColor.GRAY + Settings.discordCommandPrefix +"link " + discordLinkToken + ChatColor.RESET + "\" to link this minecraft account ot your discord account.");

                return true;
            }

            try {
                final int linkID = Integer.parseUnsignedInt(args[0]);

                // Account linkedAccount = DiscordLinker.linkToMinecraft(player, linkID);
                // sender.sendMessage(prefix + ChatColor.GREEN + "Successfully linked this player account to the discord user " + linkedAccount.getUsername());

                return true;
            } catch (NumberFormatException exception) {
                sender.sendMessage(prefix + ChatColor.RED + "Error parsing your link Token! Please try again.");
                return false;
            }

        } else {
            sender.sendMessage(prefix + ChatColor.RED + "This command can only be executed as a Player!");
            return true;
        }
    }
}
