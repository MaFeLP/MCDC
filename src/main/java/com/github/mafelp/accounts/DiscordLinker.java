package com.github.mafelp.accounts;

import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * The class that handles linking on the Discord side of things.
 */
public class DiscordLinker {
    /**
     * The Map that contains as key the {@link Player} that created the link token and as the Value an {@link Integer}
     * between 100,000 and 999,999.
     */
    private static final Map<User, Integer> linkableAccounts = new HashMap<>();

    /**
     * Checks the {@link DiscordLinker#linkableAccounts} list, if the {@link User} already has a linking token and
     * if so, it returns the token from this map. If the user does not have a token yet, it creates one and
     * adds it to the {@link DiscordLinker#linkableAccounts} map, associated with the user.
     * @param user The user to get the Linking Token from.
     * @return The Token used to link the account in minecraft.
     */
    public static int getLinkToken(User user) {
        if (linkableAccounts.containsKey(user))
            return linkableAccounts.get(user);
        else {
            int linkID = randomLinkToken();
            linkableAccounts.put(user, linkID);
            return linkID;
        }
    }

    /**
     * The method used to create a linked {@link Account} with a discord user and the linking ID of a minecraft Player.
     * @param player The minecraft {@link Player} to link the discord {@link User} to.
     * @param linkID The ID used for finding the correct discord {@link User}.
     * @return If the ID is a valid token, it returns the newly {@link Account}, which was added to the list of accounts.
     *      If the ID is invalid, it returns an empty account.
     */
    public static Optional<Account> linkToMinecraft(Player player, int linkID) {
        for (User u: linkableAccounts.keySet()) {
            if (linkableAccounts.get(u) == linkID) {
                Account account = new Account(u, player);

                AccountManager.addAccount(account);

                return Optional.of(account);
            }
        }

        return Optional.empty();
    }

    /**
     * The method used to create a new Linking token, aka a {@link Random}, 6-Digit number.
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
}
