package com.github.mafelp.minecraft.listeners;

import com.github.mafelp.discord.DiscordMessageBroadcast;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class AdvancementListener implements Listener {
    private final HashMap<String, String> advancements = new HashMap<>();
    private final boolean fileDisabled;

    public AdvancementListener() {
        // Check if the config file exists
        final File file = new File(Settings.configurationFileDirectory, "advancements.json");
        if (! file.exists()) {
            fileDisabled = true;
            return;
        }

        String contents = "";
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                contents += reader.nextLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            Logging.logIOException(e, "Could not read from the advancements.json config file!");
            fileDisabled = true;
            return;
        }

        try {
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(contents).getAsJsonObject();
            json.entrySet().forEach(stringJsonElementEntry -> {
                String key = stringJsonElementEntry.getKey();
                key = key.replaceFirst("\\.", "/");
                String value = stringJsonElementEntry.getValue().getAsString();
                advancements.putIfAbsent(key, value);
            });
        } catch (Exception exception) {
            Logging.logException(exception, "Could parse the advancements.json file!");
            fileDisabled = true;
            return;
        }
        fileDisabled = false;
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent playerAdvancementDoneEvent) {
        // Send an Event message that this player has joined
        if (Settings.events.contains("PlayerAdvancementEvent") && ! fileDisabled) {
            String advancementKey = playerAdvancementDoneEvent.getAdvancement().getKey().getKey();
            String description_key =  advancementKey + ".description";
            String title_key = advancementKey + ".title";
            @Nullable
            String title = advancements.get(title_key);
            @Nullable
            String description = advancements.get(description_key);
            if (description == null || title == null) {
                Logging.info(ChatColor.RED + "Could not get advancement for " + ChatColor.GRAY + advancementKey + ChatColor.RED +"!");
                return;
            }
            DiscordMessageBroadcast discordMessageBroadcast = new DiscordMessageBroadcast(
                    "Advancement made: " + title + "!",
                    description,
                    playerAdvancementDoneEvent.getPlayer());
            discordMessageBroadcast.setName("AdvancementEventBroadcaster");
            discordMessageBroadcast.start();
        }
    }
}
