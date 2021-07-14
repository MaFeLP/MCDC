package com.github.mafelp.discord.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import static com.github.mafelp.utils.Settings.discordCommandPrefix;

/**
 * The class that handles whispering on the discord side of things.
 */
public class WhisperListener {
    /**
     * The method that initializes the unlinking.
     *
     * @param event The event containing information about the command that was being used.
     */
    public static void onSlashCommand(SlashCommandCreateEvent event) {
        User author = event.getSlashCommandInteraction().getUser();
        // help message for wrong usage
        EmbedBuilder helpMessage = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Error")
                .addField("Usage", discordCommandPrefix + "whisper <@account name> \"<message>\"")
                .addField("Alternative usage", discordCommandPrefix + "whister <@discord name> \"<message>\"")
                .addField("Functionality", "Whispers your message to the minecraft account of the receiver.")
                .setColor(new Color(0xFFB500))
                .setFooter("Help message for command \"whisper\"");
        // Embed to send, when the bot does not have the required Permissions.
        EmbedBuilder noAccountEmbed = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Error!")
                .setColor(Color.RED);

        // Only lets this command be executed with a private message or in a group of people.
        if (event.getSlashCommandInteraction().getChannel().isPresent() && (
                event.getSlashCommandInteraction().getChannel().get().asServerChannel().isPresent()
                        || event.getSlashCommandInteraction().getChannel().get().asGroupChannel().isPresent()
                )) {
            EmbedBuilder notAPrivateMessage = new EmbedBuilder()
                    .setAuthor(author)
                    .setTitle("Error!")
                    .setColor(Color.RED)
                    .addField("Not a private message error", "This command can only be used via direct message or in group messages with this bot.");

            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(notAPrivateMessage).respond();
            return;
        }

        Logging.debug("User \"" + author.getName() + "\" executed command \"" + event.getSlashCommandInteraction().getCommandName() + "\".");

        Optional<Account> optionalSenderAccount = Account.getByDiscordUser(author);

        // If the user does not have an account.
        if (optionalSenderAccount.isEmpty()) {
            Logging.debug("User does not have an account. Sending no account Embed.");
            noAccountEmbed.addField("You don't have account!", "You can only whisper, when you have an account! Use \"" + discordCommandPrefix + "link\" to create one!");
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(noAccountEmbed).respond();
            return;
        }

        Account receiver;
        Optional<Account> optionalAccount;
        // If no valid user was given, use the discord api instead. This won't throw an error and because the API
        // can not have an account, the execution will fail, with a no account embed.
        if (event.getSlashCommandInteraction().getFirstOption().isPresent()) {
            if (event.getSlashCommandInteraction().getFirstOption().get().requestUserValue().isPresent()) {
                try {
                    optionalAccount = Account.getByDiscordUser(event.getSlashCommandInteraction().getFirstOption().get().requestUserValue().get().join());
                } catch (CompletionException e) {
                    Logging.debug("User " + event.getSlashCommandInteraction().getUser().getName() + " executed command \"" + event.getSlashCommandInteraction().getCommandName() + "\"; Response: No Such User");
                    noAccountEmbed.addField("Discord User Not Found", "The User you entered is invalid. Please try again!");
                    event.getSlashCommandInteraction().createImmediateResponder().addEmbed(noAccountEmbed).respond();
                    return;
                }
            } else {
                noAccountEmbed.addField("Discord User Not Found", "The User you entered is invalid. Please try again!");
                event.getSlashCommandInteraction().createImmediateResponder().addEmbed(noAccountEmbed).respond();
                return;
            }
        } else {
            // Should not be reached!
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(helpMessage).respond();
            return;
        }

        if (optionalAccount.isPresent()) {
            receiver = optionalAccount.get();
            Logging.debug("Found Account with tag " + receiver.getUsername());
        } else {
            if (event.getSlashCommandInteraction().getFirstOptionUserValue().isPresent()) {
                Logging.debug("User " + event.getSlashCommandInteraction().getUser().getName() + " executed command \"" + event.getSlashCommandInteraction().getCommandName() + "\"; Response: User \"" + event.getSlashCommandInteraction().getFirstOptionUserValue().get().getName() + "\" does not have an account!");
                noAccountEmbed.addField("User has no account", "Sorry, but we could not find an account to whisper to for " + event.getSlashCommandInteraction().getFirstOptionUserValue().get().getMentionTag() + ". Please try again or tell them to use \"/link\"!");
            } else {
                Logging.debug("User " + event.getSlashCommandInteraction().getUser().getName() + " executed command \"" + event.getSlashCommandInteraction().getCommandName() + "\"; Response: No Such User");
                noAccountEmbed.addField("Discord User Not Found", "The User you entered is invalid. Please try again!");
            }
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(noAccountEmbed).respond();
            return;
        }

        EmbedBuilder playerNotOnlineEmbed = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("PlayerNotOnlineError")
                .setColor(Color.RED)
                .addField("Player Not Online", "You cannot whisper to an offline player!");

        OfflinePlayer player = receiver.getPlayer();
        if (player == null || !receiver.getPlayer().isOnline()) {
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(playerNotOnlineEmbed).respond();
            Logging.debug("Player not online. Cannot whisper message.");
            return;
        }

        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(playerNotOnlineEmbed).respond();
            Logging.debug("Player not online. Cannot whisper message.");
            return;
        }
        String message = event.getSlashCommandInteraction().getSecondOptionStringValue().orElse(null);
        //Logging.info("User " + ChatColor.GRAY + author.getName() + ChatColor.RESET + "whispered the following to minecraft user " + ChatColor.GRAY + onlinePlayer.getDisplayName() + ChatColor.RESET + ": " + ChatColor.AQUA + message);
        Settings.minecraftServer.getConsoleSender().sendMessage(whisperPrefix(event, false, receiver.getUsername()) + message);
        onlinePlayer.sendMessage(whisperPrefix(event, true, "") + message);

