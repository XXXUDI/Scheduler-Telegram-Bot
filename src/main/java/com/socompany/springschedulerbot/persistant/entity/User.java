package com.socompany.springschedulerbot.persistant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

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

}
