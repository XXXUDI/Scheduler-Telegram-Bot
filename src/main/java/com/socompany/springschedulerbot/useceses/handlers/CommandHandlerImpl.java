package com.socompany.springschedulerbot.useceses.handlers;

import com.socompany.springschedulerbot.common.CommonInfo;

import com.socompany.springschedulerbot.persistant.dto.UserRequestDto;
import com.socompany.springschedulerbot.persistant.dto.UserResponseDto;
import com.socompany.springschedulerbot.service.UserService;
import com.socompany.springschedulerbot.useceses.commands.GoBackCommand;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import com.socompany.springschedulerbot.useceses.commands.SchedulerMenuCommand;
import com.socompany.springschedulerbot.useceses.commands.StartMenuCommand;
import com.socompany.springschedulerbot.useceses.util.StateManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.*;

@Service
@Slf4j
public class CommandHandlerImpl implements CommandHandler {
    private final Map<String, Command>  commandMap = new ConcurrentHashMap<>();
    private final StateManager stateManager;
    private final UserService userService;

    // Toggle functions like /weather | /bitcoin ...
    private static final Map<String, Function<UserResponseDto, UserRequestDto>> TOGGLE_FUNCTIONS = Map.of(
            WEATHER.getCommand(), user -> toggleField(user, u -> u.setWeatherReminderEnabled(!u.isWeatherReminderEnabled())),
            EVENTS.getCommand(), user -> toggleField(user, u -> u.setEventsReminderEnabled(!u.isEventsReminderEnabled())),
            BITCOIN.getCommand(), user -> toggleField(user, u -> u.setBitcoinPriceReminderEnabled(!u.isBitcoinPriceReminderEnabled())),
            CURRENCY.getCommand(), user -> toggleField(user, u -> u.setCurrencyPriceReminderEnabled(!u.isCurrencyPriceReminderEnabled()))
    );

    // Constructor :)
    public CommandHandlerImpl(
            StateManager stateManager,
            StartMenuCommand startMenuCommand,
            SchedulerMenuCommand schedulerMenuCommand,
            GoBackCommand goBackCommand, UserService userService) {
        this.stateManager = stateManager;
        this.userService = userService;

        // Back Button Command
        commandMap.put(BACK.getCommand(), goBackCommand);

        // Menu Commands
        commandMap.put(START.getCommand(), startMenuCommand);
        commandMap.put(SCHEDULER.getCommand(), schedulerMenuCommand);
        // ...
    }

    @Override
    @Async
    public void handleCommand(String command, CommonInfo commonInfo) {
        log.info("Thread {}, Handling command: {}, User: {}",
                Thread.currentThread().getName(), command, commonInfo.getChatId());
        Command cmd = commandMap.get(command);

        if (TOGGLE_FUNCTIONS.containsKey(command)) {
            handleToggleCommand(command, commonInfo);
            commandMap.get(SCHEDULER.getCommand()).execute(commonInfo);
        }

        if(cmd != null) {
            if(!command.equals("/back")) {
                stateManager.pushState(commonInfo.getChatId(), cmd);
            }
            cmd.execute(commonInfo);
        } else{
            log.warn("Command {} not found in commandMap", command);
        }
    }

    private static UserRequestDto toggleField(UserResponseDto user, Consumer<UserRequestDto> fieldUpdater) {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setChatId(user.getChatId());
        fieldUpdater.accept(userRequestDto);
        return userRequestDto;
    }

    public void handleToggleCommand(String commandType, CommonInfo commonInfo) {
        Function<UserResponseDto, UserRequestDto> toggleFunction = TOGGLE_FUNCTIONS.get(commandType);
        if (toggleFunction == null) {
            log.warn("Command {} is not supported for toggling", commandType);
            return;
        }

        userService.findByChatId(commonInfo.getChatId()).ifPresentOrElse(
                user -> {
                    userService.toggleReminderOption(commonInfo.getChatId(), toggleFunction);
                },
                () -> log.warn("User with chatId {} not found", commonInfo.getChatId())
        );
    }

}
