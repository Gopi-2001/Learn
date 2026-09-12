package com.airtribe.meditrack.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private DateUtil() { }
    public static LocalDateTime parse(String input) { return LocalDateTime.parse(input, FORMATTER); }
    public static String format(LocalDateTime dateTime) { return dateTime.format(FORMATTER); }
}
