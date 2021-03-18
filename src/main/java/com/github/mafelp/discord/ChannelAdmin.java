package com.github.mafelp.discord;

import com.github.mafelp.Settings;
import com.github.mafelp.minecraft.skins.Skin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        // Server[] servers = Settings.discordApi.getServers().toArray(new Server[0]);
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
     * @param category category in which the channel should be created
     * @param topic the topic the channel should have
     * @throws InterruptedException thrown when the thread is being stopped before the waiting loop could be fulfilled
     * @return success state
     */
    // type might change to ServerTextChannel later on
    public static boolean createChannel(String name, Server server, ChannelCategory category, String topic) throws InterruptedException {
        // TODO add channel creation support
        CompletableFuture<ServerTextChannel> serverTextChannelCompletableFuture = new ServerTextChannelBuilder(server)
                .setName(name)
                .setCategory(category)
                .setTopic(topic)
                .setAuditLogReason("Creating Channel for communication with Minecraft Server")
                .create();

        // wait for the channel to become available
        int i = 0;
        // getting the state of the channel
        while (!serverTextChannelCompletableFuture.isDone() && i < 10) {
            // if channel is not present, print log an info and wait 10ms
            Settings.minecraftServer.getLogger().info(Settings.prefix + "Channel creation not complete. Waiting 10 ms...");
            i++;
            Thread.sleep(10);
        }

        // try to get the server text channel
        // might be the return value in the future
        try {
            ServerTextChannel serverTextChannel = serverTextChannelCompletableFuture.get();
            // TODO Add: channel id to the list of channels where to broadcast the messages to
            // TODO Add: Only the role specified in RoleAdmin can see and write to the channel
            // return a success
            return true;
        } catch (InterruptedException | ExecutionException e) {
            Settings.minecraftServer.getLogger().warning(Settings.prefix + ChatColor.RED +
                    "Something went while trying to create a channel! Error: " + e.getMessage());
            // return a failure
            return false;
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
