package com.github.mafelp.accounts;

import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.*;

/**
 * Manager for creating, saving and loading linked discord and minecraft accounts.
 */
public class AccountManager {
    /**
     * The list of all linked accounts
     */
    private static List<Account> linkedAccounts = new ArrayList<>();

    /**
     * The file in which the accounts are saved.
     */
    private static final File accountFile = new File(Settings.getConfigurationFileDirectory(), "accounts.json");

    /**
     * The method to create an account file.
     * @throws IOException the exception thrown, when the file could not be created, due to an error.
     */
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

    /**
     * The method that saves linkedAccounts to a JSON file.
     * @throws FileNotFoundException the exception thrown, when the file does not exists, we try to write to.
     */
    public static void saveAccounts() throws FileNotFoundException {
        JsonArray accounts = new JsonArray();

        for (Account account: linkedAccounts) {
            JsonObject accountInfo = new JsonObject();

            accountInfo.addProperty("discordID", account.getUserID());
            accountInfo.addProperty("username", account.getUsername());
            accountInfo.addProperty("discordMentionTag", account.getMentionTag());

            accountInfo.addProperty("minecraftUUID", account.getPlayerUUID().toString());

            accounts.add(accountInfo);
        }

        PrintStream printStream = new PrintStream(new FileOutputStream(accountFile));
        printStream.print(accounts);
        printStream.close();
    }

    /**
     * The method that handles starting of the thread, which should load the accounts in.
     * @throws IOException The exception that is being thrown, if the accounts.json File does not exists or the {@link AccountLoader} encounters an {@link IOException}.
     */
    public static void loadAccounts() throws IOException{
        if (!accountFile.exists())
            createAccountsFile();

        Thread accountLoaderThread = new AccountLoader();
        accountLoaderThread.setName("AccountLoader");
        accountLoaderThread.start();
    }

    /**
     * The getter for the list of Linked Accounts.
     * @return The linked Accounts list, currently used.
     */
    public static List<Account> getLinkedAccounts() {
        return linkedAccounts;
    }

    /**
     * The setter for the list of Linked Accounts. This should only be used by this package!
     * @param set The List to set the linked accounts to.
     * @return the list, this method has set, aka. the input list.
     */
    protected static List<Account> setLinkedAccounts(List<Account> set) {
        linkedAccounts = set;
        return linkedAccounts;
    }

    /**
     * Adds an Account to the list of linked accounts.
     * @param account The account to add
     * @return the list of all linked Accounts.
     */
    public static List<Account> addAccount (Account account) {
        linkedAccounts.add(account);
        return linkedAccounts;
    }
}
