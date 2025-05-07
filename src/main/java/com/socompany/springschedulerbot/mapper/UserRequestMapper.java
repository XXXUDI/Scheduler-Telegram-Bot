package com.socompany.springschedulerbot.mapper;

import com.socompany.springschedulerbot.persistant.dto.UserRequestDto;
import com.socompany.springschedulerbot.persistant.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserRequestMapper implements Mapper<UserRequestDto, User> {
    @Override
    public User map(UserRequestDto from) {

        User user = new User();
        user.setDailyReminderTime(from.getDailyReminderTime());
        user.setAdmin(from.isAdmin());
        user.setChatId(from.getChatId());
        user.setWeatherReminderEnabled(from.isWeatherReminderEnabled());
        user.setCurrencyPriceReminderEnabled(from.isCurrencyPriceReminderEnabled());
        user.setEventsReminderEnabled(from.isEventsReminderEnabled());
        user.setBitcoinPriceReminderEnabled(from.isBitcoinPriceReminderEnabled());

        return user;
    }

    @Override
    public User map(UserRequestDto fromObj, User toObj) {

        toObj.setDailyReminderTime(fromObj.getDailyReminderTime());
        toObj.setAdmin(fromObj.isAdmin());
        toObj.setChatId(fromObj.getChatId());
        toObj.setCurrencyPriceReminderEnabled(fromObj.isCurrencyPriceReminderEnabled());
        toObj.setEventsReminderEnabled(fromObj.isEventsReminderEnabled());
        toObj.setBitcoinPriceReminderEnabled(fromObj.isBitcoinPriceReminderEnabled());
        toObj.setWeatherReminderEnabled(fromObj.isWeatherReminderEnabled());

        return toObj;
    }
}
