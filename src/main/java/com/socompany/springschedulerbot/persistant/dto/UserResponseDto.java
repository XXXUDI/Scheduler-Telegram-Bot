package com.socompany.springschedulerbot.persistant.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Data
@Setter
@Getter
public class UserResponseDto {
    private Long chatId;
    private boolean isAdmin = false;

    private boolean isWeatherReminderEnabled = false;

    private boolean isEventsReminderEnabled = false;

    private boolean isBitcoinPriceReminderEnabled = false;

    private boolean isCurrencyPriceReminderEnabled = false;

    private LocalTime dailyReminderTime;
}
