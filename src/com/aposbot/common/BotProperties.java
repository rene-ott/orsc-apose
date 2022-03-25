package com.aposbot.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class BotProperties {

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

    public static String getReportApiUrl() {
        return getNonEmptyValueOrNull(BotProperties.getProperties(), "report_api_url");
    }

    public static String getReportApiKey() {
        return getNonEmptyValueOrNull(BotProperties.getProperties(), "report_api_key");
    }

    public static String getReportInterval() {
        return getNonEmptyValueOrNull(BotProperties.getProperties(), "report_interval");
    }

    public static String getProxyHost() {
        return getNonEmptyValueOrNull(BotProperties.getProperties(), "proxy_host");
    }

    public static String getProxyPort() {
        return getNonEmptyValueOrNull(BotProperties.getProperties(), "proxy_port");
    }

    public static String getProxyUsername() {
        return getNonEmptyValueOrNull(BotProperties.getProperties(), "proxy_username");
    }

    public static String getProxyPassword() {
        return getNonEmptyValueOrNull(BotProperties.getProperties(), "proxy_password");
    }

    private static String getNonEmptyValueOrNull(Properties properties, String key) {
        if (properties == null) {
            return null;
        }
        String val = properties.getProperty(key);

        if (val == null) {
            return null;
        }

        if (!val.equals("")) {
            return val;
        }

        return null;
    }
}
