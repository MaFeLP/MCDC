package com.github.mafelp.accounts;

import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;

import java.util.UUID;

public class Account {
    private final User user;
    private final long userID;
    private String username;
    private final String mentionTag;

    private final Player player;
    private final UUID playerUUDI;

    public Account(User user, Player player) {
        this.user = user;
        this.userID = user.getId();
        this.username = user.getName();
        this.mentionTag = user.getMentionTag();

        this.player = player;
        this.playerUUDI = player.getUniqueId();
    }

    public User getUser() {
        return user;
    }

    public long getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public Account setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getMentionTag() {
        return mentionTag;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getPlayerUUDI() {
        return playerUUDI;
    }
}
