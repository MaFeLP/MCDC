package com.github.mafelp.minecraft.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

import static com.github.mafelp.utils.Settings.prefix;

/**
 * The class that handles execution of the command <code>/whisper</code>
 */
public class Whisper implements CommandExecutor {
    /**
     * The Method called when command <code>/token</code> is executed.
     *
     * @param commandSender  The sender of the command
     * @param command the command he/she used
     * @param label the label of the command
     * @param args additional arguments passed: Discord ID and unique identifier.
     * @return command success
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Only lets players execute this command
        if (!(commandSender instanceof Player)) {
            Logging.debug("CommandSender " + commandSender.getName() + " is not a user and can therefore not whisper to accounts...");
            commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, you can only execute this command as a player!");
            return true;
        }

        Player player = (Player) commandSender;

        // Checks if the command sender has an account.
        if (Account.getByPlayer(player).isEmpty()) {
            Logging.debug("Player " + commandSender.getName() + " does not have an account. They are not allowed to whisper!");
            commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, you are only allowed to whisper, if you have an account. Use \"/link\" to get one!");
            return true;
        }

        // Try to parse the first argument into an account name.
        // All the other arguments are going to get parsed into an argument.
        Logging.debug("Parsing whisper message...");
        boolean isReceiver = true;
        boolean firstWord = true;
        String receiverString = "";
        StringBuilder messageBuilder = new StringBuilder();
        for (String s :
                args) {
            if (isReceiver) {
                isReceiver = false;
                receiverString = s;
            } else {
                if (!firstWord)
                    messageBuilder.append(' ');
                else
                    firstWord = false;
                messageBuilder.append(s);
            }
        }
        String message = messageBuilder.toString();

        // Try to get the account.
        Optional<Account> optionalAccount = Account.getByUsername(receiverString);
        Account receiver;
        Logging.debug("Trying to parse the receiver account.");
        if (optionalAccount.isPresent()) {
            Logging.debug("Account parsing complete! Found an Account tag.");
            receiver = optionalAccount.get();
        } else {
            Logging.debug("Not an account tag. Trying to parse from minecraft name.");
            StringBuilder receiverBuilder = new StringBuilder();
            for (char c : receiverString.toCharArray()) {
                if (c != '@')
                    receiverBuilder.append(c);
                else
                    Logging.debug("Removed leading @");
            }
            String receiverBuilt = receiverBuilder.toString();

            Logging.debug("Scanning offline players...");
            for (OfflinePlayer p : Settings.minecraftServer.getOfflinePlayers()) {
                if (Objects.equals(p.getName(), receiverBuilt))
                    optionalAccount = Account.getByPlayer(p);
            }

            if (optionalAccount.isPresent()){
                receiver = optionalAccount.get();
            } else {
                Logging.debug("Account " + receiverString + " is not a minecraft, nor an account name.");
                commandSender.sendMessage(prefix + ChatColor.RED + "Sorry, no account with account tag \"" + receiverString + "\" found!");
                return true;
            }
        }

        EmbedBuilder messageEmbed = new EmbedBuilder()
                .setAuthor(Account.getByPlayer(player).get().getUser())
                .setColor(Color.YELLOW)
                .setTitle("Whisper Message from " + commandSender.getName())
                .setDescription(message)
                .setFooter("On " + Settings.serverName);

        Logging.debug("Sending whisper message to " + receiver.getUsername() + "...");
        Message messageSent = receiver.getUser().sendMessage(messageEmbed).join();

        commandSender.sendMessage(prefix + ChatColor.GREEN + "You whispered to " + ChatColor.GRAY + receiver.getUsername() + ChatColor.GREEN + ":" +
                ChatColor.GRAY + "\n\u255A\u25B6" + ChatColor.RESET + message);

        Logging.info(ChatColor.GRAY + player.getName() + ChatColor.RESET + " has whispered to account " + ChatColor.GRAY + receiver.getUsername() + ChatColor.RESET + ": " + ChatColor.GRAY + message);
        Logging.debug("This message has the id: " + messageSent.getId());
        return true;
    }
}
