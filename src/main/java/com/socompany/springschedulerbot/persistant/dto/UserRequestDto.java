package com.socompany.springschedulerbot.persistant.dto;

import com.socompany.springschedulerbot.persistant.enums.CountryCode;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UserRequestDto {

    private Long chatId;

    private boolean isAdmin = false;

    private boolean isWeatherReminderEnabled = false;

    private boolean isEventsReminderEnabled = false;

    private boolean isBitcoinPriceReminderEnabled = false;

    private boolean isCurrencyPriceReminderEnabled = false;

    private LocalTime dailyReminderTime;

    private CountryCode countryCode;
}
