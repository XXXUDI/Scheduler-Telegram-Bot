package com.socompany.springschedulerbot.service;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.mapper.UserRequestMapper;
import com.socompany.springschedulerbot.mapper.UserResponseMapper;
import com.socompany.springschedulerbot.persistant.dto.UserRequestDto;
import com.socompany.springschedulerbot.persistant.dto.UserResponseDto;
import com.socompany.springschedulerbot.persistant.entity.User;
import com.socompany.springschedulerbot.persistant.enums.CountryCode;
import com.socompany.springschedulerbot.repository.UserRepository;
import com.socompany.springschedulerbot.useceses.commands.enums.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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

    // For local debugging
    public List<UserResponseDto> findByDailyReminderTime(LocalTime time) {
        return userRepository.findByDailyReminderTime(time).stream().map(userResponseMapper::map).toList();
    }

    // Method which supports Time Zone
    public List<UserResponseDto> findByDailyReminderForReminderAtCurrentTime() {
        List<User> users = userRepository.findUsersWithAnyReminderEnabled();

        LocalTime nowSystemTime = LocalTime.now().withSecond(0).withNano(0);

        return users.stream()
                .filter(user -> {
                    String timeZone = user.getCountryCode().getTimeZone();
                    LocalTime userLocalTime = nowSystemTime.atDate(LocalDate.now())
                            .atZone(ZoneId.systemDefault())
                            .withZoneSameInstant(ZoneId.of(timeZone))
                            .toLocalTime();
                    return userLocalTime.equals(user.getDailyReminderTime());
                        }).map(userResponseMapper::map).toList();
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

    public void registerUser(CommonInfo commonInfo) {
        // Створення користувача з дефолтними налаштуваннями
        UserRequestDto newUser = new UserRequestDto();
        newUser.setChatId(commonInfo.getChatId());
        newUser.setAdmin(false); // дефолтне значення
        newUser.setWeatherReminderEnabled(false); // дефолтне значення
        newUser.setEventsReminderEnabled(false); // дефолтне значення
        newUser.setBitcoinPriceReminderEnabled(false); // дефолтне значення
        newUser.setCurrencyPriceReminderEnabled(false); // дефолтне значення
        newUser.setDailyReminderTime(null);
        newUser.setCountryCode(CountryCode.UA);// дефолтне значення або час за замовчуванням

        // Виклик `UserService` для збереження користувача в базу
        log.info("Registering new user with chatId {}", commonInfo.getChatId());
        save(newUser);

        // Відразу створюємо SchedulerMenu для нового користувача
    }

    public Optional<UserResponseDto> updateCountryCode(Long chatId, CountryCode countryCode) {
        return userRepository.findByChatId(chatId)
                .map(user -> {
                    UserRequestDto updatedUser = convertUserToUserRequestDto(user);
                    updatedUser.setCountryCode(countryCode);
                    return userRequestMapper.map(updatedUser, user);
                })
                .map(userRepository::saveAndFlush)
                .map(userResponseMapper::map);
    }

    public Optional<UserResponseDto> toggleReminderOption(Long chatId, CommandType reminderOption) {
        log.info("Toggling reminder option {} for chatId {}", reminderOption, chatId);
        return userRepository.findByChatId(chatId)
                .map(user -> {
                    UserRequestDto updatedUser = updateUserReminderOption(user, reminderOption);
                    return userRequestMapper.map(updatedUser, user);
                })
                .map(userRepository::saveAndFlush)
                .map(userResponseMapper::map);
    }

    public Optional<UserResponseDto> updateReminderTime(Long chatId, LocalTime time) {
        return userRepository.findByChatId(chatId)
                .map(user -> {
                    user.setDailyReminderTime(time);
                    UserRequestDto updatedUser = convertUserToUserRequestDto(user);
                    updatedUser.setDailyReminderTime(time);
                    return userRequestMapper.map(updatedUser, user);
                })
                .map(userRepository::saveAndFlush)
                .map(userResponseMapper::map);
    }



    private UserRequestDto updateUserReminderOption(User user, CommandType reminderOption) {
        UserRequestDto result = convertUserToUserRequestDto(user);

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

    private UserRequestDto convertUserToUserRequestDto(User user) {
        UserRequestDto result = new UserRequestDto();
        result.setChatId(user.getChatId());
        result.setAdmin(user.isAdmin());
        result.setEventsReminderEnabled(user.isEventsReminderEnabled());
        result.setWeatherReminderEnabled(user.isWeatherReminderEnabled());
        result.setBitcoinPriceReminderEnabled(user.isBitcoinPriceReminderEnabled());
        result.setDailyReminderTime(user.getDailyReminderTime());
        result.setCurrencyPriceReminderEnabled(user.isCurrencyPriceReminderEnabled());
        result.setCountryCode(user.getCountryCode());

        return result;
    }


}
