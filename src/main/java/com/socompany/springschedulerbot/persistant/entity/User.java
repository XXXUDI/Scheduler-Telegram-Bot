package com.socompany.springschedulerbot.persistant.entity;

import com.socompany.springschedulerbot.persistant.enums.CountryCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Locale;

@Entity
@Table(name = "user_table")
@Getter @Setter
public class User extends BaseEntity {

    @Id
    private Long chatId;

    private boolean isAdmin = false;

    private boolean isWeatherReminderEnabled = false;

    private boolean isEventsReminderEnabled = false;

    private boolean isBitcoinPriceReminderEnabled = false;

    private boolean isCurrencyPriceReminderEnabled = false;

    private LocalTime dailyReminderTime;

    @Enumerated(EnumType.STRING)
    private CountryCode countryCode = CountryCode.UA;

}
