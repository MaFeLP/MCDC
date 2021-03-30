package com.github.mafelp.discord.commands;

import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import com.github.mafelp.discord.ChannelAdmin;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.mafelp.utils.Logging.info;
import static com.github.mafelp.utils.Settings.*;

/**
 * The class that listens to the discord chats, if the channel creation command is executed. -
 * As discord announced just today, there will be an update to the bot API, that'll be adding
 * slash command support. This class will be moved, if the update is available in this API.
 */
public class CreateChannelListener implements MessageCreateListener {
    /**
     * The method called by the discord API, for every chat message. -
     * This method will filter them and execute commands accordingly.
     * @param event The event containing information about the message.
     */
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        // If the message is sent by the bot, return
        if (event.getMessageAuthor().isYourself()) {
            return;
        }

        // If message does not start with the command prefix, return
        // if (event.getReadableMessageContent().startsWith(discordCommandPrefix) ||
        //        event.getReadableMessageContent() == null)
        //    return;

        // Gets the content of the message as strings (prints it, if debug is enabled)
        String content = event.getReadableMessageContent();
        if (debug)
            info("Readable Message content is: " + content);

        Command command;

        try {
            command = CommandParser.parseFromString(content);
        } catch (CommandNotFinishedException | NoCommandGivenException e) {
            Logging.logException(e, "");
            return;
        }

        // Creates an array of arguments
        // TODO add argument parser
        if (debug)
            info("Arguments are: " + command.getCommand() + " " + Arrays.toString(command.getArguments()));

        // help message for wrong usage
        EmbedBuilder helpMessage = new EmbedBuilder()
                .setAuthor(event.getMessageAuthor())
                .setTitle("Error")
                .addField("Usage", discordCommandPrefix + "createChannel <name>")
                .setColor(Color.RED)
                .setFooter("Help message for command \"createChannel\"")
                ;

        // error embed for server not present
        EmbedBuilder serverNotPresentError = new EmbedBuilder()
                .setAuthor(event.getMessageAuthor())
                .setTitle("Error")
                .addField("Server not present Error","Could not get the server. Maybe you sent this " +
                        "message in a direct message?")
                .setColor(Color.RED)
                .setFooter("Error while trying to create a channel")
                ;

        EmbedBuilder successEmbed = new EmbedBuilder()
                .setAuthor(event.getMessageAuthor())
                .setTitle("Success!")
                .addField("Successful channel creation",
                        "Successfully created a new channel to sync minecraft message to!")
                .setColor(Color.GREEN)
                .setFooter("")
                ;

        EmbedBuilder welcomeEmbed = new EmbedBuilder()
                .setAuthor(discordApi.getYourself())
                .setTitle("Welcome!")
                .addField("Minecraft Communication",
                        "This is a channel for cross platform communication between this channel and the minecraft" +
                                " server " + serverName + "!")
                .addField("Warning!","Every message you send in here is being transferred to the minecraft server!")
                .setColor(Color.GREEN)
                .setFooter("Made by MaFeLP: https://github.com/mafelp")
                ;

        // If the message is empty/if the arguments are none, return
        if (command.getCommand() == null)
            return;

        // Check if the message start with the command prefix
        if (command.getCommand().equalsIgnoreCase(discordCommandPrefix + "createChannel")) {
            if (command.getArguments().length == 1) {
                event.getChannel().sendMessage(helpMessage);
                return;
            }

            // Saves the states of the channel creation
            AtomicBoolean success = new AtomicBoolean(false);

            // If the server is present, create a new channel
            Command finalCommand = command;
            event.getServer().ifPresent(server ->  {success.set(true);
                    ChannelAdmin.createChannel(finalCommand.getStringArgument(1), server,
                    "Cross communication channel with the Minecraft Server " + Settings.serverName,
                            successEmbed, event.getChannel().asServerTextChannel().get(),
                            welcomeEmbed);
            });

            // If the channel could not be created, print an error warning
             if(!success.get()) {
                Settings.minecraftServer.getLogger().warning(Settings.prefix + "Channel not present.");
                Settings.minecraftServer.getLogger().warning(Settings.prefix + "Could not execute Command createChannel");

                // Reply to the sender with the error embed
                event.getChannel().sendMessage(serverNotPresentError).thenAcceptAsync(message ->
                        Logging.info("Send error embed to " + event.getMessageAuthor().getDisplayName())
                );
            }
        }
    }
}
