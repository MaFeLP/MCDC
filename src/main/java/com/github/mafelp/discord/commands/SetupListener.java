package com.github.mafelp.discord.commands;

import com.github.mafelp.discord.ChannelAdmin;
import com.github.mafelp.discord.RoleAdmin;
import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Logging;
import org.bukkit.ChatColor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.awt.*;
import java.util.concurrent.CompletionException;

import static com.github.mafelp.utils.Logging.info;
import static com.github.mafelp.utils.Settings.*;

/**
 * discord channel <code>setup</code> command class. This commands creates a new role and channel based on the arguments.
 */
public class SetupListener {
    /**
     * The method that is being called every time a message was sent to the discord channel. <br>
     * It first checks if the content of the message is the command setup and then creates a role and channel accordingly.
     * @param event The event passed in by the api containing information about the message.
     */
    public static void onSlashCommand(SlashCommandCreateEvent event) {
        User author = event.getSlashCommandInteraction().getUser();
        
        // help message for wrong usage
        EmbedBuilder helpMessage = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Error")
                .addField("Usage", discordCommandPrefix + "setup <name>")
                .addField("Functionality", "Adds a role with the specified name and an according channel in which only the role can see and write to it.")
                .setColor(new Color(0xFFB500))
                .setFooter("Help message for command \"createRole\"")
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

        // the embed sent on successful execution of the command.
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Success!")
                .setColor(Color.GREEN)
                ;

        // The embed sent to the new channel.
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

        // Embed to send, when the bot does not have the required Permissions.
        EmbedBuilder noPermissionEmbed = new EmbedBuilder()
                .setAuthor(discordApi.getYourself())
                .setTitle("Error!")
                .addField("PermissionDeniedException","Could not execute this command, because the bot lacks permissions to do so!")
                .addField("how to fix", "Please refer to this projects website https://mafelp.github.io/MCDC/create-admin-role for instructions, on how to do so.")
                .setColor(Color.RED)
                ;

        // If no server could be found, send an error message and exit.
        if (event.getSlashCommandInteraction().getServer().isEmpty()) {
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(serverNotPresentError).respond();
            Logging.info("Could not setup the server: Server is not present. Sending Error Reply.");
            return;
        }

        // Checks the permission of the message author.
        if (!CheckPermission.hasAdminPermission(author, event.getSlashCommandInteraction().getServer().get())) {
            Logging.info("User \"" + author.getName() + "\" tried to execute command \"setup\"!");
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(CheckPermission.getPermissionDeniedEmbed(author, "setup the server")).respond();
            return;
        }

        // If the first argument is empty, send the help message and exit.
        if (event.getSlashCommandInteraction().getFirstOptionStringValue().isEmpty()) {
            info("Person " + ChatColor.GRAY + author.getName() + ChatColor.RESET + " used the command setup wrong. Sending help embed.");
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(helpMessage).respond();
            return;
        }

        String name = event.getSlashCommandInteraction().getFirstOptionStringValue().get();

        // try to create the role.
        try {
            Role role = RoleAdmin.createNewRole(event.getSlashCommandInteraction().getServer().get(), name, null, null);

            author.addRole(role, "MCDC role creation: Person who created the role should get the role assigned, as well.");
            info("Added role \"" + role.getName() + "\" to player \"" + author.getName() + "\".");

            successEmbed.addField("Successful role creation", "Successfully created the new role " + role.getMentionTag() + " to sync permissions for the Minecraft Channels to.");

            ServerTextChannel serverTextChannel = ChannelAdmin.createChannel(name, event.getSlashCommandInteraction().getServer().get(), "Minecraft Cross platform communication.", successEmbed, event.getSlashCommandInteraction().createImmediateResponder(), welcomeEmbed);
            Logging.info("Added channel \"" + serverTextChannel.getName() + "\" and role \"" + role.getName() + "\" to server \"" + event.getSlashCommandInteraction().getServer().get() + "\"!");

        // If this exception is thrown, the bot either does not have the correct permissions to create channels and Roles,
        // send the user an embed explaining the issue.
        } catch (CompletionException exception) {
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(noPermissionEmbed).respond();
            Logging.info(ChatColor.RED + "Could not execute Setup command. Do not have the required permissions.");
        }
    }
}
