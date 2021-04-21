package com.github.mafelp.accounts;

import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;

import java.util.UUID;

/**
 * The method that stores all necessary information about an account with linked discord user and
 * minecraft player.
 */
public class Account {
    /**
     * The discord user
     */
    private final User user;

    /**
     * the discord ID of the {@link Account#user}.
     */
    private final long userID;

    /**
     * The name of the account. The default is the discord name of the {@link Account#user}.
     */
    private String username;

    /**
     * The tag used to mention a Discord {@link User} in a discord message.
     */
    private final String mentionTag;

    /**
     * The minecraft {@link Player} to link the discord {@link User} to.
     */
    private final Player player;

    /**
     * The UUID of the minecraft {@link Account#player}.
     */
    private final UUID playerUUID;

    /**
     * The constructor to create an account from a minecraft {@link Account#playerUUID} and a discord
     * {@link Account#userID}.
     * @param user The discord {@link User} to link this account to.
     * @param player The minecraft {@link Player} to link this account to.
     */
    public Account(User user, Player player) {
        this.user = user;
        this.userID = user.getId();
        this.username = user.getName();
        this.mentionTag = user.getMentionTag();

        this.player = player;
        this.playerUUID = player.getUniqueId();
    }

    /**
     * The getter for the {@link Account#user}.
     * @return The {@link Account#user} field.
     */
    public User getUser() {
        return user;
    }

    /**
     * The getter for the {@link Account#userID}.
     * @return The {@link Account#userID} field.
     */
    public long getUserID() {
        return userID;
    }

    /**
     * The getter for the {@link Account#username}.
     * @return The {@link Account#username} field.
     */
    public String getUsername() {
        return username;
    }

    /**
     * The setter for the {@link Account#username}.
     * @return The {@link Account#username} field.
     */
    public Account setUsername(String username) {
        this.username = username;
        return this;
    }
    public String getMentionTag() {
        return mentionTag;
    }

    /**
     * The getter for the {@link Account#player}.
     * @return The {@link Account#player} field.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * The getter for the {@link Account#playerUUID}.
     * @return The {@link Account#playerUUID} field.
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
