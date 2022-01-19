package com.github.mafelp.minecraft.listeners;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Permissions;
import com.github.mafelp.utils.Settings;
import com.github.mafelp.minecraft.skins.Skin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.github.mafelp.utils.Logging.debug;
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
         * Send a welcome message to the joined player if he/she does not have an account.
         *
         * This message informs the player about this plugin and
         * informs him/her, that every messages he/she sends are
         * being transmitted to a discord channel.
         */
        if (Account.getByPlayer(playerJoinEvent.getPlayer()).isEmpty()) {
            playerJoinEvent.getPlayer().sendMessage(
                    // Adding the plugin prefix in front of the message
                    prefix + ChatColor.AQUA +
                            // Greeting the player with his/her name
                            "Hello " + ChatColor.GREEN + playerJoinEvent.getPlayer().getDisplayName() + ChatColor.AQUA + "!\n" +
                            // Adding the warning
                            ChatColor.GOLD +
                            "    This server is using MCDC version " + Settings.version + "!\n" +
                            ChatColor.RED +
                            "    Be aware that any chat messages that you send are going to be send to a discord channel!\n" +
                            ChatColor.AQUA +
                            "    To create a link with your discord account, use " + ChatColor.GRAY + "/link" +
                            ChatColor.RESET
            );

            // This check must only be performed, when a new player has joined. New Players cannot have an account,
            // so it would be useless to perform this check on players with an account.
            String usernameToCheck = "@" + playerJoinEvent.getPlayer().getName();
            if (Account.getByUsername(usernameToCheck).isPresent()) {
                String usernameToSet = "@" + Account.getByUsername(usernameToCheck).get().getPlayer().getName();
                Account accountToResetTheNameOf = Account.getByUsername(usernameToCheck).get();
                Account.getByUsername(usernameToCheck).get().setUsername(usernameToSet);
                Logging.info("Due to a new player having joined the server with the same name as an account had, the name of the old account " + usernameToCheck + "has been reset to " + ChatColor.GRAY + usernameToSet);
                if (accountToResetTheNameOf.getPlayer().getPlayer() != null && accountToResetTheNameOf.getPlayer().isOnline()) {
                    accountToResetTheNameOf.getPlayer().getPlayer().sendMessage(prefix + ChatColor.YELLOW + "Your account name has been reset, due to a player with the same name joining the server.");
                }
            }
        }

        // Downloads the skin from Mojang
        new Skin(playerJoinEvent.getPlayer(), true);

        debug("Player \"" + playerJoinEvent.getPlayer().getDisplayName() + "\" has the permissions: " +
                "configEdit: " + CheckPermission.checkPermission(Permissions.configEdit, playerJoinEvent.getPlayer())
        );

        // TODO add discord message updating with online players.
    }
}
