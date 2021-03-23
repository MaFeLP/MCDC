package com.github.mafelp.discord.commands;

import com.github.mafelp.Logging;
import com.github.mafelp.Settings;
import com.github.mafelp.discord.ChannelAdmin;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.mafelp.Logging.info;
import static com.github.mafelp.Settings.*;

public class CreateChannelListener implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        // If the message is sent by the bot, return
        if (event.getMessageAuthor().isYourself()) {
            return;
        }

        minecraftServer.getLogger().info("In Method\n" + event.getReadableMessageContent());

        // If message does not start with the command prefix, return
        // if (event.getReadableMessageContent().startsWith(discordCommandPrefix) ||
        //        event.getReadableMessageContent() == null)
        //    return;

        // Gets the content of the message as strings (prints it, if debug is enabled)
        String content = event.getReadableMessageContent();
        if (debug)
            info("Readable Message content is: " + content);

        // Creates an array of arguments
        // TODO add argument parser
        String[] args = content.split(" ");
        if (debug)
            info("Arguments are: " + Arrays.toString(args));

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
        if (args.length == 0)
            return;

        // Check if the message start with the command prefix
        if (args[0].equalsIgnoreCase(discordCommandPrefix + "createChannel")) {
            if (args.length != 2) {
                event.getChannel().sendMessage(helpMessage);
                return;
            }

            // Saves the states of the channel creation
            AtomicBoolean success = new AtomicBoolean(false);

            // If the server is present, create a new channel
            event.getServer().ifPresent(server ->  {success.set(true);
                    ChannelAdmin.createChannel(args[1], server,
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
