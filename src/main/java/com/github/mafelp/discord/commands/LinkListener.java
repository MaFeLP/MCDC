package com.github.mafelp.discord.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.accounts.DiscordLinker;
import com.github.mafelp.accounts.MinecraftLinker;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;
import java.util.Optional;

/**
 * The class that handles discord messages and test, if they are the link command. If so, it starts the linking process.
 */
public class LinkListener {
    protected static void checkAndSendToken(final SlashCommandInteraction event, final Optional<Long> token, final User author) {
        // Embed sent on successful account linking
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setAuthor(author)
                .setColor(Color.GREEN)
                .setTitle("Success!")
                .setFooter("More information on linking here: https://mafelp.github.io/MCDC/linking")
                ;

        EmbedBuilder alreadyRegistered = new EmbedBuilder()
                .setAuthor(author)
                .setColor(new Color(0xFF3C00))
                .setTitle("Account Already Registered!")
                .setFooter("More information on linking here: https://mafelp.github.io/MCDC/linking")
                ;

        if (Account.getByDiscordUser(author).isPresent()) {
            Account account = Account.getByDiscordUser(author).get();

            alreadyRegistered.setDescription("Sorry, but your Discord Account is already linked to the minecraft user " + account.getPlayer().getName() + "!\n" +
                    "Use \"/unlink\" in Minecraft or \"" + Settings.getConfiguration().get("discordCommandPrefix") + "unlink\" to unlink your accounts!");

            event.createImmediateResponder().addEmbed(alreadyRegistered).respond();

            return;
        }

        // Checks if a number is given as the first argument. If not, send the user a new Link token.
        if (token.isEmpty()) {
            Logging.debug("No Integer Argument given in link command. Sending LinkTokenEmbed...");
            sendLinkToken(author, event);
        // If a number is being found at the first argument, try to link it to a minecraft account.
        } else {
            Logging.debug("LinkToken found. Checking it...");
            Optional<Account> linkedAccount = MinecraftLinker.linkToDiscord(author, token.get().intValue());

            if (linkedAccount.isEmpty()) {
                Logging.debug("LinkToken is invalid. Sending new Link Token");
                sendLinkToken(author, event);
                return;
            }

            Logging.debug("LinkToken valid. sending success Embed.");
            event.createImmediateResponder().addEmbed(
                    successEmbed.addInlineField("Minecraft Account", linkedAccount.get().getPlayer().getName())
                            .addInlineField("Discord Account", linkedAccount.get().getUsername() + " :  " + linkedAccount.get().getMentionTag())
            ).respond();
        }
    }

    /**
     * The method that handles messages, that are sent, tests if they are the link command.
     * If so, this method starts the linking process.
     * @param event The event sent from the Discord API, containing important information about the message,
     *              such as the channel it has been sent to and the {@link User} who has sent it.
     */
    public static void onSlashCommand(SlashCommandCreateEvent event) {
        User author = event.getSlashCommandInteraction().getUser();
        Logging.debug("Executing link command for user " + author);

    }

    /**
     * This method sends the user a token, that it can use in {@link com.github.mafelp.minecraft.commands.Link} to
     * links its accounts together.
     * @param user The user to create the linking token from.
     * @param event The event to respond to.
     */
    protected static void sendLinkToken(final User user, final SlashCommandInteraction event) {
        int minecraftLinkToken = DiscordLinker.getLinkToken(user);

        EmbedBuilder reply = new EmbedBuilder()
                .setColor(Color.MAGENTA)
                .setTitle("Your Link Token")
                .setAuthor(user)
                .addField("Token","" + minecraftLinkToken)
                .addField("Usage", "To finish linking of your accounts, please log into the minecraft server, and use \"/link " + minecraftLinkToken + "\" to link this discord account to your minecraft account.")
                .setFooter("MCDC made by MaFeLP (https://mafelp.github.io/MCDC/")
                ;

        event.createImmediateResponder().addEmbed(reply).respond();
    }
}