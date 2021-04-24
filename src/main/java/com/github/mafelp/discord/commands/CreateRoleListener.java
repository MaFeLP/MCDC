package com.github.mafelp.discord.commands;

import com.github.mafelp.discord.RoleAdmin;
import com.github.mafelp.utils.*;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.*;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.concurrent.CompletionException;

import static com.github.mafelp.utils.Logging.info;
import static com.github.mafelp.utils.Settings.*;

/**
 * CreateRoleListenerClass implements role creation with the command <code>prefix+createRole name</code>
 * on the server, where the command was executed.
 */
public class CreateRoleListener implements MessageCreateListener {
    /**
     * The method that is being called every time a message was sent to the discord channel. <br>
     * It first checks if the content of the message is the command createRole and then creates the role accordingly.
     * @param event The event passed in by the api containing information about the message.
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

        // Tries parsing the command. If it fails, exit. The error handling is being done by the CreateChannelListener.
        Command command;
        try {
            command = CommandParser.parseFromString(content);
        } catch (CommandNotFinishedException | NoCommandGivenException e) {
            // Logging.logException(e, "Error parsing hte command from the string...");
            return;
        }

        // help message for wrong usage
        EmbedBuilder helpMessage = new EmbedBuilder()
                .setAuthor(event.getMessageAuthor())
                .setTitle("Error")
                .addField("Usage", discordCommandPrefix + "createRole <name>")
                .setFooter("Help message for command \"createRole\"")
                .setColor(new Color(0xFFB500))
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

        // If the message is empty/if the arguments are none, return
        if (command.getCommand() == null)
            return;

        if (!command.getCommand().equalsIgnoreCase(discordCommandPrefix + "createRole"))
            return;

        // Checks the permission of the message author.
        if (!CheckPermission.hasAdminPermission(event.getMessageAuthor())) {
            Logging.info("User \"" + event.getMessageAuthor().getDisplayName() + "\" tried to execute command \"createChannel\"!");
            event.getChannel().sendMessage(CheckPermission.getPermissionDeniedEmbed(event.getMessageAuthor(), "create Role"));
            return;
        }

        // If the user passed a wrong number of arguments, send the help message and exit.
        if (command.getStringArgument(0).isEmpty() || command.getStringArgument(1).isPresent()) {
            info("Person " + ChatColor.GRAY + event.getMessageAuthor().getDisplayName() + ChatColor.RESET + " used the command createRole wrong. Sending help embed.");
            event.getChannel().sendMessage(helpMessage);
            return;
        }

        // If the server could not be found, send an error message and exit.
        if (event.getServer().isEmpty()) {
            event.getChannel().sendMessage(serverNotPresentError);
            Logging.info("Could not create the new Server Role: Server is not present. Sending Error Reply.");
            return;
        }

        // add linking and automatic linking of roles.
        // Comment of the author: The current version of the Discord API cannot handle adding roles to a channel.

        // Try creating the new Role
        try {
            if (event.getChannel().asServerTextChannel().isPresent()) {
                Role role = RoleAdmin.createNewRole(event.getServer().get(), command.getStringArgument(0).get(), successEmbed, event.getChannel().asServerTextChannel().get());
                event.getMessageAuthor().asUser().ifPresent(user -> {
                    user.addRole(role, "MCDC role creation: Person who created the role should get the role assigned, as well.");
                    info("Added role \"" + role.getName() + "\" to player \"" + user.getName() + "\".");
                });
            } else {
                minecraftServer.getLogger().warning(prefix + "Could not get the ServerTextChannel. Sending error embed.");
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setAuthor(event.getMessageAuthor())
                                .setColor(Color.RED)
                                .setTitle("Error!")
                                .addField("ServerTextChannelNotPresentError", "Could not get this Channel as a server text channel. Maybe you sent this message in private message?")
                );
            }

        // If this exception is thrown, the bot either does not have the permission to create a new channel or
        // the connection to the discord servers has been lost.
        } catch (CompletionException exception) {
            event.getChannel().sendMessage(noPermissionEmbed);
            Logging.info(ChatColor.RED + "Could not execute createRole command. Do not have the required permissions.");
        }
    }
}
