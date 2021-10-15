package com.aposbot.report;

import com.aposbot.common.BotPropReader;

import java.util.Properties;

public class ReportPropReader {

    public static String getApiUrl() {
        return getNonEmptyValueOrNull(BotPropReader.getProperties(), "report_api_url");
    }

    public static String getApiKey() {
        return getNonEmptyValueOrNull(BotPropReader.getProperties(), "report_api_key");
    }

    public static String getInterval() {
        return getNonEmptyValueOrNull(BotPropReader.getProperties(), "report_interval");
    }

    private static String getNonEmptyValueOrNull(Properties properties, String key) {
        if (properties == null) {
            return null;
        }
        String val = properties.getProperty(key);
        if (!val.equals("")) {
            return val;
        }

        return null;
    }
}
