package com.socompany.springschedulerbot.service;

import com.socompany.springschedulerbot.mapper.UserRequestMapper;
import com.socompany.springschedulerbot.mapper.UserResponseMapper;
import com.socompany.springschedulerbot.persistant.dto.UserRequestDto;
import com.socompany.springschedulerbot.persistant.dto.UserResponseDto;
import com.socompany.springschedulerbot.repository.UserRepository;
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
        return  userRepository.findById(chatId).map(userResponseMapper::map);
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


    public Optional<UserResponseDto> toggleReminderOption(Long chatId, Function<UserResponseDto, UserRequestDto> updater) {
        return findByChatId(chatId)
                .map(updater)
                .flatMap(this::update);
    }


}
