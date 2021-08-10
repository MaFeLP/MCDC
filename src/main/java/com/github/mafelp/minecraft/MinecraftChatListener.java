package com.github.mafelp.minecraft;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.discord.ChannelAdmin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

/**
 * Class that reacts to chat messages sent on the minecraft server.
 */
public class MinecraftChatListener implements Listener {

    /**
     * Method called by the minecraft server when a chat message is being sent
     * @param event class containing information about the message
     */
    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        // Getting information about the message
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Replaces Account usernames with their matching MentionTag so the user will be pinged in the Message.
        String[] messageParts = message.split(" ");
        message = "";
        for (String s : messageParts) {
            Optional<Account> a = Account.getByUsername(s);
            if (a.isPresent())
                message += a.get().getMentionTag() + " ";
            else
                message += s + " ";
        }

        // Hand the sending of the message to the dedicated method.
        ChannelAdmin.broadcastMessage(player, message);
    }
}
