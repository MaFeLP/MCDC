package com.github.mafelp.discord.commands;

import com.github.mafelp.discord.ChannelAdmin;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.awt.*;
import java.util.concurrent.CompletionException;

import static com.github.mafelp.utils.Settings.*;

/**
 * The class that listens to the discord chats, if the channel creation command is executed. -
 * As discord announced just today, there will be an update to the bot API, that'll be adding
 * slash command support. This class will be moved, if the update is available in this API.
 */
public class CreateChannelListener {
    /**
     * The method called by the discord API, for every chat message. -
     * This method will filter them and execute commands accordingly.
     * @param event The event containing information about the message.
     */
    public static void onSlashCommand(SlashCommandCreateEvent event) {
        User author = event.getSlashCommandInteraction().getUser();
        // help message for wrong usage
        EmbedBuilder helpMessage = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Error")
                .addField("Usage", discordCommandPrefix + "createChannel <name>")
                .setColor(new Color(0xFFB500))
                .setFooter("Help message for command \"createChannel\"")
                ;

        // error embed for server not present
        EmbedBuilder serverNotPresentError = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Error")
                .addField("Server not present Error","Could not get the server. Maybe you sent this " +
                        "message in a direct message?")
                .setColor(Color.RED)
                .setFooter("Error while trying to create a channel")
                ;

        EmbedBuilder successEmbed = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Success!")
                .addField("Successful channel creation",
                        "Successfully created a new channel to sync minecraft message to!")
                .setColor(Color.GREEN)
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

        // Checks if the first argument after the first argument exists.
        if (event.getSlashCommandInteraction().getFirstOption().isEmpty() ||
                event.getSlashCommandInteraction().getFirstOption().get().getFirstOption().isEmpty()) {
            Logging.info("User \"" + author.getName() + "\" tried to execute command \"createChannel\"!");
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(helpMessage).respond();
            return;
        }

        // Gets the first argument after the first argument and then gets the it as a string
        var secondOption = event.getSlashCommandInteraction().getFirstOption().get().getFirstOption().get();
        String name = secondOption.getStringValue().get();

        if (secondOption.getStringValue().isEmpty()) {
            Logging.info("User \"" + author.getName() + "\" tried to execute command \"createChannel\"!");
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(helpMessage).respond();
            return;
        }

        if (name.equalsIgnoreCase("")) {
            Logging.info("User \"" + author.getName() + "\" tried to execute command \"createChannel\"!");
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(helpMessage).respond();
            return;
        }

        // If the server is present, create a new channel
        if (event.getSlashCommandInteraction().getServer().isPresent()) {
            try {
                ChannelAdmin.createChannel(name, event.getSlashCommandInteraction().getServer().get(),
                        "Cross communication channel with the Minecraft Server " + Settings.serverName,
                        successEmbed, event.getSlashCommandInteraction().createImmediateResponder(),
                        welcomeEmbed);
            } catch (CompletionException exception) {
                event.getSlashCommandInteraction().createImmediateResponder().addEmbed(serverNotPresentError).respond();
                Logging.info(ChatColor.RED + "Could not execute createChannel command. Do not have the required permissions.");
            }
        } else {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + "Channel not present.");
            Settings.minecraftServer.getLogger().warning(Settings.prefix + "Could not execute Command createChannel");

            // Reply to the sender with the error embed
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(serverNotPresentError).respond().thenAcceptAsync(message ->
                    Logging.info("Send error embed to " + author.getName())
            );
        }
    }
}
