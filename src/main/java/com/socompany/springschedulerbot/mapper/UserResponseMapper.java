package com.socompany.springschedulerbot.mapper;

import com.socompany.springschedulerbot.persistant.dto.UserResponseDto;
import com.socompany.springschedulerbot.persistant.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserResponseMapper implements Mapper<User, UserResponseDto>{

    @Override
    public UserResponseDto map(User user) {
        UserResponseDto responseDto = new UserResponseDto();

        responseDto.setDailyReminderTime(user.getDailyReminderTime());
        responseDto.setChatId(user.getChatId());
        responseDto.setAdmin(user.isAdmin());

        responseDto.setCountryCode(user.getCountryCode());

        responseDto.setEventsReminderEnabled(user.isEventsReminderEnabled());
        responseDto.setWeatherReminderEnabled(user.isWeatherReminderEnabled());
        responseDto.setCurrencyPriceReminderEnabled(user.isCurrencyPriceReminderEnabled());
        responseDto.setBitcoinPriceReminderEnabled(user.isBitcoinPriceReminderEnabled());

        return responseDto;
    }
}
