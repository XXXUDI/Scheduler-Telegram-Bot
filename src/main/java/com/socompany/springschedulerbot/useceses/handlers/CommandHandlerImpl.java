package com.socompany.springschedulerbot.useceses.handlers;

import com.socompany.springschedulerbot.common.CommonInfo;

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

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.*;

@Service
@Slf4j
public class CommandHandlerImpl implements CommandHandler {

    private final Map<String, Command>  commandMap = new ConcurrentHashMap<>();
    private final StateManager stateManager;


    public CommandHandlerImpl(
            StateManager stateManager,
            StartMenuCommand startMenuCommand,
            SchedulerMenuCommand schedulerMenuCommand,
            GoBackCommand goBackCommand) {
        this.stateManager = stateManager;
        commandMap.put(BACK.getCommand(), goBackCommand);

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

        if(cmd != null) {
            if(!command.equals("/back")) {
                stateManager.pushState(commonInfo.getChatId(), cmd);
            }
            cmd.execute(commonInfo);
        } else{
            log.warn("Command {} not found in commandMap", command);
        }
    }

}
