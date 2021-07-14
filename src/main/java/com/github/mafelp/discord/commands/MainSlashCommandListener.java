package com.github.mafelp.discord.commands;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class MainSlashCommandListener implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent slashCommandCreateEvent) {
        switch (slashCommandCreateEvent.getSlashCommandInteraction().getCommandName()) {
            case "setup" -> {}
            case "link" -> LinkListener.onSlashCommand(slashCommandCreateEvent);
            default -> {}
        }
    }
}
