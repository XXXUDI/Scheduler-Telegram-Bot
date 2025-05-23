package com.socompany.springschedulerbot.useceses.handlers;

import com.socompany.springschedulerbot.common.CommonInfo;

import com.socompany.springschedulerbot.service.UserService;
import com.socompany.springschedulerbot.useceses.commands.*;
import com.socompany.springschedulerbot.useceses.commands.enums.CommandType;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import com.socompany.springschedulerbot.useceses.util.StateManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.*;

@Service
@Slf4j
public class CommandHandlerImpl implements CommandHandler {
    // Beans
    private final StateManager stateManager;
    private final UserService userService;

    private final Map<String, Command>  commandMap = new ConcurrentHashMap<>();
    private final List<String> TOGGLE_FUNCTIONS = List.of(
            WEATHER.getCommand(),
            EVENTS.getCommand(),
            BITCOIN.getCommand(),
            CURRENCY.getCommand()
    );

    // Constructor :)
    public CommandHandlerImpl(
            StateManager stateManager,
            StartMenuCommand startMenuCommand,
            SchedulerMenuCommand schedulerMenuCommand,
            ChangeDateCommand changeDateCommand,
            ChangeTimeZoneCommand changeTimeZoneCommand,
            AddTaskCommand addTaskCommand,
            DeleteTaskCommand deleteTaskCommand,
            SettingsMenuCommand settingsMenuCommand,
            AboutMenuCommand aboutMenuCommand,
            ToDoMenuCommand toDoMenuCommand,
            GoBackCommand goBackCommand,
            WrongCommand wrongCommand,
            UserService userService) {
        this.stateManager = stateManager;
        this.userService = userService;

        // Back Button Command
        commandMap.put(BACK.getCommand(), goBackCommand);

        // Wrong Command
        commandMap.put(WRONG_COMMAND.getCommand(), wrongCommand);

        // Menu Commands
        commandMap.put(START.getCommand(), startMenuCommand);
        commandMap.put(ADD_TASK.getCommand(), addTaskCommand);
        commandMap.put(DELETE_TASK.getCommand(), deleteTaskCommand);
        commandMap.put(ABOUT.getCommand(), aboutMenuCommand);
        commandMap.put(TODO_MENU.getCommand(), toDoMenuCommand);
        commandMap.put(SCHEDULER.getCommand(), schedulerMenuCommand);
        commandMap.put(SETTINGS.getCommand(), settingsMenuCommand);

        // Commands to change time or timezone

        commandMap.put(CHANGE_DATE.getCommand(), changeDateCommand);
        commandMap.put(CHANGE_TIMEZONE.getCommand(), changeTimeZoneCommand);
        // ...
    }

    @Override
    @Async
    public void handleCommand(String command, CommonInfo commonInfo) {
        log.info("Thread {}, Handling command: {}, User: {}",
                Thread.currentThread().getName(), command, commonInfo.getChatId());
        Command cmd = commandMap.get(command);

        if (TOGGLE_FUNCTIONS.contains(command)) {
            log.info("User {} toggled option {}", commonInfo.getChatId(), command);
            userService.toggleReminderOption(commonInfo.getChatId(), CommandType.getCommandType(command));
            cmd = commandMap.get(SCHEDULER.getCommand());
        }

        if(cmd != null) {
            if(isState(command))  {
                stateManager.pushState(commonInfo.getChatId(), cmd);
            }
            cmd.execute(commonInfo);
        } else{
            log.warn("Command {} not found in commandMap", command);
            commandMap.get(WRONG_COMMAND.getCommand()).execute(commonInfo);
        }
    }

    private boolean isState(String command) {
        return !command.equals("/back") && !TOGGLE_FUNCTIONS.contains(command)
                && !command.equals(CHANGE_DATE.getCommand()) && !command.equals(CHANGE_TIMEZONE.getCommand())
                && !command.equals(DELETE_TASK.getCommand()) && !command.equals(ADD_TASK.getCommand())
                && !command.equals(WRONG_COMMAND.getCommand());
    }

}

