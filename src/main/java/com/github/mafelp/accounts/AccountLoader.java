package com.github.mafelp.accounts;

import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        Scanner scanner;
        try {
            scanner = new Scanner(accountFile);
        } catch (FileNotFoundException e) {
            Logging.logException(e, "Configuration file could not be found. Aborting Account loading.");
            return;
        }

        Logging.debug("Start: Reading Accounts file in.");

        StringBuilder fileInput = new StringBuilder();

        while (scanner.hasNextLine()) {
            fileInput.append(scanner.nextLine());
        }

        String input = fileInput.toString();

        Logging.debug("Trying to parse the accounts file input...");

        JsonElement jsonInput = jsonParser.parse(input);
        JsonArray accounts = jsonInput.getAsJsonArray();

        List<Account> linkedAccounts = new ArrayList<>();

        for (JsonElement jsonElement : accounts) {
            Logging.debug("In Accounts parsing loop.");

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            final long discordID = jsonObject.get("discordID").getAsLong();
            final String username = jsonObject.get("username").getAsString();
            final String minecraftUUID = jsonObject.get("minecraftUUID").getAsString();

            final Player player = Settings.minecraftServer.getPlayer(minecraftUUID);
            final User user = Settings.discordApi.getUserById(discordID).join();

            if (player == null || user == null)
                continue;

            linkedAccounts.add(new Account(user, player).setUsername(username));
        }

        Logging.debug("Done parsing accounts. Setting the list.");

        AccountManager.setLinkedAccounts(linkedAccounts);
    }
}
