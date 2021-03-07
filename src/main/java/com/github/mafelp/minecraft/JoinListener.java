package com.github.mafelp.minecraft;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import static com.github.mafelp.Settings.prefix;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.getPlayer().sendMessage(prefix +
                ChatColor.AQUA +
                "Hello " + ChatColor.GREEN + playerJoinEvent.getPlayer().getDisplayName()  + ChatColor.AQUA + "!\n" +
                ChatColor.RED +
                "    This server is using MCDC version 0.1-beta!\n" +
                "    Be aware that any chat messages that you send are going to be send to a discord channel!" +
                ChatColor.RESET);
    }

}
