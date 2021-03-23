package com.github.mafelp;

import java.io.IOException;

public class Logging {
    public static void logException(Exception exception, String logMessage) {

        Settings.minecraftServer.getLogger().warning(
                Settings.prefix + logMessage + " Error: " + exception.getMessage()
        );

        if (Settings.debug) {
            for (StackTraceElement s :
                    exception.getStackTrace()) {
               Settings.minecraftServer.getLogger().warning("\t" + s.toString());
            }
        }
    }

    public static void logIOException(IOException exception, String logMessage) {
        Settings.minecraftServer.getLogger().warning(
                Settings.prefix + logMessage + " Error: " + exception.getMessage()
        );

        if (Settings.debug) {
            for (StackTraceElement s :
                    exception.getStackTrace()) {
                Settings.minecraftServer.getLogger().warning("\t" + s.toString());
            }
        }
    }

    public static void info(String message) {
        Settings.minecraftServer.getLogger().info(Settings.prefix + message);
    }
}