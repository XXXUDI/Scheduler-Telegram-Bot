package com.socompany.springschedulerbot.useceses.util;

import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class StateManager {

    //ChatId -> stack of commands
    private final Map<Long, Deque<Command>> userStateHistory = new ConcurrentHashMap<>();


    public void pushState(Long chatId, Command command) {
        userStateHistory.computeIfAbsent(chatId, k -> new ArrayDeque<>()).push(command);
    }

    public Command popState(Long userId) {
        Deque<Command> commands = userStateHistory.get(userId);
        if (commands != null && commands.size() > 1) {
            commands.pop();
            return commands.peek();
        }
        return null;
    }
}
