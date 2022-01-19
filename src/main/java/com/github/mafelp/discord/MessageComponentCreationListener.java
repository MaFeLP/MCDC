package com.github.mafelp.discord;

import com.github.mafelp.utils.Logging;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.awt.*;

public class MessageComponentCreationListener implements MessageComponentCreateListener {
    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction interaction = event.getMessageComponentInteraction();

        switch (interaction.getCustomId()) {
            case "helpSelectMenu" -> helpSelectMenu(interaction);
            default -> {}
        }
    }

    private static void helpSelectMenu(MessageComponentInteraction interaction) {
        if (interaction.asSelectMenuInteraction().isEmpty())
            return;
        interaction.getMessage().ifPresent(message -> {
            if (message.canYouDelete())
                message.delete("Message no longer needed, as the help command continues.").thenAccept(none ->
                        Logging.debug("Removed original help message."));
        });

        InteractionImmediateResponseBuilder message = interaction.createImmediateResponder()
                .append("Your requested help page(s):")
                .addComponents(ActionRow.of(Button.link("https://mafelp.github.io/MCDC/", "View on the web")));
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(interaction.getUser())
                .setColor(new Color(0xDE9A4C))
                .setTimestampToNow()
                ;
        interaction.asSelectMenuInteraction().get().getChosenOptions().forEach(selectMenuOption -> {
            switch (selectMenuOption.getLabel()) {
                case "Account" -> {
                    message.addEmbed(embed
                            .setTitle("Account Help")
                            .setDescription("Here is the information about the custom accounts\n\nFor more account controls, please log in to the Minecraft Server and use tab completion on the `/account` command")
                            .addField("/link", "Links your discord account to your Minecraft Account on this server.")
                            .addField("/unlink", "Removes the discord - Minecraft account link")
                    );
                    Logging.debug("Added Account embed to the help message.");
                }
                case "Config" -> {
                    message.addEmbed(embed
                            .setTitle("Config Help")
                            .setDescription("""
                                                Administrators can change the configuration of this bot.
                                                
                                                This feature is currently only available as a server command!
                                                As an administrator, you can also use the minecraft server console.
                                                
                                                To see available options, please use the `Tab` Key when typing `/config `.
                                                """)
                    );
                    Logging.debug("Added Config embed to the help message.");
                }
                case "Create Channel" -> {
                    message.addEmbed(embed
                            .setTitle("Create Channel Help")
                            .setDescription("""
                                    Takes one argument:
                                      - The channel name
                                      
                                    What does this command do? It creates a channel with the specified name and adds it to its configuration to send all the minecraft messages to.
                                    
                                    It is advised to not use this command, but use the `/setup` command instead!
                                    
                                    This command can only be used by Server/Bot Admins!""")
                    );
                    Logging.debug("Added createChannel embed to the help message.");
                }
                case "Create Role" -> {
                    message.addEmbed(embed
                            .setTitle("Create Role Help")
                            .setDescription("""
                                    Takes one argument:
                                      - The role name
                                      
                                    What does this command do? It creates a new role with the specified name, so you can grant this role access to the minecraft related channels.
                                    
                                    It is advised to not use this command, but use the `/setup` command instead!
                                    
                                    This command can only be used by Server/Bot Admins!""")
                    );
                    Logging.debug("Added createRole embed to the help message.");
                }
                case "Help" -> {
                    message.addEmbed(embed
                            .setTitle("Help")
                            .setDescription("""
                                    Gets you a menu, with which you can get further information on all the commands.
                                    
                                    If you still need further help, please write the developers an email (mafelp@protonmail.ch)!""")
                    );
                    Logging.debug("Added Help embed to the help message.");
                }
                case "Link" -> {
                    message.addEmbed(embed
                            .setTitle("Link Help")
                            .setDescription("""
                                    This command either takes one optional argument: A link token.
                                    You can obtain a link token, by running `/link` in minecraft.
                                    If you leave the token field blank, this command will generate you a link token, which you can then use as an argument in minecraft to link your accounts.
                                    
                                    Linking of your account enables you for whisper messages between discord users and minecraft users (and the other way round as well), as well as getting clickable mentions if mentioned with `@` in the minecraft chat.""")
                    );
                    Logging.debug("Added Link embed to the help message.");
                }
                case "mcmsg" -> {
                    message.addEmbed(embed
                            .setTitle("mcmsg Help")
                            .setDescription("""
                                    Send a private message to a person on discord, that just they can see!
                                    
                                    This command takes two arguments:
                                      - The first argument is the user(name) that you want to send the message to.
                                      - The second argument is the message you want to send them.""")
                    );
                    Logging.debug("Added mcmsg embed to the help message.");
                }
                case "Setup" -> {
                    message.addEmbed(embed
                            .setTitle("Setup Help")
                            .setDescription("""
                                    Setup a new channel for incoming messages and a new role to manage permissions with.
                                    
                                    This command takes one argument: The name of the new channel and role (it will be the same name)""")
                    );
                    Logging.debug("Added Setup embed to the help message.");
                }
                case "Unlink" -> {
                    message.addEmbed(embed
                            .setTitle("Unlink Help")
                            .setDescription("Unlinks your discord and minecraft accounts. If you want to link your accounts, run `/link` either in discord or in minecraft.")
                    );
                    Logging.debug("Added Unlink embed to the help message.");
                }
                case "Whisper" -> {
                    message.addEmbed(embed
                            .setTitle("Whisper Help")
                            .setDescription("""
                                    Send a private message to a person on discord, that just they can see!
                                    
                                    This command takes two arguments:
                                      - The first argument is the user(name) that you want to send the message to.
                                      - The second argument is the message you want to send them.""")
                    );
                    Logging.debug("Added Whisper embed to the help message.");
                }
                default -> {
                    message.addEmbed(new EmbedBuilder()
                            .setColor(new Color(0xFF0048))
                            .setTitle("Error!")
                            .setAuthor(interaction.getApi().getYourself())
                            .setDescription("""
                                    An internal server error occurred!
                                    
                                    Please create an issue here: https://github.com/MaFeLP/MCDC/issues/new
                                    and reference the following error message:
                                    
                                    MessageComponentCreationListener.java: Unrecognised selectMenuLabel \"""" + selectMenuOption.getLabel() + "\"")
                    );
                    Logging.debug("Added Account embed to the help message.");
                }
            }
        });
        message.respond();
    }
}
