package com.airtribe.meditrack.interfaces;

public interface Searchable {
    boolean matches(String keyword);

    default String searchableText() {
        return "Search enabled";
    }
}
