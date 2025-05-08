package com.socompany.springschedulerbot.service;

import com.socompany.springschedulerbot.mapper.UserRequestMapper;
import com.socompany.springschedulerbot.mapper.UserResponseMapper;
import com.socompany.springschedulerbot.persistant.dto.UserRequestDto;
import com.socompany.springschedulerbot.persistant.dto.UserResponseDto;
import com.socompany.springschedulerbot.persistant.entity.User;
import com.socompany.springschedulerbot.repository.UserRepository;
import com.socompany.springschedulerbot.useceses.commands.enums.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserRequestMapper userRequestMapper;
    private final UserResponseMapper userResponseMapper;


    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream().map(userResponseMapper::map).toList();
    }

    public Optional<UserResponseDto> findByChatId(Long chatId) {
        return userRepository.findById(chatId).map(userResponseMapper::map);
    }

    public UserResponseDto save(UserRequestDto userRequestDto) {
        return Optional.of(userRequestDto)
                .map(dto -> {
                    return userRequestMapper.map(userRequestDto);
                })
                .map(userRepository::save)
                .map(userResponseMapper::map)
                .orElseThrow();
    }

    public Optional<UserResponseDto> update(UserRequestDto userRequestDto) {
        return userRepository.findByChatId(userRequestDto.getChatId())
                .map(user -> userRequestMapper.map(userRequestDto, user))
                .map(userRepository::saveAndFlush)
                .map(userResponseMapper::map);
    }

    public Optional<UserResponseDto> toggleReminderOption(Long chatId, CommandType reminderOption) {
        log.info("Toggling reminder option {} for chatId {}", reminderOption, chatId);
        return userRepository.findByChatId(chatId)
                .map(user -> {
                    UserRequestDto updatedUser = updateUserReminder(user, reminderOption);
                    return userRequestMapper.map(updatedUser, user);
                })
                .map(userRepository::saveAndFlush)
                .map(userResponseMapper::map);
    }

    public UserRequestDto updateUserReminder(User user, CommandType reminderOption) {
        UserRequestDto result = new UserRequestDto();
        result.setChatId(user.getChatId());
        result.setAdmin(user.isAdmin());
        result.setEventsReminderEnabled(user.isEventsReminderEnabled());
        result.setWeatherReminderEnabled(user.isWeatherReminderEnabled());
        result.setBitcoinPriceReminderEnabled(user.isBitcoinPriceReminderEnabled());
        result.setDailyReminderTime(user.getDailyReminderTime());
        result.setDailyReminderTime(user.getDailyReminderTime());

        switch (reminderOption) {
            case WEATHER:
                result.setWeatherReminderEnabled(!user.isWeatherReminderEnabled());
                break;
            case EVENTS:
                result.setEventsReminderEnabled(!user.isEventsReminderEnabled());
                break;
            case BITCOIN:
                result.setBitcoinPriceReminderEnabled(!user.isBitcoinPriceReminderEnabled());
                break;
            case CURRENCY:
                result.setCurrencyPriceReminderEnabled(!user.isCurrencyPriceReminderEnabled());
                break;
            default:
                log.error("Unsupported reminder option: {}", reminderOption);
                throw new IllegalArgumentException("Unsupported reminder option: " + reminderOption);
        }
        return result;
    }


}
