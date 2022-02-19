package com.github.mafelp.discord.commands;

import com.github.mafelp.utils.Logging;
import org.bukkit.ChatColor;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

/**
 * The class to handle all slash command reactions.
 */
public class MainSlashCommandListener implements SlashCommandCreateListener {
    /**
     * The method to handle the actual handling of the slash command events, by handing them over to their own files.
     * @param slashCommandCreateEvent The event passed over by the discord api that contains the slash command
     *                                interaction.
     */
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent slashCommandCreateEvent) {
        Logging.debug("Slash command interaction caught: " + slashCommandCreateEvent.getSlashCommandInteraction().getCommandName());
        switch (slashCommandCreateEvent.getSlashCommandInteraction().getCommandName()) {
            case "setup" -> SetupListener.onSlashCommand(slashCommandCreateEvent);
            case "link" -> LinkListener.onSlashCommand(slashCommandCreateEvent);
            case "unlink" -> UnlinkListener.onSlashCommand(slashCommandCreateEvent.getSlashCommandInteraction());
            case "whisper", "mcmsg" -> WhisperListener.onSlashCommand(slashCommandCreateEvent);
            case "create" -> {
                if (slashCommandCreateEvent.getSlashCommandInteraction().getOptionByIndex(0).isPresent()) {
                    switch (slashCommandCreateEvent.getSlashCommandInteraction().getOptionByIndex(0).get().getName()) {
                        case "channel" -> CreateChannelListener.onSlashCommand(slashCommandCreateEvent);
                        case "role" -> CreateRoleListener.onSlashCommand(slashCommandCreateEvent);
                        default -> Logging.info(ChatColor.RED + "Error in SlashCommand \"create\": First option is: " + slashCommandCreateEvent.getSlashCommandInteraction().getOptionByIndex(0).get().getName());
                    }
                } else {
                    Logging.info(ChatColor.RED + "Error in SlashCommand \"create\": no First option given!");
                }
            }
            case "account" -> AccountListener.onSlashCommand(slashCommandCreateEvent.getSlashCommandInteraction());
            case "config" -> ConfigListener.onSlashCommand(slashCommandCreateEvent.getSlashCommandInteraction());
            case "help" -> HelpListener.onSlashCommand(slashCommandCreateEvent);
            default -> Logging.info(ChatColor.RED + "Wait. Wait? This command is not recognised: " + slashCommandCreateEvent.getSlashCommandInteraction().getCommandName() + " and this should not have happened!");
        }
    }
}
