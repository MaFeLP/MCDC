package com.github.mafelp.discord;

import org.bukkit.ChatColor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.*;
import org.javacord.api.entity.server.Server;

import java.awt.*;
import java.util.concurrent.CompletionException;

import static com.github.mafelp.utils.Logging.info;
import static com.github.mafelp.utils.Settings.discordApi;

/**
 * Class to manage the role that allows users to see the channel,
 * specified in ChannelAdmin
 */
public class RoleAdmin {
    /**
     * Method creates a new Role on a server with the specified name.
     * @param server The server to create the new role on.
     * @param name The name of the new role.
     * @param successEmbed The embed to sent the user on success.
     * @param successChannel The channel to sent the successEmbed to.
     * @return The newly created role
     */
    public static Role createNewRole(Server server, String name,
                                     EmbedBuilder successEmbed, ServerTextChannel successChannel) throws CompletionException {
        Permissions permissions = new PermissionsBuilder()
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

        Role role = new RoleBuilder(server)
                .setColor(new Color(194, 98, 94))
                .setAuditLogReason("MCDC: Minecraft Server Role creation")
                .setDisplaySeparately(false)
                .setMentionable(true)
                .setName(name)
                .setPermissions(permissions)
                .create().join();

        info("Created new Role " + ChatColor.GRAY + role.getName() + ChatColor.RESET + " on server " + ChatColor.RESET + server.getName() + "!");

        if (successEmbed != null)
            successChannel.sendMessage(successEmbed.addField("New Role", "The new role is: " + role.getMentionTag() + "!")
                    .addField("Usage:", "Give the role to any members that should be allowed to view and write to the minecraft channel. Later this will get added automatically with linking!"));

        discordApi.getYourself().addRole(role, "MCDC needs to see the channel as well!");
        info("Added role \"" + role.getName() + "\" to the discord API.");

        return role;
    }
}
