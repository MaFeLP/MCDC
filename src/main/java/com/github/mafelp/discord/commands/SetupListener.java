package com.github.mafelp.discord.commands;

import com.github.mafelp.discord.ChannelAdmin;
import com.github.mafelp.discord.RoleAdmin;
import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;

import static com.github.mafelp.utils.Logging.info;
import static com.github.mafelp.utils.Settings.*;

/**
 * discord channel <code>setup</code> command class. This commands creates a new role and channel based on the arguments.
 */
public class SetupListener implements MessageCreateListener {
    /**
     * The method that is being called every time a message was sent to the discord channel. <br>
     * It first checks if the content of the message is the command setup and then creates a role and channel accordingly.
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
                .addField("Usage", discordCommandPrefix + "setup <name>")
                .addField("Functionality", "Adds a role with the specified name and an according channel in which only the role can see and write to it.")
                .setColor(new Color(0xFFB500))
                .setFooter("Help message for command \"createRole\"")
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

        // If the message is empty/if the arguments are none, return
        if (command.getCommand() == null)
            return;

        if (!command.getCommand().equalsIgnoreCase(discordCommandPrefix + "setup"))
            return;

        // Checks the permission of the message author.
        if (!CheckPermission.hasAdminPermission(event.getMessageAuthor())) {
            Logging.info("User \"" + event.getMessageAuthor().getDisplayName() + "\" tried to execute command \"setup\"!");
            event.getChannel().sendMessage(CheckPermission.getPermissionDeniedEmbed(event.getMessageAuthor(), "setup the server"));
            return;
        }

        if (command.getStringArgument(0).isEmpty() || command.getStringArgument(1).isPresent()) {
            info("Person " + ChatColor.GRAY + event.getMessageAuthor().getDisplayName() + ChatColor.RESET + " used the command setup wrong. Sending help embed.");
            event.getChannel().sendMessage(helpMessage);
            return;
        }

        if (event.getServer().isEmpty()) {
            event.getChannel().sendMessage(serverNotPresentError);
            Logging.info("Could not setup the server: Server is not present. Sending Error Reply.");
            return;
        }

        if (event.getChannel().asServerTextChannel().isEmpty()) {
            minecraftServer.getLogger().warning(prefix + "Could not get the ServerTextChannel. Sending error embed.");
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setAuthor(event.getMessageAuthor())
                            .setColor(Color.RED)
                            .setTitle("Error!")
                            .addField("ServerTextChannelNotPresentError", "Could not get this Channel as a server text channel. Maybe you sent this message in private message?")
            );

            return;
        }

        String name = command.getStringArgument(0).get();

        Role role = RoleAdmin.createNewRole(event.getServer().get(), name, null, event.getChannel().asServerTextChannel().get());
        event.getMessageAuthor().asUser().ifPresent(user -> {
            user.addRole(role, "MCDC role creation: Person who created the role should get the role assigned, as well.");
            info("Added role \"" + role.getName() + "\" to player \"" + user.getName() + "\".");
        });

        successEmbed.addField("Successful role creation", "Successfully created the new role " + role.getMentionTag() + " to sync permissions for the Minecraft Channels to.");

        if (event.getServerTextChannel().isEmpty()) {
            return;
        }

        ServerTextChannel serverTextChannel = ChannelAdmin.createChannel(name, event.getServer().get(), "Minecraft Cross platform communication.", successEmbed, event.getServerTextChannel().get(), welcomeEmbed);

        if (serverTextChannel == null) {
            minecraftServer.getLogger().warning("Could not create the server Text channel. Unknown error!");
            return;
        }

        Logging.info("Added channel \"" + serverTextChannel.getName() + "\" and role \"" + role.getName() + "\" to server \"" + event.getServer().get().getName() + "\"!");
    }
}
