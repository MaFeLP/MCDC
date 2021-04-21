package com.github.mafelp.accounts;

import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;

import java.io.*;
import java.util.*;

public class AccountManager {
    private static List<Account> linkedAccounts = new ArrayList<>();

    private static final File accountFile = new File(Settings.getConfigurationFileDirectory(), "accounts.json");

    private static final JsonParser jsonParser= new JsonParser();

    public static void createAccountsFile() throws IOException {
        if (accountFile.exists()) {
            Logging.info("accounts file " + accountFile.getAbsolutePath() + " already exists. Not overwriting it.");
        } else {
            boolean fileCreationSuccess = accountFile.createNewFile();
            Logging.info("Accounts file creation... Success: " + fileCreationSuccess);

            if (!fileCreationSuccess)
                return;

            PrintStream printStream = new PrintStream(new FileOutputStream(accountFile));
            printStream.println("[]");
            printStream.close();
        }
    }

    public static void saveAccounts() throws FileNotFoundException {
        JsonArray accounts = new JsonArray();

        for (Account account: linkedAccounts) {
            JsonObject accountInfo = new JsonObject();

            accountInfo.addProperty("discordID", account.getUserID());
            accountInfo.addProperty("username", account.getUsername());
            accountInfo.addProperty("discordMentionTag", account.getMentionTag());

            accountInfo.addProperty("minecraftUUID", account.getPlayerUUDI().toString());

            accounts.add(accountInfo);
        }

        PrintStream printStream = new PrintStream(new FileOutputStream(accountFile));
        printStream.print(accounts);
        printStream.close();
    }

    public static void loadAccounts() {
        if (!accountFile.exists())
            return;

        Thread accountLoaderThread = new AccountLoader();
        accountLoaderThread.setName("AccountLoader");
        accountLoaderThread.start();
    }

    public static List<Account> getLinkedAccounts() {
        return linkedAccounts;
    }

    public static List<Account> setLinkedAccounts(List<Account> set) {
        linkedAccounts = set;
        return linkedAccounts;
    }
}
