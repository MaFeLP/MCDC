package com.github.mafelp.minecraft.commands;

import com.github.mafelp.utils.CheckPermission;
import com.github.mafelp.utils.Logging;
import com.github.mafelp.utils.Permissions;
import com.github.mafelp.utils.Settings;
import com.github.mafelp.discord.DiscordMain;
import jdk.jshell.spi.SPIResolutionException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * "/token" command - use: set the discord api token with a command,
 * instead of using the configuration file
 */
public class Token implements CommandExecutor {
    /**
     * Command handler for command "/token"
     * @param commandSender the sender of the command
     * @param command the command executed by the sender
     * @param label the label of the command
     * @param args arguments parsed by the sender after the command
     * @return success state
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        // Only execute if the sender is an operator
        if (!CheckPermission.checkPermission(Permissions.configEdit, commandSender)) {
            commandSender.sendMessage(Settings.prefix + ChatColor.RED + "Sorry, you do not have the permissions to execute this command.\n" + Settings.prefix + ChatColor.RED + "This incident will be reported.");
            Logging.info("User " + commandSender.getName() + " tried to execute command /token.");
            return true;
        }

        // check if no, or more then one argument were passed
        if (args == null || args.length != 1) {
            // if so, return a fail
            // and send the sender the help message
            commandSender.sendMessage(helpMessage);
            return false;
        }

        // if a bot is running or was already created, log it off and signify this to the sender
        if (Settings.discordApi != null) {
            // try to shut the bot down
            try {
                DiscordMain.shutdown();
                commandSender.sendMessage(Settings.prefix + "Disconnected the Discord bot.");
            } catch (Exception e) {
                // if the bot could not be disconnected, return a fail and send the sender an error message
                commandSender.sendMessage(Settings.prefix + ChatColor.RED + "Could not disconnect the Discord Bot!\n" +
                        "The full error message can be seen in the console!");
                // also log the complete error stack trace to the console
                Logging.logException(e, "Disconnecting the bot, failed!");
                return false;
            }
        }
        // try to log the bot in
        try {
            // set the token in the configuration
            Settings.getConfiguration().set("apiToken", args[0]);
            // Save the configuration to the file
            Settings.saveConfiguration();
            // reload configuration
            Settings.init();
            // try to log the bot in
            Thread discordInitThread = new DiscordMain();
            discordInitThread.setName("Initializing the Discord instance.");
            discordInitThread.start();
            // send success messages and return
            commandSender.sendMessage(Settings.prefix + ChatColor.GREEN + "Successfully saved config file!");
            commandSender.sendMessage(Settings.prefix + ChatColor.GREEN + "Using token: " + ChatColor.GRAY + Settings.getApiToken());
            return true;
        } catch (Exception exception) {
            // if something goes wrong: return a failure,
            // set send the sender an error message
            // and log the complete error stack trace to the console
            commandSender.sendMessage(Settings.prefix +
                    "An error appeared during the part reload.\n" +
                    "The error has been logged to the console.");
            Logging.logException(exception, "An error appeared during the setting of the discord api token!");
            return false;
        }
    }

    /**
     * The help message used, when the command had invalid parameters
     */
    private static final String helpMessage = Settings.prefix
            + "Wrong usage!\nUse \"/token <Your Discord Token>\"!";
}
