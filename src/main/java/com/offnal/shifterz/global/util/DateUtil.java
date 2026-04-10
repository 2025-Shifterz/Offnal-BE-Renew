package com.offnal.shifterz.global.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    private DateUtil() {}

    public static boolean isDateFilter(String filter) {
        if (filter == null || filter.isBlank()) {
            return false;
        }

        try {
            LocalDate.parse(filter, DateTimeFormatter.ISO_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}



