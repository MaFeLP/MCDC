package com.github.mafelp.accounts;

import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.OfflinePlayer;
import org.javacord.api.entity.user.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * The thread, which loads the accounts into AccountManager.linkedAccounts.
 */
public class AccountLoader extends Thread{
    /**
     * The Json Parser used to parse Json from the Accounts File.
     */
    private static final JsonParser jsonParser = new JsonParser();

    /**
     * The file in which all the accounts are stored.
     */
    private static final File accountFile = new File(Settings.getConfigurationFileDirectory(), "accounts.json");

    /**
     * The method that runs the loading in another thread.
     */
    @Override
    public void run() {
        // Loads the accounts.json file in.
        Scanner scanner;
        try {
            scanner = new Scanner(accountFile);
        } catch (FileNotFoundException e) {
            Logging.logException(e, "Configuration file could not be found. Aborting Account loading.");
            return;
        }

        // Reads the contents of the accounts.json file
        Logging.debug("Start: Reading Accounts file in.");

        StringBuilder fileInput = new StringBuilder();

        while (scanner.hasNextLine()) {
            fileInput.append(scanner.nextLine());
        }

        String input = fileInput.toString();

        Logging.debug("accounts.json File input: " + input);
        Logging.debug("Trying to parse the accounts file input...");

        // Parses the file into a JSON Object
        JsonElement jsonInput = jsonParser.parse(input);
        JsonArray accounts = jsonInput.getAsJsonArray();

        List<Account> linkedAccounts = new ArrayList<>();

        // Parses all the JSON objects in the accounts array, stored in the accounts.json file
        // and then adds them to the list of linked accounts.
        for (JsonElement jsonElement : accounts) {
            Logging.debug("In Accounts parsing loop.");

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            final long discordID = jsonObject.get("discordID").getAsLong();
            final String username = jsonObject.get("username").getAsString();
            final UUID minecraftUUID = UUID.fromString(jsonObject.get("minecraftUUID").getAsString());

            Logging.debug("Getting player value for player with UUID: " + minecraftUUID);
            final OfflinePlayer player = Settings.minecraftServer.getOfflinePlayer(minecraftUUID);

            Logging.debug("Getting Discord User from ID: " + discordID);
            final User user = Settings.discordApi.getUserById(discordID).join();

            if (user == null) {
                Logging.info("discord user not found. ignoring.");
                continue;
            }

            Logging.debug("Adding user " + username + " to the list of accounts.");

            linkedAccounts.add(new Account(user, player).setUsername(username));
        }

        Logging.debug("Done parsing accounts. Setting the list to: " + Arrays.toString(linkedAccounts.toArray()));

        // Sets the global list of linked accounts to the array list 'linkedAccounts' so other processes can access them.
        AccountManager.setLinkedAccounts(linkedAccounts);
    }
}
