package com.github.mafelp.discord.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.accounts.AccountManager;
import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.Optional;

import static com.github.mafelp.utils.Settings.*;

/**
 * The class used to listen in discord and unlinks your account.
 */
public class UnlinkListener implements MessageCreateListener {
    /**
     * The method that initializes the unlinking.
     * @param messageCreateEvent The
     */
    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        // If the message is sent by the bot, return
        if (messageCreateEvent.getMessageAuthor().isYourself()) {
            return;
        }

        // If message does not start with the command prefix, return
        // if (event.getReadableMessageContent().startsWith(discordCommandPrefix) ||
        //        event.getReadableMessageContent() == null)
        //    return;


        // Gets the content of the message as strings (prints it, if debug is enabled)
        String content = messageCreateEvent.getReadableMessageContent();

        // If the command could not be passed, exit. Error handling is done by the CreateChannelListener.
        Command command;
        try {
            command = CommandParser.parseFromString(content);
        } catch (CommandNotFinishedException | NoCommandGivenException e) {
            // Logging.logException(e, "Error parsing hte command from the string...");
            return;
        }

        // help message for wrong usage
        EmbedBuilder helpMessage = new EmbedBuilder()
                .setAuthor(messageCreateEvent.getMessageAuthor())
                .setTitle("Error")
                .addField("Usage", discordCommandPrefix + "unlink")
                .addField("Functionality", "Unlinks your discord account from your minecraft account.")
                .setColor(new Color(0xFFB500))
                .setFooter("Help message for command \"unlink\"")
                ;

        // the embed sent on successful execution of the command.
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setAuthor(messageCreateEvent.getMessageAuthor())
                .setTitle("Success!")
                .setColor(Color.GREEN)
                ;

        // Embed to send, when the bot does not have the required Permissions.
        EmbedBuilder noAccountEmbed = new EmbedBuilder()
                .setAuthor(messageCreateEvent.getMessageAuthor())
                .setTitle("Error!")
                .addField("NoAccountError","Sorry, you don't have an account to unlink! Use \"" + discordCommandPrefix + "link\" to create one!")
                .setColor(Color.RED)
                ;

        // If the message is empty/if the arguments are none, return
        if (command.getCommand() == null)
            return;

        // If the command is not equal to setup, do nothing and return.
        if (!command.getCommand().equalsIgnoreCase(discordCommandPrefix + "unlink"))
            return;

        // On wrong usage, aka. when you pass arguments.
        if (command.getStringArgument(0).isPresent()) {
            messageCreateEvent.getChannel().sendMessage(helpMessage);
            return;
        }

        // Deletes the original message, if specified in the configuration under deleteDiscordCommandMessages
        if (Settings.getConfiguration().getBoolean("deleteDiscordCommandMessages") && messageCreateEvent.isServerMessage()) {
            Logging.debug("Deleting original command message with ID: " + messageCreateEvent.getMessage().getIdAsString());
            messageCreateEvent.getMessage().delete("Specified in MCDC configuration: Was a command message.").join();
            Logging.debug("Deleted the original command message.");
        }

        // Only execute if te message Author is a user and not a webhook.
        if (messageCreateEvent.getMessageAuthor().asUser().isPresent()) {
            User user = messageCreateEvent.getMessageAuthor().asUser().get();

            Optional<Account> optionalAccount = Account.getByDiscordUser(user);

            // If the user does not have an account.
            if (optionalAccount.isEmpty()) {
                messageCreateEvent.getChannel().sendMessage(noAccountEmbed);
                return;
            }

            // Get the account and some information about it.
            Account account = optionalAccount.get();

            String minecraftName = account.getPlayer().getName();
            String mentionTag = account.getMentionTag();

            // Then remove the account.
            AccountManager.removeAccount(account);

            successEmbed.addField("Successful Unlinking","Successfully unlinked your minecraft account \"" + minecraftName + "\" from user discord account " + mentionTag);

            messageCreateEvent.getChannel().sendMessage(successEmbed);
        } else {
            messageCreateEvent.getChannel().sendMessage(
                    new EmbedBuilder()
                    .setAuthor(discordApi.getYourself())
                    .setTitle("Error!")
                    .setColor(Color.RED)
                    .addField("UserParsingError","You are not a user, so you can't have an account!")
            );
        }
    }
}
