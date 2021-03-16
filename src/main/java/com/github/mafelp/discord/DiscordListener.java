package com.github.mafelp.discord;

import com.github.mafelp.Settings;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import static com.github.mafelp.Settings.msgPrefix;

public class DiscordListener implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (!event.getMessageAuthor().isYourself()) {
            Settings.minecraftServer.broadcastMessage(msgPrefix(event) +
                    event.getReadableMessageContent());
        }
    }
}
