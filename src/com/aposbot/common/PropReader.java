package com.aposbot.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class PropReader {
    
    private static final String PROPERTIES_FILE = "." + File.separator + "conf" + File.separator + "bot.properties";

    public static Properties getProperties() {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            p.load(in);
            if (!p.containsKey("font")) {
                p.put("font", "");
            }
            return p;
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static void storeProperties(Properties p) {
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE)) {
            p.store(out, null);
        } catch (final Throwable t) {
            System.out.println("Error storing updated properties: " + t);
        }
    }
}
