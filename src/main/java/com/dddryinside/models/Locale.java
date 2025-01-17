package com.dddryinside.models;

import lombok.Getter;

@Getter
public enum Locale {
    RU( "Русский", "ru_locale"),
    BY( "Беларуская", "by_locale"),
    UA( "Украінская", "ua_locale"),
    PL("Polski", "pl_locale"),
    EN( "English", "en_locale");

    private final String name;
    private final String file;

    Locale(String name, String file) {
        this.name = name;
        this.file = file;
    }
}
