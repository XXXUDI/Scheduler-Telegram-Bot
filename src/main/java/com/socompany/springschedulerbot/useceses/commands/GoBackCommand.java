package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import com.socompany.springschedulerbot.useceses.util.StateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoBackCommand implements Command {

    private final StateManager stateManager;

    @Override
    public void execute(CommonInfo commonInfo) {
        Command previous = stateManager.popState(commonInfo.getChatId());
        if(previous != null) {
            log.info("Executing previous command for user {}", commonInfo.getChatId());
            previous.execute(commonInfo);
        } else {
            log.warn("No previous state for user {}", commonInfo.getChatId());
        }
    }
}
