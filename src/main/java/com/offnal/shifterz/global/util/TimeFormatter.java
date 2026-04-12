package com.offnal.shifterz.global.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static String format(LocalTime time) {
        return time.format(FORMATTER);
    }

    public static String formatRange(LocalTime start, LocalTime end) {
        return format(start) + " ~ " + format(end);
    }
}
