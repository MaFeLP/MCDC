package com.github.mafelp.discord.commands;

import com.github.mafelp.discord.ChannelAdmin;
import com.github.mafelp.discord.RoleAdmin;
import com.github.mafelp.utils.*;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.concurrent.CompletionException;

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

        // the embed sent on successful execution of the command.
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setAuthor(event.getMessageAuthor())
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

        // If the message is empty/if the arguments are none, return
        if (command.getCommand() == null)
            return;

        // If the command is not equal to setup, do nothing and return.
        if (!command.getCommand().equalsIgnoreCase(discordCommandPrefix + "setup"))
            return;

        // Deletes the original message, if specified in the configuration under deleteDiscordCommandMessages
        if (Settings.getConfiguration().getBoolean("deleteDiscordCommandMessages") && event.isServerMessage()) {
            Logging.debug("Deleting original command message with ID: " + event.getMessage().getIdAsString());
            event.getMessage().delete("Specified in MCDC configuration: Was a command message.").join();
            Logging.debug("Deleted the original command message.");
        }

        // Checks the permission of the message author.
        if (!CheckPermission.hasAdminPermission(event.getMessageAuthor())) {
            Logging.info("User \"" + event.getMessageAuthor().getDisplayName() + "\" tried to execute command \"setup\"!");
            //event.getChannel().sendMessage(CheckPermission.getPermissionDeniedEmbed(event.getMessageAuthor(), "setup the server"));
            return;
        }

        // If the command has a wrong number of arguments, send the help embed and exit.
        if (command.getStringArgument(0).isEmpty() || command.getStringArgument(1).isPresent()) {
            info("Person " + ChatColor.GRAY + event.getMessageAuthor().getDisplayName() + ChatColor.RESET + " used the command setup wrong. Sending help embed.");
            event.getChannel().sendMessage(helpMessage);
            return;
        }

        // If the first argument is empty, send the help message and exit.
        if (command.getStringArgument(0).get().equalsIgnoreCase("")) {
            info("Person " + ChatColor.GRAY + event.getMessageAuthor().getDisplayName() + ChatColor.RESET + " used the command setup wrong. Sending help embed.");
            event.getChannel().sendMessage(helpMessage);
            return;
        }

        // If no server could be found, send an error message and exit.
        if (event.getServer().isEmpty()) {
            event.getChannel().sendMessage(serverNotPresentError);
            Logging.info("Could not setup the server: Server is not present. Sending Error Reply.");
            return;
        }

        // If the channel you sent your message to is not a TextChannel, send as error message and exit.
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

        // try to create the role.
        try {
            Role role = RoleAdmin.createNewRole(event.getServer().get(), name, null, null);

            event.getMessageAuthor().asUser().ifPresent(user -> {
                user.addRole(role, "MCDC role creation: Person who created the role should get the role assigned, as well.");
                info("Added role \"" + role.getName() + "\" to player \"" + user.getName() + "\".");
            });

            successEmbed.addField("Successful role creation", "Successfully created the new role " + role.getMentionTag() + " to sync permissions for the Minecraft Channels to.");

            if (event.getServerTextChannel().isEmpty()) {
                return;
            }

            // TODO migrate to slash commands
            // ServerTextChannel serverTextChannel = ChannelAdmin.createChannel(name, event.getServer().get(), "Minecraft Cross platform communication.", successEmbed, event.getServerTextChannel().get(), welcomeEmbed);
            //Logging.info("Added channel \"" + serverTextChannel.getName() + "\" and role \"" + role.getName() + "\" to server \"" + event.getServer().get().getName() + "\"!");
            Logging.info(ChatColor.RED + "Creating a text channel with setup is currently not supported. Please try again later.");
            event.getServerTextChannel().get().sendMessage(new EmbedBuilder()
                    .setAuthor(event.getApi().getYourself())
                    .setColor(Color.RED)
                    .setTitle("Feature unavailable")
                    .setDescription("Due to the current migration process to slash commands, it is currently not supported to create text channels with the setup command. You can still do this manually, by using the \"/create channel\" command.")
            ).join();

        // If this exception is thrown, the bot either does not have the correct permissions to create channels and Roles,
        // send the user an embed explaining the issue.
        } catch (CompletionException exception) {
            event.getChannel().sendMessage(noPermissionEmbed);
            Logging.info(ChatColor.RED + "Could not execute Setup command. Do not have the required permissions.");
        }
    }
}
