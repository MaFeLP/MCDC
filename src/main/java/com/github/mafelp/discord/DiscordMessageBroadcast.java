package com.github.mafelp.discord;

import com.github.mafelp.minecraft.skins.Skin;
import com.github.mafelp.utils.Settings;
import org.bukkit.entity.Player;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.List;

/**
 * The Thread to handle sending of discord messages, to not hang the server on Message Sending.
 */
public class DiscordMessageBroadcast extends Thread {
    /**
     * The person, who sent the message.
     */
    private final Player messageAuthor;

    /**
     * What the player has sent.
     */
    private final String message;

    /**
     * Constructor to give the function all needed information.
     * @param messageAuthor The person, who sent the message.
     * @param message The message, the messageAuthor has sent.
     */
    public DiscordMessageBroadcast(Player messageAuthor, String message) {
        this.messageAuthor = messageAuthor;
        this.message = message;
    }

    /**
     * The executing method to run the thread and sent all the messages.
     */
    @Override
    public void run() {
        // get all channels, where the message should be send to
        List<Channel> channels = ChannelAdmin.getMessageChannels();

        // if the list is empty, return a failure and log an error
        if (channels.isEmpty()) {
            Settings.minecraftServer.getLogger().warning("Could not broadcast message: No Channels were selected");
            return;
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
    }
}
