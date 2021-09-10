package com.github.mafelp.discord.commands;

import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Permissions;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * The class used to listen in discord and unlinks your account.
 */
public class HelpListener {
    /**
     * The method that initializes the unlinking.
     * @param event The event that hold information about this specific command.
     */
    public static void onSlashCommand(SlashCommandCreateEvent event) {
        User author = event.getSlashCommandInteraction().getUser();

        List<SelectMenuOption> options = Arrays.asList(
                SelectMenuOption.create("Account", "Account"),
                SelectMenuOption.create("Help", "Help"),
                SelectMenuOption.create("Link", "Link"),
                SelectMenuOption.create("mcmsg", "mcmsg"),
                SelectMenuOption.create("Unlink", "Unlink"),
                SelectMenuOption.create("Whisper", "Whisper")
        );
        if (CheckPermission.checkPermission(Permissions.discordBotAdmin, author.getId()) || CheckPermission.checkPermission(Permissions.discordServerAdmin, author.getId()))
            options.addAll(Arrays.asList(
                    SelectMenuOption.create("Config", "Config"),
                    SelectMenuOption.create("Create Channel", "Create Channel"),
                    SelectMenuOption.create("Create Role", "Create Role"),
                    SelectMenuOption.create("Setup", "Setup")
            ));

        event.getSlashCommandInteraction().createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle("Help Menu")
                        .setAuthor(author)
                        .setFooter("MCDC was created by: MaFeLP", "https://avatars.githubusercontent.com/u/60669873")
                        .setTimestampToNow()
                        .setColor(new Color(0xAAFF00))
                        .setDescription("""
                                Please select an action from the menu below, you want to get help about!

                                If you want to get more detailed information about the individual commands, you can check out our wiki page: https://mafelp.github.io/MCDC/""")
                )
                .addComponents(ActionRow.of(
                SelectMenu.create("helpSelectMenu", options)
        )).respond().thenAccept(interactionOriginalResponseUpdater -> Logging.info("User \"" + author.getName() + "\" executed command \"help\"; Result: SelectMenu and normal help"));
    }
}
