package com.github.mafelp.discord;

import com.github.mafelp.utils.Settings;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import static com.github.mafelp.utils.Settings.msgPrefix;

/**
 * Class that listens to all messages sent to a discord server
 * on which the bot is present on and can see the channels or
 * via direct message.
 *
 * Handles Communication: discord -> Minecraft
 */
public class DiscordListener implements MessageCreateListener {
    /**
     * Method called by the discord bot api when a message is received.
     * @param event Message create event containing information about the message.
     */
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        /*
         * Only handles messages not sent by the bot.
         *
         * This prevents the bot from sending a message into the discord
         * and then sending its own message into the minecraft chat again,
         * where the message was originally sent from.
         */
        if (event.getMessageAuthor().isYourself()) {
            return;
        }

        /*
         * Also only handles messages that are no commands.
         *
         * This prevents from sending messages that should not be known by the players
         * and only by the bot/server owner.
         */
        if (event.getReadableMessageContent().startsWith(Settings.discordCommandPrefix))
            return;

        // Send the readable content of the message into the minecraft chat
        // for everyone to read.
        // TODO broadcast version of message WITHOUT line break to the console and messages with line breaks to the players if Settings.shortMsg == true
        Settings.minecraftServer.broadcastMessage(
                msgPrefix(event) + event.getReadableMessageContent()
        );
    }
}
