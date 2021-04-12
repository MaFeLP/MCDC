package com.github.mafelp.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.io.*;
import java.util.Scanner;

import static com.github.mafelp.utils.Logging.debug;
import static com.github.mafelp.utils.Settings.getConfiguration;

/**
 * Class used to check permissions on Discord or on the Minecraft server
 */
public class CheckPermission {

    /**
     * Gets the embed to server to the user, when he/she does not have the required permissions for something.
     * @param messageAuthor The person who sent the message and should be shown in the header.
     * @param action The action the person tried to do. Will be inserted into embed.
     * @return an embed to sent to the person.
     */
    public static EmbedBuilder getPermissionDeniedEmbed(final MessageAuthor messageAuthor, final String action) {
        return new EmbedBuilder()
                .setAuthor(messageAuthor)
                .setTitle("Error")
                .addField("Permission denied!","Sorry, you do not have the required permission to " + action + "! Please ask the bot administrator and/or server owner, if you believe this is a mistake.")
                .setColor(Color.RED.darker())
                .setFooter("PermissionDeniedError")
        ;
    }

    /**
     * Checks if a user is a server admin or in the <code>permission.adminIDs</code> list in the <code>config.yml</code>
     * file.
     * @param messageAuthor The user to check the permissions from.
     * @return if the person is authorized to do so.
     */
    public static boolean hasAdminPermission (final MessageAuthor messageAuthor) {
        // Checking if the sender has the required permissions

        if (messageAuthor.isServerAdmin())
            return true;

        if (messageAuthor.isBotOwner())
            return true;

        for (long person :
                getConfiguration().getLongList("permission.adminIDs")) {
            if (messageAuthor.getId() == person)
                return true;
        }

        return false;
    }

    /**
     * Checks if a user a server admin or in the <code>permissions.botAdminIDs</code> list in the
     * <code>config.yml</code> file.
     * @param messageAuthor the user to check the permission from
     * @return if the user is authorized to do so.
     */
    public static boolean isBotAdmin (final MessageAuthor messageAuthor) {
        // Checking if the sender has the required permissions

        if (messageAuthor.isBotOwner())
            return true;

        for (long person : getConfiguration().getLongList("permission.botAdminIDs")) {
            if (messageAuthor.getId() == person)
                return true;
        }

        return false;
    }

    /**
     * Gets the admin Level of a player/uuid from the ops.json file in the root directory of the minecraft server.
     * @param player The player to check the level of.
     * @return the OP level of the player. If non could be found, return a zero.
     */
    public static short getAdminLevel (final Player player) {
        JsonParser parser = new JsonParser();

        // Reads the content of the ops.json file into the stringBuilder
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File("./ops.json"));

            while (scanner.hasNextLine())
                stringBuilder.append(scanner.nextLine());

            scanner.close();
        } catch (FileNotFoundException e) {
            // This catch case is never going to get invoked, because the server
            // itself always creates the ops.json file (or it wouldn't start at first)
            Logging.logException(e, "Error whilst trying to get the operator file.");
            return 0;
        }

        // Gets the contents of the ops.json file as a JSONArray,
        // because multiple players are stored in this file.
        JsonArray jsonObject = parser.parse(stringBuilder.toString()).getAsJsonArray();

        debug("Getting permission level for Player " + player.getDisplayName());

        for (JsonElement jo : jsonObject) {
            // Logging.debug("In Loop");

            final String uuid = jo.getAsJsonObject().get("uuid").getAsString();
            final short opLevel = jo.getAsJsonObject().get("level").getAsShort();

            debug("UUID: " + uuid + "; opLevel: " + opLevel);

            if (player.getUniqueId().toString().equalsIgnoreCase(uuid))
                return opLevel;
        }

        return 0;
    }

    /**
     * Checks if a player has a specific admin level.
     * @param player the player to check the level of.
     * @param level the minimum level required for the player to have for this equation to be true.
     * @return if the player has the required level.
     */
    public static boolean hasAdminLevel(final Player player, final int level) {
        return getAdminLevel(player) >= level;
    }

    /**
     * Checks the configuration if the user id is in the configuration path.
     * @param permission The permission to have.
     * @param id The id of the user to check the permission of
     * @return if the permission is granted or not.
     */
    public static boolean checkPermission(final Permissions permission, final long id) {
        debug("searching configuration for id " + id + "...");
        debug("looking for long list " + "permissions." + permission.toString() + ".allowedUserIDs");
        debug("The long list contents are: " + Settings.getConfiguration().getLongList("permission." + permission + ".allowedUserIDs"));
        for (long configID : Settings.getConfiguration().getLongList("permission." + permission + ".allowedUserIDs")) {
            if (configID == id)
                return true;
        }

        return false;
    }

    /**
     * Checks the configuration if a player has a specific permission or has a specific level.
     * @param permission The permission to check the level of.
     * @param player the player to check the permission of.
     * @return if the player ahs the required permission.
     */
    public static boolean checkPermission(final Permissions permission, final Player player) {
        String UUID = player.getUniqueId().toString();
        int opLevel = getAdminLevel(player);

        int requiredPermissionLevel = Settings.getConfiguration().getInt("permission." + permission.toString() + ".level");

        // Check the OP Level
        if (opLevel >= requiredPermissionLevel) {
            // debug("Granted permission with OP level. Level: " + opLevel + "; requiredOPLevel: " + requiredPermissionLevel);
            return true;
        }

        // Check the configuration
        for (String configUUID : Settings.getConfiguration().getStringList("permission." + permission + ".allowedUserUUIDs")) {
            if (configUUID.equalsIgnoreCase(UUID)) {
                // debug("Granted permission with wildcard.");
                return true;
            }
        }

        return false;
    }
}