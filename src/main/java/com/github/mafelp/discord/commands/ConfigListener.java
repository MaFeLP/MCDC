package com.github.mafelp.discord.commands;

import com.github.mafelp.discord.DiscordMain;
import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Permissions;
import com.github.mafelp.utils.Settings;
import org.bukkit.ChatColor;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.github.mafelp.utils.Settings.*;

/**
 * The class that listens to the discord chats, if the channel creation command is executed. -
 * As discord announced just today, there will be an update to the bot API, that'll be adding
 * slash command support. This class will be moved, if the update is available in this API.
 */
public class ConfigListener {
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
                .addField("Usage", "/config <COMMAND> (<PATH>) (<VALUE>)")
                .addInlineField("reload", "Reloads the configuration from the file, overriding any changes.")
                .addInlineField("save", "Saves the configuration to the config file.")
                .addInlineField("default", "Resets the configuration to its default state")
                .addInlineField("set", "Sets a config value to the specified \"PATH\"")
                .addInlineField("get", "Gets the configuration value for the specific \"PATH\"")
                .addInlineField("add", "Adds \"VALUE\" to list, which is located at \"PATH\"")
                .addInlineField("remove", "Removed \"VALUE\" from the list, which is located at \"PATH\"")
                .setColor(new Color(0xFFB500))
                .setFooter("Help message for command \"config\"");

        List<SlashCommandInteractionOption> options = event.getOptions();

        if (options.size() > 2 || options.size() == 0) {
            Logging.info("User \"" + author.getName() + "\" tried to execute command \"config\"!");
            event.createImmediateResponder().addEmbed(helpMessage).respond();
            return;
        }

        EmbedBuilder errorEmbed = new EmbedBuilder()
                .setAuthor(author)
                .setTitle("Error")
                .setColor(Color.RED)
                .setFooter("Use \"/help\" for help or go to https://mafelp.github.io/MCDC/configuration");

