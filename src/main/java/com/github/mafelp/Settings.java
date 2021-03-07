package com.github.mafelp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

public class Settings {
    public static final String API_TOKEN = "Your super secret Discord Bot Token: https://discord.com/developers/applications/";

    public static volatile DiscordApi discordApi;
    public static volatile Server minecraftServer;
    public static volatile String prefix;
    public static boolean shortMsg = true;
    public static String msgPrefix(MessageCreateEvent event) {
        if (shortMsg) {
            String first = ChatColor.DARK_GRAY + "[" +
                    ChatColor.LIGHT_PURPLE + "DC" +
                    ChatColor.DARK_GRAY + "/" +
                    ChatColor.GOLD;
            String last = ChatColor.DARK_GRAY + "]" +
                    ChatColor.BLACK + ": " +
                    ChatColor.RESET;

            return first +
                    event.getMessageAuthor().getDisplayName() +
                    last;
        } else {
            String first = ChatColor.GRAY + "╔" +
                    ChatColor.DARK_GRAY + "[" +
                    ChatColor.LIGHT_PURPLE + "DC" +
                    ChatColor.DARK_GRAY + "/" +
                    ChatColor.GOLD;
            String second = ChatColor.DARK_GRAY + " in " +
                    ChatColor.DARK_AQUA;
            String third = ChatColor.DARK_GRAY + " on " +
                    ChatColor.DARK_AQUA;
            String last = ChatColor.DARK_GRAY + "]" +
                    ChatColor.BLACK + ": " +
                    ChatColor.GRAY + "\n╚▶" +
                    ChatColor.RESET;

            return first +
                    event.getMessageAuthor().getDisplayName() +
                    second +
                    event.getServerTextChannel().get().getName() +
                    third +
                    event.getServer().get().getName() +
                    last;
        }
    }
}
