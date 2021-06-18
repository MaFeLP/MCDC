package com.github.mafelp.accounts;

import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * The class that handles linking on the minecraft side of things.
 */
public class MinecraftLinker {
    /**
     * The Map that contains as key the {@link Player} that created the link token and as the Value an {@link Integer}
     * between 100,000 and 999,999.
     */
    private static final Map<Player, Integer> linkableAccounts = new HashMap<>();

    /**
     * Checks the {@link MinecraftLinker#linkableAccounts} list, if the {@link Player} already has a linking token and
     * if so, it returns the token from this map. If the player does not have a token yet, it creates one and
     * adds it to the {@link MinecraftLinker#linkableAccounts} map, associated with the player.
     * @param player The player to get the Linking Token from.
     * @return The Token used to link the account in discord.
     */
    public static int getLinkToken(Player player) {
        if (Account.getByPlayer(player).isPresent())
            return -1;

        if (linkableAccounts.containsKey(player))
            return linkableAccounts.get(player);
        else {
            int linkID = randomLinkToken();
            linkableAccounts.put(player, linkID);
            return linkID;
        }
    }

    /**
     * The method used to create a new Linking token, aka a {@link Random}, 6-Digit number, that is not already
     * in the linkableAccounts list. This prevents two users from having the same linking token.
     * @return a random linking token.
     */
    private static int randomLinkToken() {
        Random random = new Random();
        int r;
        do {
            r = random.nextInt(899_999) + 100_000;
        } while (linkableAccounts.containsValue(r));

        return r;
    }

    /**
     * The method used to create a linked {@link Account} with a discord user and the linking ID of a minecraft Player.
     * @param user The discord {@link User} to link the minecraft {@link Player} to.
     * @param linkID The ID used for finding the correct minecraft {@link Player}.
     * @return If the ID is a valid token, it returns the newly {@link Account}, which was added to the list of accounts.
     *      If the ID is invalid, it returns an empty account.
     */
    public static Optional<Account> linkToDiscord(User user, int linkID) {
        // Iterates over all the discord accounts
        for (Player p: linkableAccounts.keySet()) {
            // if the linkID passed in matches the id generated in the DiscordLinker#getLinkToken function,
            // it would create a new Account with the player and the User and adds this account.
            if (linkableAccounts.get(p) == linkID) {
                Account account = new Account(user, p);

                AccountManager.addAccount(account);

                //removes the current linkID from the map, so the link id would be freed again.
                linkableAccounts.remove(p);

                return Optional.of(account);
            }
        }

        return Optional.empty();
    }
}
