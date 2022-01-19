package com.github.mafelp.discord;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.minecraft.skins.Skin;
import com.github.mafelp.utils.Settings;
import org.bukkit.entity.Player;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jetbrains.annotations.Nullable;

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
     * The command that is being executed.
     */
    private final String command;

    /**
     * What the player has sent.
     */
    private final String message;

    /**
     * The type of the broadcast.
     */
    private final BroadcastType broadcastType;

    /**
     * Constructor to define this thread as a message broadcast.
     * @param messageAuthor The person, who sent the message.
     * @param message The message, the messageAuthor has sent.
     */
    public DiscordMessageBroadcast(Player messageAuthor, String message) {
        this.messageAuthor = messageAuthor;
        this.message = message;

        this.broadcastType = BroadcastType.chatMessageBroadcast;

        this.command = null;
    }

    /**
     * Constructor to define this thread as a command-info broadcast by the server.
     * @param command The command that was executed.
     */
    public DiscordMessageBroadcast(String command) {
        this.command = command;

        this.broadcastType = BroadcastType.serverCommandBroadcast;

        this.messageAuthor = null;
        this.message = null;
    }

    /**
     * The constructor to define this thread as a command-info broadcast by a player.
     * @param command The command the player executed.
     * @param player The player who executed the command.
     */
    public DiscordMessageBroadcast(String command, Player player) {
        this.command = command;
        this.messageAuthor = player;

        this.broadcastType = BroadcastType.playerCommandBroadcast;

        this.message = null;
    }

    /**
     * The constructor to define this thread as an event broadcast for discord channel.
     * @param event The event that has happened
     * @param message The message to accompany this event
     * @param messageAuthor The player that might be referenced in this event
     */
    public DiscordMessageBroadcast(String event, String message, @Nullable Player messageAuthor) {
        this.command = event;
        this.message = message;
        this.messageAuthor = messageAuthor;
        this.broadcastType = BroadcastType.eventBroadcast;
    }

    /**
     * The executing method to run the thread and sent all the messages.
     */
    @Override
    public void run() {
        switch (this.broadcastType) {
            case chatMessageBroadcast -> messageBroadcast();
            case playerCommandBroadcast -> playerCommandBroadcast();
            case serverCommandBroadcast -> serverCommandBroadcast();
            case eventBroadcast -> eventBroadcast();
        }
    }

    /**
     * Sends the give embed to all configured channels.
     * @param embed The embed to send to the different channels.
     */
    private void sendMessages(EmbedBuilder embed) {
        // get all channels, where the message should be send to
        List<Channel> channels = ChannelAdmin.getMessageChannels();

        // if the list is empty, return a failure and log an error
        if (channels.isEmpty()) {
            Settings.minecraftServer.getLogger().warning("Could not broadcast message: No Channels were selected");
            return;
        }

        // Send the message to each channel, if it is a text channel.
        channels.forEach(channel ->
                channel.asTextChannel().ifPresent(textChannel ->
                        textChannel.sendMessage(embed)
                )
        );
    }

    /**
     * The method to send a chat message.
     */
    private void messageBroadcast() {
        // create an embed for the message
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setDescription(message);

        if (Account.getByPlayer(messageAuthor).isPresent())
            embed.setAuthor(Account.getByPlayer(messageAuthor).get().getUser());
        else {
            assert messageAuthor != null;
            embed.setAuthor(messageAuthor.getDisplayName(), "https://namemc.com/profile/" + messageAuthor.getName(), new Skin(messageAuthor, false).getHead(), ".png");
        }
        if (Settings.getConfiguration().getBoolean("showFooterInMessages", true))
            embed.setFooter("On " + Settings.serverName);

        sendMessages(embed);
    }

    /**
     * The method to send a server command embed.
     */
    private void serverCommandBroadcast() {
        // create an embed for the message
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0xA245F3))
                .setAuthor(Settings.discordApi.getYourself())
                .setTitle("Server Command was executed!")
                .setDescription(command);

        if (Settings.getConfiguration().getBoolean("showFooterInMessages", true))
            embed.setFooter("On " + Settings.serverName);

        sendMessages(embed);
    }

    /**
     * The method to send a player command embed.
     */
    private void playerCommandBroadcast() {
        assert messageAuthor != null;

        // create an embed for the message
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0x5645F3))
                .setAuthor(Settings.discordApi.getYourself())
                .setTitle("Player " + messageAuthor.getName() + " executed a command!")
                .setDescription(command);

        if (Account.getByPlayer(messageAuthor).isPresent())
            embed.setAuthor(Account.getByPlayer(messageAuthor).get().getUser());
        else
            embed.setAuthor(messageAuthor.getDisplayName(), "https://namemc.com/profile/" + messageAuthor.getName(), new Skin(messageAuthor, false).getHead(), ".png");

        if (Settings.getConfiguration().getBoolean("showFooterInMessages", true))
            embed.setFooter("On " + Settings.serverName);

        sendMessages(embed);
    }

    private void eventBroadcast() {
        // create an embed for the message
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0x17A288))
                .setAuthor(Settings.discordApi.getYourself())
                .setTitle(command)
                .setDescription(message);

        if (messageAuthor != null) {
            if (Account.getByPlayer(messageAuthor).isPresent())
                embed.setAuthor(Account.getByPlayer(messageAuthor).get().getUser());
            else
                embed.setAuthor(messageAuthor.getDisplayName(), "https://namemc.com/profile/" + messageAuthor.getName(), new Skin(messageAuthor, false).getHead(), ".png");
        }

        if (Settings.getConfiguration().getBoolean("showFooterInMessages", true))
            embed.setFooter("On " + Settings.serverName);

        sendMessages(embed);
    }
}

/**
 * The type of the broadcast.
 */
enum BroadcastType {
    /**
     * A player executed a command.
     */
    playerCommandBroadcast,
    /**
     * The server executed a command.
     */
    serverCommandBroadcast,
    /**
     * A player sent a normal message.
     */
    chatMessageBroadcast,

    /**
     * If an event was emitted
     */
    eventBroadcast
}