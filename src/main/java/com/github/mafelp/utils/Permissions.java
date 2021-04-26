package com.github.mafelp.utils;

/**
 * Permissions stored in the configuration with (levels and) wildcards for specific users.
 */
public enum Permissions {
    /**
     * The permission to edit the configuration file
     */
    configEdit,

    /**
     * The permission to edit and remove accounts from the command line.
     */
    accountEdit,

    /**
     * The permission to perform commands on teh discord servers.
     */
    discordServerAdmin,
    /**
     * The permission to perform commands with the bot, e. g. inviting.
     */
    discordBotAdmin,
}
