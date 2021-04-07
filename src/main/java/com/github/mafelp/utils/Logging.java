package com.github.mafelp.utils;

import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

/**
 * Logging class to make logging easier and implement a global logging syntax.
 */
public class Logging {
    /**
     * Logs a general exception with the general syntax.
     * @param exception The exception that should be logged.
     * @param logMessage The message that should be logged before the error to describe, where the error occurred.
     */
    public static void logException(Exception exception, String logMessage) {
        // Log the error
        Settings.minecraftServer.getLogger().warning(
                Settings.prefix + logMessage + " Error: " + exception.getMessage()
        );

        // Print the Stack trace, when debug mode is enabled.
        if (Settings.debug) {
            for (StackTraceElement s :
                    exception.getStackTrace()) {
               Settings.minecraftServer.getLogger().warning("\t" + s.toString());
            }
        }
    }

    /**
     * Logs an IOException with the general syntax.
     * @param exception The exception that should be logged.
     * @param logMessage The message that should be logged before the error to describe, where the error occurred.
     */
    public static void logIOException(IOException exception, String logMessage) {
        // Log the error
        Settings.minecraftServer.getLogger().warning(
                Settings.prefix + logMessage + " Error: " + exception.getMessage()
        );

        // Print the Stack trace, when debug mode is enabled.
        if (Settings.debug) {
            for (StackTraceElement s :
                    exception.getStackTrace()) {
                Settings.minecraftServer.getLogger().warning("\t" + s.toString());
            }
        }
    }

    /**
     * Logs an InvalidConfigurationException for YAML configurations. -
     * Normally this method will not be called.
     * @param exception The exception that should be logged.
     * @param logMessage The message that should be logged before the error to describe, where the error occurred.
     */
    public static void logInvalidConfigurationException(InvalidConfigurationException exception, String logMessage) {
        // log the exception
        Settings.minecraftServer.getLogger().warning(
                Settings.prefix + logMessage + " Error: " + exception.getMessage()
        );

        // Log the stack strace, if debug is enabled.
        if (Settings.debug) {
            for (StackTraceElement stackTraceElement :
                    exception.getStackTrace()) {
                Settings.minecraftServer.getLogger().warning("\t" + stackTraceElement.toString());
            }
        }
    }

    /**
     * Log a simple info message with the plugin syntax.
     * @param message The message that should be logged.
     */
    public static void info(String message) {
        Settings.minecraftServer.getLogger().info(Settings.prefix + message);
    }

    /**
     * Log a info message, if the <code>debug</code> option is enabled in the <code>config.yml</code> file
     * @param message The message that should be logged.
     */
    public static void debug(String message) {
        if (Settings.debug)
            Settings.minecraftServer.getLogger().info(Settings.prefix + message);
    }
}
