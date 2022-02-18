package com.github.mafelp.discord.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.accounts.AccountManager;
import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Permissions;
import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * The class that listens to the discord chats, if the channel creation command is executed. -
 * As discord announced just today, there will be an update to the bot API, that'll be adding
 * slash command support. This class will be moved, if the update is available in this API.
 */
public class AccountListener {
    /**
     * The method called by the discord API, for every chat message. -
     * This method will filter them and execute commands accordingly.
     * @param event The event containing information about the message.
     */
    public static void onSlashCommand(SlashCommandInteraction event) {
        User author = event.getUser();
        // help message for wrong usage
        EmbedBuilder helpMessage = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Error")
                .addField("Usage", "/account <COMMAND> (<OPTION>)")
                .addInlineField("get", "Gets you the Minecraft Name of a Discord User (Option: Discord User)")
                .addInlineField("link", "Same as the command /link.\nSee \"/help\" -> Link for more information.")
                .addInlineField("name / username", "Changes your account name to OPTION")
                .addInlineField("unlink", "Same as the command /unlink.\nSee \"/help\" -> Unlink for more information.")
                .setColor(new Color(0xFFB500))
                .setFooter("Help message for command \"account\"");

        if (CheckPermission.checkPermission(Permissions.discordBotAdmin, author.getId()) || CheckPermission.checkPermission(Permissions.discordServerAdmin, author.getId())) {
            helpMessage.addInlineField("reload", "Reloads the accounts from the config file.")
                    .addInlineField("remove", "Removes an account.")
                    .addInlineField("save", "Saves the currently linked accounts to the save file.")
            ;
        }

        List<SlashCommandInteractionOption> options = event.getOptions();

        if (options.size() > 2 || options.size() == 0) {
            Logging.info("User \"" + author.getName() + "\" tried to execute command \"account\"!");
            event.createImmediateResponder().addEmbed(helpMessage).respond();
            return;
        }

        EmbedBuilder errorEmbed = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Error")
                .setColor(Color.RED)
                .setFooter("Use \"/help\" for help!");

        EmbedBuilder noAccountErrorEmbed = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("No Account Error")
                .setColor(Color.RED)
                .setDescription("Please use \"/link\" to link your accounts and get access to this feature!")
                .setFooter("Use \"/help\" for help!");

        switch (options.get(0).getName().toLowerCase(Locale.ROOT)) {
            // Links your discord and minecraft accounts
            case "link" -> LinkListener.checkAndSendToken(event, options.get(0).getOptionLongValueByIndex(0), author);
            // Sets your username
            case "name", "username" -> {
                Logging.debug("Subcommand: " + options.get(0).getName());
                if (Account.getByDiscordUser(author).isEmpty()) {
                    event.createImmediateResponder().addEmbed(noAccountErrorEmbed).respond().join();
                    return;
                }

                if (options.get(0).getOptionStringValueByIndex(0).isEmpty()) {
                    Logging.info(ChatColor.GRAY + author.getName() + ChatColor.RESET + " requested his account name.");
                    event.createImmediateResponder().addEmbed(new EmbedBuilder()
                            .setAuthor(author)
                            .setTitle("Account name")
                            .setColor(Color.GREEN)
                            .setDescription("Your account name is: " + Account.getByDiscordUser(author).get().getUsername())
                            .setFooter("If you wanted to change your account name, append it to the command!")
                    ).respond().join();
                    return;
                }

                Logging.debug("DC User " + author.getName() + " requested a name change.");
                // Validate the inputted string
                String inputName = options.get(0).getOptionStringValueByIndex(0).get();

                Logging.debug("Checking for wrong characters in requested name.");
                char[] chars = inputName.toCharArray();
                char[] forbiddenCharacters = {'\\', ' ', '"', '\'', '<', '>', '#', '!', '$', '%', '^', '&', '*', '(', ')', '+', '=', '[', ']', '{', '}', ';', ':', '?', '/'};

                int i = 0;
                for (char c : chars) {
                    if (c == '@' && i != 0) {
                        event.createImmediateResponder().addEmbed(
                                errorEmbed.setDescription("Sorry, you are not allowed to have \"@\" in your name!")
                        ).respond().join();
                        return;
                    }

                    for (char forbidden : forbiddenCharacters) {
                        if (forbidden == c) {
                            event.createImmediateResponder().addEmbed(
                                    errorEmbed.setDescription("Sorry, you are not allowed to have these characters in your name: \\ \"'<>#!@$%^&*()_/+=[]{}|")
                            ).respond().join();
                            return;
                        }
                    }

                    i++;
                }
                Logging.debug("The requested name for " + author.getName() + " has passed the character check!");

                // If the first character is an @, do not add one to the start of the name.
                String nameToSet = chars[0] == '@' ? inputName : '@' + inputName;

                // Checks if the input name meets the specific requirements.
                Logging.debug("Checking for name length...");
                if (nameToSet.length() >= 33) {
                    Logging.debug("Name did not pass the length check!");
                    event.createImmediateResponder().addEmbed(errorEmbed.setDescription(
                            "Your name is too long! The limit is 32 characters!")
                    ).respond().join();
                    return;
                }

                // Checks if the name is taken by anybody else
                Logging.debug("Checking if the name is a duplicate of another name.");
                for (Account a : AccountManager.getLinkedAccounts()) {
                    if (a.getUsername().equalsIgnoreCase(nameToSet)) {
                        Logging.debug("Name failed the duplicate name check!");
                        event.createImmediateResponder().addEmbed(errorEmbed.setDescription(
                                "Could not set name: Name is already taken!"
                        )).respond().join();
                        return;
                    }
                }
                Logging.debug("Name has passed the duplicate name check!");

                // Checks if the name is a minecraft name, of a player, who already was online.
                Logging.debug("Checking if the name is a minecraft user.");
                for (OfflinePlayer offlinePlayer : Settings.minecraftServer.getOfflinePlayers()) {
                    if (nameToSet.equalsIgnoreCase("@" + offlinePlayer.getName())) {
                        Logging.debug("Name failed the minecraft name check!");
                        event.createImmediateResponder().addEmbed(errorEmbed.setDescription(
                                "Could not set name: Name is a minecraft name!"
                        )).respond().join();
                        return;
                    }
                }
                Logging.debug("Name passed the minecraft username check!");

                // Gets the account and sets its username.
                Account account = Account.getByDiscordUser(author).get();
                Logging.info("DC User " + author.getName() + " changed its username from " + account.getUsername() + " to " + nameToSet + ".");
                account.setUsername(nameToSet);
                event.createImmediateResponder().addEmbed(new EmbedBuilder()
                        .setTitle("Username Change completed!")
                        .setColor(Color.GREEN)
                        .setAuthor(author)
                        .setDescription("Your username has been set to " + Account.getByDiscordUser(author).get().getUsername())
                ).respond().join();
            }
            // The subcommand used to get your/another players account name.
            case "get" -> {
                Logging.debug("DC User " + author.getName() + " executed subcommand \"get\".");
                // If no additional arguments were passed, give the player his/her account name.
                if (options.get(0).getOptionUserValueByIndex(0).isEmpty()) {
                    if (Account.getByDiscordUser(author).isEmpty()) {
                        Logging.debug("DC User " + ChatColor.GRAY + author.getName() + ChatColor.RESET + " has requested his account name: They don't have one. Sending help message.");
                        event.createImmediateResponder().addEmbed(new EmbedBuilder()
                                .setDescription(
                                        "Sorry, you do not have an account. \n\nUse \"/link\" to get one or specify a name to get account details of."
                                )
                        ).respond().join();
                        return;
                    }
                    Logging.debug("DC User " + ChatColor.GRAY + author.getName() + ChatColor.RESET + " has requested his account name.");
                    Account account = Account.getByDiscordUser(author).get();
                    event.createImmediateResponder().addEmbed(new EmbedBuilder()
                            .setAuthor(author)
                            .setTitle("Account info")
                            .setColor(new Color(0x9512FA))
                            .addField("Account name", account.getUsername())
                            .addField("Discord Name", account.getUser().getMentionTag())
                            .addField("Minecraft Name", account.getPlayer().getName())
                    ).respond().join();

                    return;
                }

                // If additional Arguments are passed, get the name of the accounts.
                Logging.debug("Offline Players:");
                // Checks all players that were online at least once and if they have an account.
                for (OfflinePlayer p : Settings.minecraftServer.getOfflinePlayers()) {
                    Logging.debug("`--> " + p.getName());
                    // Check if the name of the player equals the requested name.
                    if (Objects.equals(p.getName(), options.get(0).getOptionStringValueByIndex(0).get())) {
                        // Get the account for the requested Player.
                        Optional<Account> requestedAccount = Account.getByPlayer(p);

                        // If the player does not have an account, send an error message.
                        if (requestedAccount.isPresent()) {
                            Logging.debug("Player " + author.getName() + " got the account name for player " + options.get(0).getOptionStringValueByIndex(0).get() + ". It is: " + requestedAccount.get().getUsername());
                            event.createImmediateResponder().addEmbed(new EmbedBuilder()
                                    .setAuthor(author)
                                    .setTitle("Account info")
                                    .setColor(new Color(0x9512FA))
                                    .addField("Account name", requestedAccount.get().getUsername())
                                    .addField("Discord Name", requestedAccount.get().getUser().getMentionTag())
                                    .addField("Minecraft Name", requestedAccount.get().getPlayer().getName())
                            ).respond().join();
                        }
                    }
                }

                // If no player could be found, send the player an error message and exit.
                event.createImmediateResponder().addEmbed(errorEmbed.setDescription(
                    "Player with the name " + options.get(0).getOptionStringValueByIndex(0).get() + " does not exist!"
                )).respond().join();
            }
            // Removes an account, if the player has the required permissions.
            case "remove" -> {
                // If the user does not have te required permissions, exit.
                if (incidentReport(author, event)) return;

                // Checks if enough arguments were passed.
                if (options.get(0).getOptionUserValueByIndex(0).isEmpty()) {
                    event.createImmediateResponder().addEmbed(errorEmbed.setDescription("""
                                    Not enough arguments given!

                                    Please provide the discord user to get information of!"""
                    )).respond().join();
                    Logging.debug("DC User " + ChatColor.DARK_GRAY + author.getName() + ChatColor.RESET + " tried to execute the command " + ChatColor.DARK_GRAY + "account remove " + Arrays.toString(options.toArray()) + ChatColor.RESET + "! The command did not have enough/too much arguments!");
                    return;
                }

                if (Account.getByDiscordUser(options.get(0).getOptionUserValueByIndex(0).get()).isEmpty()) {
                    event.createImmediateResponder().addEmbed(errorEmbed.setDescription(
                            "User " + options.get(0).getOptionUserValueByIndex(0).get().getMentionTag() + " does not have account! Ignoring..."
                    )).respond().join();

                    return;
                }

                Account toRemove = Account.getByDiscordUser(options.get(0).getOptionUserValueByIndex(0).get()).get();
                String username = toRemove.getUsername();
                AccountManager.removeAccount(toRemove);
                event.createImmediateResponder().addEmbed(new EmbedBuilder()
                        .setTitle("Success!")
                        .setAuthor(author)
                        .setColor(Color.GREEN)
                        .setDescription("Successfully removed account with username: " + username
                )).respond().join();
                Logging.info("Discord User " + author.getName() + "remove the account with the name " + ChatColor.GRAY + username + ChatColor.RESET + ".");
            }
            // The subcommand used to save the accounts file.
            case "save" -> {
                // If the user does not have te required permissions, exit.
                if (incidentReport(author, event)) return;

                InteractionOriginalResponseUpdater message = event.createImmediateResponder().addEmbed(new EmbedBuilder()
                        .setTitle("Scheduled Save of the Accounts...")
                        .setAuthor(author)
                        .setColor(new Color(0xFFD500))
                        .setDescription("This message will update, once the accounts have been saved.")
                ).respond().join();
                Logging.info("Discord User " + ChatColor.DARK_GRAY + author.getName() + ChatColor.RESET + " is saving the configuration file.");
                try {
                    AccountManager.saveAccounts();
                    Logging.info(ChatColor.GREEN + "Successfully saved the accounts file!");
                    message.removeAllEmbeds().addEmbed(new EmbedBuilder()
                            .setTitle("Success!")
                            .setColor(Color.GREEN)
                            .setAuthor(author)
                            .setDescription("Successfully saved the accounts file!"
                    )).update().join();
                } catch (FileNotFoundException e) {
                    Logging.logIOException(e, "Error saving the account file! (Discord Author of this save: " + author.getName());
                    message.removeAllEmbeds().addEmbed(errorEmbed.setDescription(
                        "A FileNotFoundException occurred! Please see the server console for more details!"
                    )).update().join();
                }
            }
            // The subcommand used to reload the accounts file into memory.
            case "reload" -> {
                // If the user does not have te required permissions, exit.
                if (incidentReport(author, event)) return;

                InteractionOriginalResponseUpdater message = event.createImmediateResponder().addEmbed(new EmbedBuilder()
                        .setTitle("Scheduled Reload of the Accounts...")
                        .setAuthor(author)
                        .setColor(new Color(0xFFD500))
                        .setDescription("Reloading the accounts file... Warning! Only edit the file, if you know what you are doing!")
                ).respond().join();
                Logging.info("Discord User " + ChatColor.DARK_GRAY + author.getName() + ChatColor.RESET + " is reloading the configuration file.");
                try {
                    AccountManager.loadAccounts();
                    Logging.info(ChatColor.GREEN + "Successfully saved the accounts file!");
                    message.removeAllEmbeds().addEmbed(new EmbedBuilder()
                            .setTitle("Success!")
                            .setColor(Color.GREEN)
                            .setAuthor(author)
                            .setDescription("Successfully reloaded the accounts file!"
                            )).update().join();
                } catch (IOException e) {
                    Logging.logIOException(e, "Error reloading the accounts file (Author of this command: " + author.getName());
                    message.removeAllEmbeds().addEmbed(errorEmbed.setDescription(
                            "A FileNotFoundException occurred! Please see the server console for more details!"
                    )).update().join();
                }
            }
            // The subcommand that lists all currently linked accounts
            case "list" -> {
                if (! Settings.getConfiguration().getBoolean("allowListAllAccounts")) {
                    event.createImmediateResponder().addEmbed(errorEmbed.setDescription(
                        "Sorry, the Server administrator has disabled the listing of accounts!"
                    )).respond().join();
                    return;
                }

                EmbedBuilder response = new EmbedBuilder()
                        .setAuthor(author)
                        .setTitle("Account list")
                        .setColor(new Color(0x00))
                        .setDescription("A List of all account, following the schema")
                        .addField("Username", "Minecraft/Discord");

                for (Account a : AccountManager.getLinkedAccounts())
                    response.addInlineField(a.getUsername(), a.getPlayer().getName() + "/" + a.getMentionTag());

                Logging.info("Discord User " + author.getName() + " requested an account list.");
                event.createImmediateResponder().addEmbed(response).respond().join();
            }
            // The subcommand that unlinks your account.
            case "unlink" -> UnlinkListener.onSlashCommand(event);
            // If no subcommand was specified.
            default -> Logging.info(ChatColor.RED + "Error getting slash command option. Expected a valid option, got \"" + options.get(0).getName() + "\" instead!");
        }
    }

    /**
     * The method used to check the {@link com.github.mafelp.utils.Permissions}: {@code accountEdit} of a command Sender.
     * @param author The command sender to check the permission of.
     * @param event The command that will be shown into the console, if the permission is denied.
     * @return If the permission was granted.
     */
    private static boolean incidentReport(@NotNull User author, SlashCommandInteraction event) {
        EmbedBuilder errorEmbed = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Error")
                .setColor(Color.RED)
                .setFooter("Use \"/help\" for help!");

        if (!CheckPermission.checkPermission(Permissions.accountEdit, author.getId())) {
            event.createImmediateResponder().addEmbed(errorEmbed.setDescription("""
                            Sorry, you don't have the required permissions, to execute this command!

                            This incident will be reported!"""
            )).respond().join();
            Logging.info("DC User " + ChatColor.DARK_GRAY + author.getName() + ChatColor.RESET + " tried to execute the command " + ChatColor.DARK_GRAY + "account remove " + Arrays.toString(event.getOptions().toArray()) + ChatColor.RESET + "! This action was denied due to missing permission!");
            return true;
        }
        return false;
    }
}
