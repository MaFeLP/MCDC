package com.github.mafelp.discord.commands;

import com.github.mafelp.utils.Logging;
import org.bukkit.ChatColor;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class MainSlashCommandListener implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent slashCommandCreateEvent) {
        switch (slashCommandCreateEvent.getSlashCommandInteraction().getCommandName()) {
            case "setup" -> {}
            case "link" -> LinkListener.onSlashCommand(slashCommandCreateEvent);
            case "unlink" -> UnlinkListener.onSlashCommand(slashCommandCreateEvent);
            case "whisper", "mcmsg" -> WhisperListener.onSlashCommand(slashCommandCreateEvent);
            default -> Logging.info(ChatColor.RED + "Wait. Wait? This command is not recognised: " + slashCommandCreateEvent.getSlashCommandInteraction().getCommandName() + " and this should not have happened!");
        }
    }
}
