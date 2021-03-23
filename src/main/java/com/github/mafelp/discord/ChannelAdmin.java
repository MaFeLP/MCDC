package com.github.mafelp.discord;

import com.github.mafelp.Logging;
import com.github.mafelp.Settings;
import com.github.mafelp.minecraft.skins.Skin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The class that manages the discord channels, whose messages are relayed to the
 * minecraft server and vise versa
 */
public class ChannelAdmin {
    /**
     * gets all the channels whose IDs were defined in the config.yml
     * @return list of all channels of the ids
     */
    private static List<Channel> getMessageChannels() {
        // creating new list to return
        List<Channel> serverTextChannels = new ArrayList<>();

        // for each id in channel IDs in the config.yml (ID is a long)
        for (long l : Settings.getConfiguration().getLongList("channelIDs")) {
            // check if the channel of the id is present/exists
            if (Settings.discordApi.getChannelById(l).isPresent()) {
                // if it exists, add it to the list of channels
                serverTextChannels.add(Settings.discordApi.getChannelById(l).get());
            } else {
                // if it does not exist, log an error to the console
                Settings.minecraftServer.getLogger().warning(Settings.prefix + ChatColor.RED +
                        "Could not get Server channel, channel id: " + l);
            }
        }

        return serverTextChannels;
    }

    /**
     * Creates a channel to send the minecraft messages to.
     * @param name name of the channel
     * @param server server on which to create the channel on
     * @param topic the topic the channel should have
     * @param successEmbed the embed to be sent into successChannel after completion
     * @param successChannel the channel successEmbed is sent to
     * @param welcomeEmbed the embed to sent to the newly created channel
     * @return the newly created channel
     */
    public static ServerTextChannel createChannel(String name, Server server, String topic,
                                                  EmbedBuilder successEmbed, ServerTextChannel successChannel,
                                                  EmbedBuilder welcomeEmbed) {
        try {
            // return value
            AtomicReference<ServerTextChannel> out = new AtomicReference<>();

            // Create the Channel
            new ServerTextChannelBuilder(server)
                    .setName(name)
                    .setTopic(topic)
                    .setAuditLogReason("Creating Channel for communication with Minecraft Server")
                    .create()
                    .thenAccept(serverTextChannel -> {
                        // After the channel has been created
                        // TODO Add: Only the role specified in RoleAdmin can see and write to the channel
                        Logging.info("Added channel " + serverTextChannel.getName() + " to server " + serverTextChannel.getServer().getName());

                        // Add a field containing a link to the new channel and send the embed
                        successEmbed.addField("New Channel",
                                "The new channel is: <#" + serverTextChannel.getIdAsString() + ">");
                        successChannel.sendMessage(successEmbed);

                        // Also send the welcome embed into the newly created channel
                        serverTextChannel.sendMessage(welcomeEmbed);

                        // Set return channel to the newly created one
                        out.set(serverTextChannel);

                        // Add the id of the new channel to the list of ids in the configuration and save/reload it
                        List<Long> ids = Settings.getConfiguration().getLongList("channelIDs");
                        ids.add(serverTextChannel.getId());
                        Settings.getConfiguration().set("channelIDs", ids);
                    });

            return out.get();
        } catch (Exception exception) {
            Logging.logException(exception, "Something went wrong while trying to create a text channel.");
            return null;
        }

    }

    /**
     * Sends an embed with the message string to all channels returned by getMessageChannels()
     * @param messageAuthor messageAuthor who sent the message to the minecraft chat
     * @param message the message String to broadcast to the channels
     * @return success state
     */
    public static boolean broadcastMessage(Player messageAuthor, String message) {
        // get all channels, where the message should be send to
        List<Channel> channels = getMessageChannels();

        // if the list is empty, return a failure and log an error
        if (channels.isEmpty()) {
            Settings.minecraftServer.getLogger().warning("Could not broadcast message: No Channels were selected");
            return false;
        }

        // create an embed for the message
        //TODO Add: show head of player as author picture
        EmbedBuilder embed = new EmbedBuilder()
             // .setAuthor(messageAuthor.getDisplayName())
                .setAuthor(messageAuthor.getDisplayName(), "", new Skin(messageAuthor, false).getHead(), ".png")
                .setColor(Color.YELLOW)
                .setFooter("On " + Settings.serverName)
                .addInlineField("Message:", message);

        // send the embed to all channels in the list
        for (Channel channel : channels) {
            // only send the embed, if the channel is present, to avoid exceptions
            if (channel.asServerTextChannel().isPresent()) {
                channel.asServerTextChannel().get().sendMessage(embed);
            }
        }

        // return a success
        return true;
    }
}