        event.getSlashCommandInteraction().createImmediateResponder().addEmbed(
                new EmbedBuilder()
                .setAuthor(author)
                .setColor(Color.GREEN)
                .setTitle("Success!")
                .addField("You whispered to " + onlinePlayer.getName() + ":", message)
        ).respond();
    }

    /**
     * The method that creates a prefix for whispered messages, according to the settings. <br>
     * This prefix can either be a one-liner or a two-liner.
     * @param event The event that stores all the information about the whisper message sender.
     * @param playerIsReceiver Receiver is a player; if not, it will be the console so don't display line
     *                         breaks and emoji if this is set to false.
     * @param receiver The "name" of the receiver.
     * @return The prefix to add to the message.
     */
    private static String whisperPrefix(SlashCommandCreateEvent event, boolean playerIsReceiver, String receiver) {
        String first = "";
        String last;
        if (Settings.shortMsg) {
            first = ChatColor.DARK_GRAY + "[" +
                    ChatColor.LIGHT_PURPLE + "DC" +
                    ChatColor.DARK_GRAY + "/" +
                    ChatColor.GOLD;
            last = ChatColor.DARK_GRAY + "]" +
                    ChatColor.BLACK + ": " +
                    ChatColor.RESET;

        } else {
            if (playerIsReceiver)
                first = ChatColor.GRAY + "\u2554";
            first += ChatColor.DARK_GRAY + "[" +
                    ChatColor.LIGHT_PURPLE + "DC" +
                    ChatColor.DARK_GRAY + "/" +
                    ChatColor.GOLD;
            last = ChatColor.DARK_GRAY + " as " +
                    ChatColor.DARK_AQUA +
                    "Private Message";
            if (!playerIsReceiver)
                last += ChatColor.DARK_GRAY + " to "
                        + ChatColor.DARK_AQUA + receiver;
            last += ChatColor.DARK_GRAY + "]" +
                    ChatColor.BLACK + ": ";
            if (playerIsReceiver)
                last += ChatColor.GRAY + "\n\u255A\u25B6";
            last += ChatColor.RESET;

        }
        return first +
                event.getSlashCommandInteraction().getUser().getName() +
                last;
    }
}