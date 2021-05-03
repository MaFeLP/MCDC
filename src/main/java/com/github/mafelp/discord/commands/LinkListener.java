package com.github.mafelp.discord.commands;

import com.github.mafelp.accounts.Account;
import com.github.mafelp.accounts.DiscordLinker;
import com.github.mafelp.accounts.MinecraftLinker;
import com.github.mafelp.utils.Command;
import com.github.mafelp.utils.CommandParser;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Settings;
import com.github.mafelp.utils.exceptions.CommandNotFinishedException;
import com.github.mafelp.utils.exceptions.NoCommandGivenException;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.Optional;

import static com.github.mafelp.utils.Settings.discordCommandPrefix;

/**
 * The class that handles discord messages and test, if they are the link command. If so, it starts the linking process.
 */
public class LinkListener implements MessageCreateListener {
    /**
     * The method that handles messages, that are sent, tests if they are the link command.
     * If so, this method starts the linking process.
     * @param event The event sent from the Discord API, containing important information about the message,
     *              such as the channel it has been sent to and the {@link User} who has sent it.
     */
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        // If the message is sent by the bot, return
        if (event.getMessageAuthor().isYourself()) {
            return;
        }

        // If message does not start with the command prefix, return
        // if (event.getReadableMessageContent().startsWith(discordCommandPrefix) ||
        //        event.getReadableMessageContent() == null)
        //    return;

        // Gets the content of the message as strings (prints it, if debug is enabled)
        String content = event.getReadableMessageContent();

        // Tries parsing the command. The error handling is being done by the CreateChannelListener.
        Command command;
        try {
            command = CommandParser.parseFromString(content);
        } catch (CommandNotFinishedException | NoCommandGivenException e) {
            // Logging.logException(e, "Error parsing the command from the string...");
            return;
        }

        // If the command does not equal link, exit.
        if (!command.getCommand().equalsIgnoreCase(Settings.discordCommandPrefix + "link")) {
            // Logging.debug("Not the link command: \"" + command.getCommand() + "\" != \"" + discordCommandPrefix + "link" + "\"");
            return;
        }

        // Deletes the original message, if specified in the configuration under deleteDiscordCommandMessages
        if (Settings.getConfiguration().getBoolean("deleteDiscordCommandMessages") && event.isServerMessage()) {
            Logging.debug("Deleting original command message with ID: " + event.getMessage().getIdAsString());
            event.getMessage().delete("Specified in MCDC configuration: Was a command message.").join();
            Logging.debug("Deleted the original command message.");
        }

        // Tries to parse the message author into a user. If this fails, don't execute this command.
        if (event.getMessageAuthor().asUser().isEmpty()) {
            Logging.debug("Could not parse user, while trying to execute link command. Aborting...");
            event.getMessage().reply(
                    new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("Could not parse the sender of this message into a discord user. We therefore cannot execute the command \"link\", as requested. See https://mafelp.github.io/MCDC/errors/ for more information.")
                            .setColor(Color.RED)
                            .setAuthor(Settings.discordApi.getYourself())
            );

            return;
        }

        Logging.debug("Executing link command for user " + event.getMessageAuthor().asUser().get().getName());

        // help message for wrong usage
        EmbedBuilder helpMessage = new EmbedBuilder()
                .setAuthor(event.getMessageAuthor())
                .setTitle("Error")
                .addField("Usage", discordCommandPrefix + "link [<link id>]")
                .addField("Functionality", "Links your discord account to a minecraft account..")
                .setColor(new Color(0xFFB500))
                .setFooter("Help message for command \"link\"")
                ;

        // Embed sent on successful account linking
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setAuthor(event.getMessageAuthor())
                .setColor(Color.GREEN)
                .setTitle("Success!")
                .setFooter("More information on linking here: https://mafelp.github.io/MCDC/linking")
                ;

        if (command.getStringArgument(1).isPresent()) {
            Logging.debug("User \"" + event.getMessageAuthor().getDisplayName() + "\" used the command 'link' wrong. Sending help embed...");
            event.getMessage().reply(helpMessage);
            return;
        }

        // Checks if a number is given as the first argument. If not, send the user a new Link token.
        if (command.getIntegerArgument(0).isEmpty()) {
            Logging.debug("No Integer Argument given in link command. Sending LinkTokenEmbed...");
            sendLinkToken(event.getMessageAuthor().asUser().get());
        // If a number is being found at the first argument, try to link it to a minecraft account.
        } else {
            Logging.debug("LinkToken found. Checking it...");
            Optional<Account> linkedAccount = MinecraftLinker.linkToDiscord(event.getMessageAuthor().asUser().get(), command.getIntegerArgument(0).get());

            if (linkedAccount.isEmpty()) {
                Logging.debug("LinkToken is invalid. Sending new Link Token");
                sendLinkToken(event.getMessageAuthor().asUser().get());
                return;
            }

            Logging.debug("LinkToken valid. sending success Embed.");
            event.getMessageAuthor().asUser().get().sendMessage(successEmbed.addInlineField("Minecraft Account", linkedAccount.get().getPlayer().getName())
                        .addInlineField("Discord Account", linkedAccount.get().getUsername() + " :  " + linkedAccount.get().getMentionTag()));
        }
    }

    /**
     * This method sends the user a token, that it can use in {@link com.github.mafelp.minecraft.commands.Link} to
     * links its accounts together.
     * @param user The user to create the linking token from.
     */
    private void sendLinkToken(final User user) {
        int minecraftLinkToken = DiscordLinker.getLinkToken(user);

        EmbedBuilder reply = new EmbedBuilder()
                .setColor(Color.MAGENTA)
                .setTitle("Your Link Token")
                .setAuthor(user)
                .addField("Token","" + minecraftLinkToken)
                .addField("Usage", "To finish linking of your accounts, please log into the minecraft server, and use \"/link " + minecraftLinkToken + "\" to link this discord account to your minecraft account.")
                .setFooter("MCDC made by MaFeLP (https://mafelp.github.io/MCDC/")
                ;

        user.sendMessage(reply);
    }
}