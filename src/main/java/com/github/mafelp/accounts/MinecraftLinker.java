package com.github.mafelp.accounts;

import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class MinecraftLinker {
    public static Map<Player, Integer> linkableAccounts = new HashMap<>();

    public static int getLinkToken(Player player) {
        if (linkableAccounts.containsKey(player))
            return linkableAccounts.get(player);
        else {
            int linkID = randomLinkToken();
            linkableAccounts.put(player, linkID);
            return linkID;
        }
    }

    public static int randomLinkToken() {
        Random random = new Random();
        int r;
        do {
            r = random.nextInt(899_999) + 100_000;
        } while (linkableAccounts.containsValue(r));

        return r;
    }

    public static Optional<Account> linkToDiscord(User user, int linkID) {
        for (Player p: linkableAccounts.keySet()) {
            if (linkableAccounts.get(p) == linkID) {
                Account account = new Account(user, p);

                AccountManager.addAccount(account);

                return Optional.of(account);
            }
        }

        return Optional.empty();
    }
}
