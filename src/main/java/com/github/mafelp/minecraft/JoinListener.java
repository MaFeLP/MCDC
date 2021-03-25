package com.github.mafelp.minecraft;

import com.github.mafelp.utils.Settings;
import com.github.mafelp.minecraft.skins.Skin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import static com.github.mafelp.utils.Settings.prefix;

/**
 * Class handling Join events of players joining the server
 */
public class JoinListener implements Listener {

    /**
     * Method called by the minecraft server when a player joins.
     * @param playerJoinEvent Class containing information about the event.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        /*
         * Send a welcome message to the joined player
         *
         * This message informs the player about this plugin and
         * informs him/her, that every messages he/she sends are
         * being transmitted to a discord channel.
         */
        playerJoinEvent.getPlayer().sendMessage(
                // Adding the plugin prefix in front of the message
                prefix + ChatColor.AQUA +
                // Greeting the player with his/her name
                "Hello " + ChatColor.GREEN + playerJoinEvent.getPlayer().getDisplayName()  + ChatColor.AQUA + "!\n" +
                // Adding the warning
                ChatColor.RED +
                "    This server is using MCDC version " + Settings.version + "!\n" +
                "    Be aware that any chat messages that you send are going to be send to a discord channel!" +
                ChatColor.RESET
        );

        // Downloads the skin from Mojang
        new Skin(playerJoinEvent.getPlayer(), true);

        // TODO add discord message updating with online players.
    }
}
