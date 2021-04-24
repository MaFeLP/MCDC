package com.github.mafelp.discord.commands;

import com.github.mafelp.utils.*;
import com.github.mafelp.discord.ChannelAdmin;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.CompletionException;

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


        // Tris to parse the command.
        Command command;
        try {
            command = CommandParser.parseFromString(content);
        } catch (CommandNotFinishedException | NoCommandGivenException e) {
            Logging.logException(e, "Error parsing the command from the string...");
            return;
        }

        if (debug)
            info("Arguments are: " + command.getCommand() + " " + Arrays.toString(command.getArguments()));

        // help message for wrong usage
        EmbedBuilder helpMessage = new EmbedBuilder()
                .setAuthor(event.getMessageAuthor())
                .setTitle("Error")
                .addField("Usage", discordCommandPrefix + "createChannel <name>")
                .setColor(new Color(0xFFB500))
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
        if (!command.getCommand().equalsIgnoreCase(discordCommandPrefix + "createChannel"))
            return;

        // Checks the permission of the message author.
        if (!CheckPermission.hasAdminPermission(event.getMessageAuthor())) {
            Logging.info("User \"" + event.getMessageAuthor().getDisplayName() + "\" tried to execute command \"createChannel\"!");
            event.getChannel().sendMessage(CheckPermission.getPermissionDeniedEmbed(event.getMessageAuthor(), "create Channel"));
            return;
        }

        // get the first channel argument and checks, if it 'empty', but it exists.
        if (command.getStringArgument(0).isPresent()) {
            if (command.getStringArgument(0).get().equalsIgnoreCase("")) {
                Logging.info("User \"" + event.getMessageAuthor().getDisplayName() + "\" tried to execute command \"createChannel\"!");
                event.getChannel().sendMessage(helpMessage);
                return;
            }
        }

        // If the command has a wrong number of arguments, send the help message and exit.
        if (command.getArguments().length != 1) {
            event.getChannel().sendMessage(helpMessage);
            return;
        }

        // Try getting the first argument. If it does not exist, send the help message and exit.
        String name;
        if (command.getStringArgument(0).isPresent())
            name = command.getStringArgument(0).get();
        else {
            event.getChannel().sendMessage(helpMessage);
            return;
        }

        // If the server is present, create a new channel
        if (event.getServer().isPresent()) {
            try {
                ChannelAdmin.createChannel(name, event.getServer().get(),
                        "Cross communication channel with the Minecraft Server " + Settings.serverName,
                        successEmbed, event.getChannel(),
                        welcomeEmbed);
            } catch (CompletionException exception) {
                // Embed to send, when the bot does not have the required Permissions.
                EmbedBuilder noPermissionEmbed = new EmbedBuilder()
                        .setAuthor(discordApi.getYourself())
                        .setTitle("Error!")
                        .addField("PermissionDeniedException","Could not execute this command, because the bot lacks permissions to do so!")
                        .addField("how to fix", "Please refer to this projects website https://mafelp.github.io/MCDC/create-admin-role for instructions, on how to do so.")
                        .setColor(Color.RED)
                        ;

                event.getChannel().sendMessage(noPermissionEmbed);
                Logging.info(ChatColor.RED + "Could not execute createChannel command. Do not have the required permissions.");
            }
        } else {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + "Channel not present.");
            Settings.minecraftServer.getLogger().warning(Settings.prefix + "Could not execute Command createChannel");

            // Reply to the sender with the error embed
            event.getChannel().sendMessage(serverNotPresentError).thenAcceptAsync(message ->
                    Logging.info("Send error embed to " + event.getMessageAuthor().getDisplayName())
            );
        }
    }
}
