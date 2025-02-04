package com.main.OnboardingService.util;

public class StringUtils {

    public static boolean isBlank(String str) {
        return str.isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
