package com.github.mafelp.discord.commands;

import com.github.mafelp.utils.*;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.bukkit.ChatColor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.*;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;

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
                .setColor(Color.RED)
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
                .addField("Successful role creation",
                        "Successfully created a new role to sync permissions for the channel to!")
                .setColor(Color.GREEN)
                .setFooter("")
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

        if (command.getStringArgument(0).isEmpty() || command.getStringArgument(1).isPresent()) {
            info("Person " + ChatColor.GRAY + event.getMessageAuthor().getDisplayName() + ChatColor.RESET + " used the command createRole wrong. Sending help embed.");
            event.getChannel().sendMessage(helpMessage);
            return;
        }

        if (event.getServer().isEmpty()) {
            event.getChannel().sendMessage(serverNotPresentError);
            Logging.info("Could not create the new Server Role: Server is not present. Sending Error Reply.");
            return;
        }

        org.javacord.api.entity.permission.Permissions permissions = new PermissionsBuilder()
                .setAllowed(PermissionType.ADD_REACTIONS)
                .setDenied(PermissionType.ADMINISTRATOR)
                .setDenied(PermissionType.ATTACH_FILE)
                .setDenied(PermissionType.BAN_MEMBERS)
                .setAllowed(PermissionType.CHANGE_NICKNAME)
                .setDenied(PermissionType.CONNECT)
                .setDenied(PermissionType.CREATE_INSTANT_INVITE)
                .setDenied(PermissionType.DEAFEN_MEMBERS)
                .setAllowed(PermissionType.EMBED_LINKS)
                .setDenied(PermissionType.KICK_MEMBERS)
                .setDenied(PermissionType.MANAGE_CHANNELS)
                .setDenied(PermissionType.MANAGE_EMOJIS)
                .setDenied(PermissionType.MANAGE_MESSAGES)
                .setDenied(PermissionType.MANAGE_NICKNAMES)
                .setDenied(PermissionType.MANAGE_ROLES)
                .setDenied(PermissionType.MANAGE_SERVER)
                .setDenied(PermissionType.MANAGE_WEBHOOKS)
                .setAllowed(PermissionType.MENTION_EVERYONE)
                .setDenied(PermissionType.MOVE_MEMBERS)
                .setDenied(PermissionType.MUTE_MEMBERS)
                .setDenied(PermissionType.PRIORITY_SPEAKER)
                .setAllowed(PermissionType.READ_MESSAGE_HISTORY)
                .setAllowed(PermissionType.READ_MESSAGES)
                .setAllowed(PermissionType.SEND_MESSAGES)
                .setDenied(PermissionType.SEND_TTS_MESSAGES)
                .setAllowed(PermissionType.SPEAK)
                .setAllowed(PermissionType.STREAM)
                .setAllowed(PermissionType.USE_EXTERNAL_EMOJIS)
                .setAllowed(PermissionType.USE_VOICE_ACTIVITY)
                .setDenied(PermissionType.VIEW_AUDIT_LOG)
                .build()
                ;

        Role role = new RoleBuilder(event.getServer().get())
                .setColor(new Color(194, 98, 94))
                .setAuditLogReason("MCDC: Minecraft Server Role creation")
                .setDisplaySeparately(false)
                .setMentionable(true)
                .setName(command.getStringArgument(0).get())
                .setPermissions(permissions)
                .create().join();

        event.getChannel().sendMessage(successEmbed.addField("New Role", "The new role is: " + role.getMentionTag() + "!")
        .addField("Usage:","Give the role to any members that should be allowed to view and write to the minecraft channel. Later this will get added automatically with linking!"));
        // TODO add linking and automatic linking of roles.

        info("Created new Role " + ChatColor.GRAY + role.getName() + ChatColor.RESET + " on server " + ChatColor.RESET + event.getServer().get().getName() + "!");

        discordApi.getYourself().addRole(role, "MCDC needs to see the channel as well!");
        info("Added role \"" + role.getName() + "\" to the discord API.");

        event.getMessageAuthor().asUser().ifPresent(user -> {
            user.addRole(role, "MCDC role creation: Person who created the role should get the role assigned, as well.");
            info("Added role \"" + role.getName() + "\" to player \"" + user.getName() + "\".");
        });
    }
}
