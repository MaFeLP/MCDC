package com.github.mafelp.discord.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.Optional;

import static com.github.mafelp.utils.Settings.discordApi;
import static com.github.mafelp.utils.Settings.discordCommandPrefix;

/**
 * The class that handles whispering on the discord side of things.
 */
public class WhisperListener implements MessageCreateListener {
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
                .addField("Usage", discordCommandPrefix + "whisper <@account name> \"<message>\"")
                .addField("Alternative usage", discordCommandPrefix + "whister <@discord name> \"<message>\"")
                .addField("Functionality", "Whispers your message to the minecraft account of the receiver.")
                .setColor(new Color(0xFFB500))
                .setFooter("Help message for command \"whisper\"")
                ;

        // Embed to send, when the bot does not have the required Permissions.
        EmbedBuilder noAccountEmbed = new EmbedBuilder()
                .setAuthor(messageCreateEvent.getMessageAuthor())
                .setTitle("Error!")
                .setColor(Color.RED)
                ;

        // If the message is empty/if the arguments are none, return
        if (command.getCommand() == null)
            return;

        // If the command is not equal to setup, do nothing and return.
        if (!(command.getCommand().equalsIgnoreCase(discordCommandPrefix + "whisper") || command.getCommand().equalsIgnoreCase(discordCommandPrefix + "mcmsg")))
            return;

        // Deletes the original message, if specified in the configuration under deleteDiscordCommandMessages
        if (Settings.getConfiguration().getBoolean("deleteDiscordCommandMessages") && messageCreateEvent.isServerMessage()) {
            Logging.debug("Deleting original command message with ID: " + messageCreateEvent.getMessage().getIdAsString());
            messageCreateEvent.getMessage().delete("Specified in MCDC configuration: Was a command message.").join();
            Logging.debug("Deleted the original command message.");
        }

        // Only lets this command be executed with a private message or in a group of people.
        if (messageCreateEvent.isPrivateMessage() || messageCreateEvent.isGroupMessage()) {
            EmbedBuilder notAPrivateMessage = new EmbedBuilder()
                    .setAuthor(messageCreateEvent.getMessageAuthor())
                    .setTitle("Error!")
                    .setColor(Color.RED)
                    .addField("Not a private message error", "This command can only be used via direct message or in group messages with this bot.")
                    ;

            messageCreateEvent.getChannel().sendMessage(notAPrivateMessage);
            return;
        }

        // On wrong usage, aka. when you pass arguments.
        if (command.getStringArgument(0).isEmpty()) {
            messageCreateEvent.getChannel().sendMessage(helpMessage);
            return;
        }

        // Only execute if te message Author is a user and not a webhook.
        if (messageCreateEvent.getMessageAuthor().asUser().isPresent()) {
            // noAccountEmbed.addField("","Sorry, user with tag " + );
            Logging.debug("User \"" + messageCreateEvent.getMessageAuthor().getDisplayName() + "\" executed command \"unlink\". Parsing User...");
            User user = messageCreateEvent.getMessageAuthor().asUser().get();

            Optional<Account> optionalSenderAccount = Account.getByDiscordUser(user);

            // If the user does not have an account.
            if (optionalSenderAccount.isEmpty()) {
                Logging.debug("User does not have an account. Sending no account Embed.");
                noAccountEmbed.addField("You don't have account!", "You can only whisper, when you have an account! Use \"" + discordCommandPrefix + "link\" to create one!");
                messageCreateEvent.getChannel().sendMessage(noAccountEmbed);
                return;
            }

            StringBuilder userID = new StringBuilder();
            for (char c: command.getStringArgument(0).get().toCharArray()) {
                if (c != '<' && c != '>' && c != '!' && c != '@')
                    userID.append(c);
            }

            Account receiver;
            Optional<Account> optionalAccount = Account.getByUsername(command.getStringArgument(0).get());

            if (optionalAccount.isPresent()){
                receiver = optionalAccount.get();
                Logging.debug("Found Account with tag " + receiver.getUsername());
            } else {
                try {
                    User userReceiver = discordApi.getUserById(Long.parseLong(userID.toString())).join();

                    if (userReceiver == null) {
                        Logging.debug("Could not get user by ID " + userID);
                        noAccountEmbed.addField("NoSuchUser", "No Discord Account could be found for the user with ID " + userID);
                        return;
                    }

                    Optional<Account> optionalReceiverAccount = Account.getByDiscordUser(userReceiver);
                    Logging.debug("Getting the account for user \"" + command.getStringArgument(0).get() + "\"...");
                    // Get the account and some information about it.
                    if (optionalReceiverAccount.isEmpty()) {
                        Logging.debug("Discord User " + userReceiver.getName() + " does not have an account. Sending noAccountEmbed.");
                        noAccountEmbed.addField("No Account", "Discord user " + userReceiver.getName() + " does not have a linked minecraft account. They should use \"" + discordCommandPrefix + "link\" to get one!");
                        return;
                    }
                    receiver = optionalReceiverAccount.get();

                } catch (NumberFormatException ex) {
                    messageCreateEvent.getChannel().sendMessage(helpMessage);
                    Logging.debug("Wrong usage of Discord Whisper command. Sending help embed...");
                    return;
                }
            }

            EmbedBuilder playerNotOnlineEmbed = new EmbedBuilder()
                    .setAuthor(messageCreateEvent.getMessageAuthor())
                    .setTitle("PlayerNotOnlineError")
                    .setColor(Color.RED)
                    .addField("Player Not Online", "You cannot whisper to an offline player!");

            OfflinePlayer player = receiver.getPlayer();
            if (player == null || !receiver.getPlayer().isOnline()) {
                messageCreateEvent.getChannel().sendMessage(playerNotOnlineEmbed);
                Logging.debug("Player not online. Cannot whisper message.");
                return;
            }

            Player onlinePlayer = player.getPlayer();
            if (onlinePlayer == null) {
                messageCreateEvent.getChannel().sendMessage(playerNotOnlineEmbed);
                Logging.debug("Player not online. Cannot whisper message.");
                return;
            }

            if (command.getStringArgument(1).isEmpty()) {
                messageCreateEvent.getChannel().sendMessage(playerNotOnlineEmbed);
                Logging.debug("Player not online. Cannot whisper message.");
                return;
            }

            Logging.debug("Building whisper message...");
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 1; i < command.getArguments().length; ++i) {
                if (i != 1)
                    messageBuilder.append(' ');
                Optional<String> messagePart = command.getStringArgument(i);
                if (messagePart.isPresent())
                    messageBuilder.append(messagePart.get());
                else
                    break;
            }

            String message = messageBuilder.toString();

            Logging.debug("Whisper message built! Sending it...");
            Logging.info("User " + ChatColor.GRAY + messageCreateEvent.getMessageAuthor().getDisplayName() + ChatColor.RESET + "whispered the following to minecraft user " + ChatColor.GRAY + onlinePlayer.getDisplayName() + ChatColor.RESET + ": " + ChatColor.AQUA + message);
            onlinePlayer.sendMessage(message);
        } else {
            Logging.debug("MessageAuthor \"" + messageCreateEvent.getMessageAuthor().getDisplayName() + "\" is not a User! Sending Error embed...");
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