        switch (options.get(0).getName().toLowerCase(Locale.ROOT)) {
            // subcommand reload:
            // Reloads the configuration.
            case "reload" -> {
                // If the user does not have te required permissions, exit.
                if (incidentReport(author, event)) return;

                InteractionOriginalResponseUpdater message = event.createImmediateResponder().addEmbed(new EmbedBuilder()
                        .setTitle("Scheduled Reload of the Config File...")
                        .setAuthor(author)
                        .setColor(new Color(0xFFD500))
                        .setDescription("""
                                        Reloading the config file...
                                        
                                        Warning! Only edit the file, if you know what you are doing!
                                        
                                        A Guide for the config file can be found at https://mafelp.github.io/mcdc/configuration""")
                ).respond().join();
                Logging.info("Discord User " + ChatColor.DARK_GRAY + author.getName() + ChatColor.RESET + " is reloading the configuration file.");
                // Shutdown sequence
                DiscordMain.shutdown();
                // Reload sequence
                init();
                Thread discordInitThread = new DiscordMain();
                discordInitThread.setName("Initializing the Discord instance.");
                discordInitThread.start();
                Logging.info(ChatColor.GREEN + "Successfully reloaded the config file!");
                message.removeAllEmbeds().addEmbed(new EmbedBuilder()
                        .setTitle("Success!")
                        .setColor(Color.GREEN)
                        .setAuthor(author)
                        .setDescription("Successfully reloaded the config file!"
                        )).update().join();
            }
            // subcommand save:
            // Saves the current state of the configuration to the configuration file.
            case "save" -> {
                // If the user does not have te required permissions, exit.
                if (incidentReport(author, event)) return;

                InteractionOriginalResponseUpdater message = event.createImmediateResponder().addEmbed(new EmbedBuilder()
                        .setTitle("Scheduled Save of the Config File...")
                        .setAuthor(author)
                        .setColor(new Color(0xFFD500))
                        .setDescription("""
                                        Saving the config file...
                                        
                                        Warning! Only edit the file, if you know what you are doing!
                                        
                                        A Guide for the config file can be found at https://mafelp.github.io/mcdc/configuration""")
                ).respond().join();
                Logging.info("Discord User " + ChatColor.DARK_GRAY + author.getName() + ChatColor.RESET + " is saving the configuration file.");
                saveConfiguration();
                Logging.info(ChatColor.GREEN + "Successfully saved the config file!");
                message.removeAllEmbeds().addEmbed(new EmbedBuilder()
                        .setTitle("Success!")
                        .setColor(Color.GREEN)
                        .setAuthor(author)
                        .setDescription("Successfully saved the config file!"
                        )).update().join();
            }
            // subcommand default:
            // resets the configuration to its defaults.
            case "default" -> {
                // If the user does not have te required permissions, exit.
                if (incidentReport(author, event)) return;

                event.createImmediateResponder().addEmbed(new EmbedBuilder()
                        .setTitle("Confirm resetting the Config File...")
                        .setAuthor(author)
                        .setColor(new Color(0xFFD500))
                        .setDescription("Do you really want to reset the configuration to its defaults?")
                ).addComponents(ActionRow.of(
                        Button.success("configResetConfirm", "Confirm"),
                        Button.danger("configResetCancel", "Cancel")
                )).respond().join();
                Logging.info("Discord User " + ChatColor.DARK_GRAY + author.getName() + ChatColor.RESET + " is requesting the config file reset dialogue.");
            }
            // subcommand set:
            // sets a value in the configuration to the specified value
            case "set" -> {
                // If the user does not have the required permissions, exit.
                if (incidentReport(author, event)) return;

                SlashCommandInteractionOption pathOptionObject = options.get(0);
                Optional<String> pathOption = pathOptionObject.getStringValue();
                Optional<String> valueOption = pathOptionObject.getOptionStringValueByIndex(0);

                if (pathOption.isEmpty() || valueOption.isEmpty()) {
                    event.createImmediateResponder().addEmbed(errorEmbed
                            .setDescription("The set command requires two arguments, of which one or more are missing!")
                            .addField("First Argument: Path", "The path in the configuration.")
                            .addField("Second Argument: Value", "The value to set at the path found in the first argument.")
                    ).respond().join();
                    Logging.debug("Path: " + pathOption.orElse("not present") + "; Value: " + valueOption.orElse("not present"));
                    return;
                }
                String path = pathOption.get();
                String valueString = valueOption.get();

                // checks if an argument is present and if so,
                // tries to get a boolean, long and at last a string from the argument.
                if (getStringAsBooleanOption(valueString).isPresent()) {
                    boolean boolValue = getStringAsBooleanOption(valueString).get();
                    Settings.getConfiguration().set(path, boolValue);
                } else if (getStringAsLongOption(valueString).isPresent()) {
                    long longValue = getStringAsLongOption(valueString).get();
                    Settings.getConfiguration().set(path, longValue);
                } else {
                    Settings.getConfiguration().set(path, valueString);
                }

                // Send a success message
                event.createImmediateResponder().addEmbed(new EmbedBuilder()
                        .setAuthor(author)
                        .setTitle("Success!")
                        .setColor(Color.GREEN)
                        .setDescription("Set value `" + path + "` to `" + valueString + "`.\n\nUse `/config save` to save the changes to the config file.")
                ).respond().join();
            }
            // subcommand get:
            // gets the value of a path in the configuration
            case "get" -> {
                // If the user does not have te required permissions, exit.
                if (incidentReport(author, event)) return;

                Optional<String> pathOption = options.get(0).getStringValue();

                if (pathOption.isEmpty()) {
                    event.createImmediateResponder().addEmbed(errorEmbed
                            .setDescription("The add command requires two arguments, of which one or more are missing!")
                            .addField("First Argument: Path", "The path in the configuration to get its value from")
                    ).respond().join();
                    Logging.debug("Path Option present? no");
                    return;
                }

                String path = pathOption.get();
                Object value = getConfiguration().get(path);
                if (value == null) {
                    event.createImmediateResponder().addEmbed(errorEmbed
                            .setDescription("No configuration entry exists for this path! See https://mafelp.github.io/MCDC/configuration for all existing paths!")
                    ).respond().join();
                    Logging.debug("Requested option is non existent in the config.");
                    return;
                }
                String valueString = value.toString();

                // Send a success message
                event.createImmediateResponder().addEmbed(new EmbedBuilder()
                        .setAuthor(author)
                        .setTitle("Config Value")
                        .setColor(new Color(0xC5FF00))
                        .addField(path, valueString)
                ).respond().join();
            }
            // subcommand add
            // adds a value to a list.
            case "add" -> {
                // If the user does not have the required permissions, exit.
                if (incidentReport(author, event)) return;

                SlashCommandInteractionOption pathOptionObject = options.get(0);
                Optional<String> pathOption = pathOptionObject.getStringValue();
                Optional<String> valueOption = pathOptionObject.getOptionStringValueByIndex(0);

                if (pathOption.isEmpty() || valueOption.isEmpty()) {
                    event.createImmediateResponder().addEmbed(errorEmbed
                            .setDescription("The add command requires two arguments, of which one or more are missing!")
                            .addField("First Argument: Path", "The path in the configuration.")
                            .addField("Second Argument: Value", "The value to add to the list, found at the first argument.")
                    ).respond().join();
                    Logging.debug("Path: " + pathOption.orElse("not present") + "; Value: " + valueOption.orElse("not present"));
                    return;
                }
                String path = pathOption.get();
                String valueString = valueOption.get();

                // checks if an argument is present and if so,
                // tries to get a boolean, long and at last a string from the argument.
                if (getStringAsBooleanOption(valueString).isPresent()) {
                    boolean boolValue = getStringAsBooleanOption(valueString).get();
                    List<Boolean> booleanList = Settings.getConfiguration().getBooleanList(path);
                    booleanList.add(boolValue);
                    Settings.getConfiguration().set(path, booleanList);
                } else if (getStringAsLongOption(valueString).isPresent()) {
                    long longValue = getStringAsLongOption(valueString).get();
                    List<Long> longList = Settings.getConfiguration().getLongList(path);
                    longList.add(longValue);
                    Settings.getConfiguration().set(path, longList);
                } else {
                    List<String> stringList = Settings.getConfiguration().getStringList(path);
                    stringList.add(valueString);
                    Settings.getConfiguration().set(path, stringList);
                }

                // Send a success message
                event.createImmediateResponder().addEmbed(new EmbedBuilder()
                        .setAuthor(author)
                        .setTitle("Success!")
                        .setColor(Color.GREEN)
                        .setDescription("Set list `" + path + "` to `" + Settings.getConfiguration().getStringList(path) + "`.\n\nUse `/config save` to save the changes to the config file.")
                ).respond().join();
            }
            // subcommand remove
            // removes a value from a list
            case "remove" -> {
                // If the user does not have the required permissions, exit.
                if (incidentReport(author, event)) return;

                SlashCommandInteractionOption pathOptionObject = options.get(0);
                Optional<String> pathOption = pathOptionObject.getStringValue();
                Optional<String> valueOption = pathOptionObject.getOptionStringValueByIndex(0);

                if (pathOption.isEmpty() || valueOption.isEmpty()) {
                    event.createImmediateResponder().addEmbed(errorEmbed
                            .setDescription("The add command requires two arguments, of which one or more are missing!")
                            .addField("First Argument: Path", "The path in the configuration.")
                            .addField("Second Argument: Value", "The value to remove from the list, found at the first argument.")
                    ).respond().join();
                    Logging.debug("Path: " + pathOption.orElse("not present") + "; Value: " + valueOption.orElse("not present"));
                    return;
                }
                String path = pathOption.get();
                String valueString = valueOption.get();

                // checks if an argument is present and if so,
                // tries to get a boolean, long and at last a string from the argument.
                if (getStringAsBooleanOption(valueString).isPresent()) {
                    boolean boolValue = getStringAsBooleanOption(valueString).get();
                    List<Boolean> booleanList = Settings.getConfiguration().getBooleanList(path);
                    booleanList.remove(boolValue);
                    Settings.getConfiguration().set(path, booleanList);
                } else if (getStringAsLongOption(valueString).isPresent()) {
                    long longValue = getStringAsLongOption(valueString).get();
                    List<Long> longList = Settings.getConfiguration().getLongList(path);
                    longList.remove(longValue);
                    Settings.getConfiguration().set(path, longList);
                } else {
                    List<String> stringList = Settings.getConfiguration().getStringList(path);
                    stringList.remove(valueString);
                    Settings.getConfiguration().set(path, stringList);
                }

                // Send a success message
                event.createImmediateResponder().addEmbed(new EmbedBuilder()
                        .setAuthor(author)
                        .setTitle("Success!")
                        .setColor(Color.GREEN)
                        .setDescription("Removed value `" + valueString + "` from `" + path + "`\n\nits contents are now:\n" + Settings.getConfiguration().getStringList(path) + "\n\nUse `/config save` to save the changes to the config file.")
                ).respond().join();
            }
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

        if (!CheckPermission.checkPermission(Permissions.accountEdit, author.getId())
                && !CheckPermission.checkPermission(Permissions.discordBotAdmin, author.getId())
                && !CheckPermission.checkPermission(Permissions.discordServerAdmin, author.getId())
        ) {
            event.createImmediateResponder().addEmbed(errorEmbed.setDescription("""
                            Sorry, you don't have the required permissions, to execute this command!

                            This incident will be reported!"""
            )).respond().join();
            Logging.info("DC User " + ChatColor.DARK_GRAY + author.getName() + ChatColor.RESET + " tried to execute the command " + ChatColor.DARK_GRAY + "config" + ChatColor.RESET + "! This action was denied due to missing permission!");
            return true;
        }
        return false;
    }

    /**
     * Gets the argument at the index as boolean.
     * @param string A string that could be converted to a boolean, or not.
     * @return the value.
     */
    private static Optional<Boolean> getStringAsBooleanOption(String string) {
            if (string.equalsIgnoreCase("true"))
                return Optional.of(true);
            else if (string.equalsIgnoreCase("false"))
                return Optional.of(false);
            else
                return Optional.empty();
    }

    /**
     * Gets the argument at the index as a Long/int.
     * @param string A string that could be converted to a long, or not
     * @return the value.
     */
    private static Optional<Long> getStringAsLongOption(String string) {
        // Prevents an abort when not a long was passed.
        try {
            return Optional.of(Long.parseLong(string));
        } catch (NumberFormatException numberFormatException) {
            return Optional.empty();
        }
    }
}
