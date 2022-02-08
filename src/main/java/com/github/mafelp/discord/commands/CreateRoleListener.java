package com.github.mafelp.discord.commands;

import com.github.mafelp.discord.RoleAdmin;
import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Logging;
import org.bukkit.ChatColor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.awt.*;
import java.util.concurrent.CompletionException;

import static com.github.mafelp.utils.Logging.info;
import static com.github.mafelp.utils.Settings.*;

/**
 * CreateRoleListenerClass implements role creation with the command <code>prefix+createRole name</code>
 * on the server, where the command was executed.
 */
public class CreateRoleListener {
    /**
     * The method that is being called every time a message was sent to the discord channel. <br>
     * It first checks if the content of the message is the command createRole and then creates the role accordingly.
     * @param event The event passed in by the api containing information about the message.
     */
    public static void onSlashCommand(SlashCommandCreateEvent event) {
        User author = event.getSlashCommandInteraction().getUser();

        // help message for wrong usage
        EmbedBuilder helpMessage = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Error")
                .addField("Usage", discordCommandPrefix + "createRole <name>")
                .setFooter("Help message for command \"createRole\"")
                .setColor(new Color(0xFFB500))
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
                .addField("Successful role creation",
                        "Successfully created a new role to sync permissions for the channel to!")
                .setColor(Color.GREEN)
                .setFooter("")
                ;

        // Embed to send, when the bot does not have the required Permissions.
        EmbedBuilder noPermissionEmbed = new EmbedBuilder()
                .setAuthor(discordApi.getYourself())
                .setTitle("Error!")
                .addField("PermissionDeniedException","Could not execute this command, because the bot lacks permissions to do so!")
                .addField("how to fix", "Please refer to this projects website https://mafelp.github.io/MCDC/create-admin-role for instructions, on how to do so.")
                .setColor(Color.RED)
                ;

        // If the server could not be found, send an error message and exit.
        if (event.getSlashCommandInteraction().getServer().isEmpty()) {
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(serverNotPresentError).respond();
            Logging.info("Could not create the new Server Role: Server is not present. Sending Error Reply.");
            return;
        }

        // Checks the permission of the message author.
        if (!CheckPermission.hasAdminPermission(author, event.getSlashCommandInteraction().getServer().get())) {
            Logging.info("User \"" + author.getName() + "\" tried to execute command \"createChannel\"!");
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(CheckPermission.getPermissionDeniedEmbed(author, "create Role"));
            return;
        }

        // Checks if the first argument after the first argument exists.
        if (event.getSlashCommandInteraction().getOptionByIndex(0).isEmpty() ||
                event.getSlashCommandInteraction().getOptionByIndex(1).isEmpty()) {
            Logging.info("User \"" + author.getName() + "\" tried to execute command \"create role\"!");
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(helpMessage).respond();
            return;
        }

        // Gets the first argument after the first argument and then gets the it as a string
        var secondOption = event.getSlashCommandInteraction().getOptionByIndex(1).get();
        String name = secondOption.getStringValue().get();

        if (secondOption.getStringValue().isEmpty()) {
            Logging.info("User \"" + author.getName() + "\" tried to execute command \"create role\"!");
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(helpMessage).respond();
            return;
        }

        // add linking and automatic linking of roles.
        // Comment of the author: The current version of the Discord API cannot handle adding roles to a channel.

        // Try creating the new Role
        try {
            if (event.getSlashCommandInteraction().getChannel().isPresent() && event.getSlashCommandInteraction().getChannel().get().asServerTextChannel().isPresent()) {
                Role role = RoleAdmin.createNewRole(event.getSlashCommandInteraction().getServer().get(), name, successEmbed, event.getSlashCommandInteraction().createImmediateResponder());
                author.addRole(role, "MCDC role creation: Person who created the role should get the role assigned, as well.");
                info("Added role \"" + role.getName() + "\" to player \"" + author.getName() + "\".");
            } else {
                minecraftServer.getLogger().warning(prefix + "Could not get the ServerTextChannel. Sending error embed.");
                event.getSlashCommandInteraction().createImmediateResponder().addEmbed(
                        new EmbedBuilder()
                                .setAuthor(author)
                                .setColor(Color.RED)
                                .setTitle("Error!")
                                .addField("ServerTextChannelNotPresentError", "Could not get this Channel as a server text channel. Maybe you sent this message in private message?")
                ).respond();
            }

        // If this exception is thrown, the bot either does not have the permission to create a new channel or
        // the connection to the discord servers has been lost.
        } catch (CompletionException exception) {
            event.getSlashCommandInteraction().createImmediateResponder().addEmbed(noPermissionEmbed).respond();
            Logging.info(ChatColor.RED + "Could not execute createRole command. Do not have the required permissions.");
        }
    }
}
