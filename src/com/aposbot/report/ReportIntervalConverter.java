package com.aposbot.report;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportIntervalConverter {
    private static long DEFAULT_INTERVAL = TimeUnit.MINUTES.toMillis(10);

    public static long convertToMillis(String duration) {
        if (duration == null)
            return DEFAULT_INTERVAL;

        Pattern p = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2})");
        Matcher matcher = p.matcher(duration);

        if (!matcher.matches())
            return DEFAULT_INTERVAL;

        int hours = Integer.parseInt(matcher.group(1));
        int mins = Integer.parseInt(matcher.group(2));
        int secs = Integer.parseInt(matcher.group(3));

        if (hours == 0 && mins == 0 && secs == 0)
            return DEFAULT_INTERVAL;

        return TimeUnit.HOURS.toMillis(hours) +
                TimeUnit.MINUTES.toMillis(mins) +
                TimeUnit.SECONDS.toMillis(secs);
    }
}
