package com.github.mafelp.minecraft;

import com.github.mafelp.utils.Settings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Test Class used for INTERNAL TESTING AND DEBUGGING PURPOSES ONLY!!!
 */
public class TestMain {
    /**
     * Main start/debugging entry point of the program run without the plugin. -
     * This is used to debug algorithms
     * @param args The arguments parsed into the shell.
     */
    public static void main(String[] args) {

        InputStream is = Settings.class.getClassLoader().getResourceAsStream("defaultConfiguration.yml");

        try {
            if (is != null) {
                FileOutputStream fileOutputStream = new FileOutputStream("./plugins/MCDC/config.yml");
                fileOutputStream.write(is.readAllBytes());
            } else {
                System.out.println("Could not read from the default configuration resource");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
