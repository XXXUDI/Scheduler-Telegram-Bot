package com.socompany.springschedulerbot.persistant.enums;

import java.util.stream.Collectors;

public enum CountryCode {
    US("America/New_York"),
    CA("America/Toronto"),
    GB("Europe/London"),
    DE("Europe/Berlin"),
    FR("Europe/Paris"),
    IT("Europe/Rome"),
    ES("Europe/Madrid"),
    PL("Europe/Warsaw"),
    RU("Europe/Moscow"),
    UA("Europe/Kyiv"),
    IN("Asia/Kolkata"),
    CN("Asia/Shanghai"),
    JP("Asia/Tokyo"),
    KR("Asia/Seoul"),
    BR("America/Sao_Paulo"),
    AU("Australia/Sydney"),
    ZA("Africa/Johannesburg"),
    NG("Africa/Lagos"),
    TR("Europe/Istanbul"),
    MX("America/Mexico_City");

    private final String timeZone;

    CountryCode(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public static CountryCode getCountryCode(String code) {
        for (CountryCode countryCode : CountryCode.values()) {
            if (countryCode.name().equalsIgnoreCase(code)) {
                return countryCode;
            }
        }
        throw new IllegalArgumentException("No country code found for " + code);
    }

    public String getFlagEmoji() {
        String country = this.name();
        int OFFSET = 0x1F1E6 - 'A';

        return country.chars()
                .mapToObj(c -> String.valueOf((char) (c + OFFSET)))
                .collect(Collectors.joining());
    }
}

