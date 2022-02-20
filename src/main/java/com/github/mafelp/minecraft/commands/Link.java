package com.github.mafelp.minecraft.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.accounts.DiscordLinker;
import com.github.mafelp.accounts.MinecraftLinker;
import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Check if the command Sender is a player.
        if (sender instanceof Player player) {
            if (Account.getByPlayer(player).isPresent()) {
                // If the CommandSender is a player, we can safely cast it to a player.
                Account account = Account.getByPlayer(player).get();

                final String message = "Sorry, but your Minecraft Account is already linked to the discord user " + account.getUser().getName() + "!\n" +
                        "Use \"/unlink\" in Minecraft or \"" + Settings.getConfiguration().get("discordCommandPrefix") + "unlink\" to unlink your accounts!";

                player.sendMessage(prefix + ChatColor.RED + message);

                return true;
            }

            // If no arguments are given, return and send the user a token.
            if (args == null) {
                sendLinkToken(player);
                return true;
            }
            if (args.length < 1) {
                sendLinkToken(player);
                return true;
            }

            // Try to link the ID, which was given as the argument, to a ID made with the discord link command.
            try {
                final int linkID = Integer.parseUnsignedInt(args[0]);

                Optional<Account> linkedAccount = DiscordLinker.linkToMinecraft(player, linkID);

                if (linkedAccount.isEmpty()) {
                    sendLinkToken(player);
                    return true;
                }

                sender.sendMessage(prefix + ChatColor.GREEN + "Successfully linked this player account to the discord user " + linkedAccount.get().getUsername());

                return true;
            // This exception is thrown, when the first argument is not an integer.
            } catch (NumberFormatException exception) {
                sender.sendMessage(prefix + ChatColor.RED + "Error parsing your link Token! Please try again.");
                return false;
            }

        // If the command is not executed by a player (e.g. a console), send an error message and exit.
        } else {
            sender.sendMessage(prefix + ChatColor.RED + "This command can only be executed as a Player!");
            return true;
        }
    }

    /**
     * The method that sends the link token in a nice message to the {@link Player} that executed this command.
     * @param player The player to send its token to.
     */
    protected static void sendLinkToken(Player player) {
        int discordLinkToken = MinecraftLinker.getLinkToken(player);

        player.sendMessage(prefix + ChatColor.RESET + "You link token is: " + ChatColor.GRAY + discordLinkToken + ChatColor.RESET + ". Use the command \"" + ChatColor.GRAY + Settings.discordCommandPrefix + "link " + discordLinkToken + ChatColor.RESET + "\" to link this minecraft account ot your discord account.");
    }
}
