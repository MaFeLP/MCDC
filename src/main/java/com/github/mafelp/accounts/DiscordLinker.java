package com.github.mafelp.accounts;

import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class DiscordLinker {
    public static Map<User, Integer> linkableAccounts = new HashMap<>();

    public static int getLinkToken(User user) {
        if (linkableAccounts.containsKey(user))
            return linkableAccounts.get(user);
        else {
            int linkID = randomLinkToken();
            linkableAccounts.put(user, linkID);
            return linkID;
        }
    }

    public static Optional<Account> linkToDiscord(Player player, int linkID) {
        for (User u: linkableAccounts.keySet()) {
            if (linkableAccounts.get(u) == linkID) {
                Account account = new Account(u, player);

                AccountManager.addAccount(account);

                return Optional.of(account);
            }
        }

        return Optional.empty();
    }

    public static int randomLinkToken() {
        Random random = new Random();
        int r;
        do {
            r = random.nextInt(899_999) + 100_000;
        } while (linkableAccounts.containsValue(r));

        return r;
    }
}
