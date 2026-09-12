package com.airtribe.meditrack.util;

public final class AppConfig {
    private static AppConfig instance;
    private AppConfig() { }
    public static synchronized AppConfig getInstance() {
        if (instance == null) instance = new AppConfig();
        return instance;
    }
}
