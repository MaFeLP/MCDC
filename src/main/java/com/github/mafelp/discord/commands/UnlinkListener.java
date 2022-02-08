package com.github.mafelp.discord.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.accounts.AccountManager;
import com.github.mafelp.utils.Logging;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;
import java.util.Optional;

import static com.github.mafelp.utils.Settings.discordCommandPrefix;

/**
 * The class used to listen in discord and unlinks your account.
 */
public class UnlinkListener{
    /**
     * The method that initializes the unlinking.
     * @param event The event that hold information about this specific command.
     */
    public static void onSlashCommand(SlashCommandInteraction event) {
        User author = event.getUser();
        // the embed sent on successful execution of the command.
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Success!")
                .setColor(Color.GREEN)
                ;

        // Embed to send, when the bot does not have the required Permissions.
        EmbedBuilder noAccountEmbed = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Error!")
                .addField("NoAccountError","Sorry, you don't have an account to unlink! Use \"" + discordCommandPrefix + "link\" to create one!")
                .setColor(Color.RED)
                ;

        Logging.debug("User \"" + author.getName() + "\" executed command \"unlink\". Parsing User...");

        Optional<Account> optionalAccount = Account.getByDiscordUser(author);

        // If the user does not have an account.
        if (optionalAccount.isEmpty()) {
            Logging.debug("User \"" + author.getName() + "\" does not have an account to unlink... Sending noAccountEmbed...");
            event.createImmediateResponder().addEmbed(noAccountEmbed).respond();
            return;
        }

        Logging.debug("Getting the account for user \"" + author.getName() + "\"...");
        // Get the account and some information about it.
        Account account = optionalAccount.get();

        String minecraftName = account.getPlayer().getName();
        String mentionTag = account.getMentionTag();
        String username = account.getUsername();

        Logging.info("Removing account \"" + username + "\"...");
        // Then remove the account.
        AccountManager.removeAccount(account);

        successEmbed.setDescription("Successfully unlinked your minecraft account \"" + minecraftName + "\" from user discord account " + mentionTag);

        Logging.info("Removed account \"" + username + "\"... Sending success embed...");
        event.createImmediateResponder().addEmbed(successEmbed).respond();
    }
}
