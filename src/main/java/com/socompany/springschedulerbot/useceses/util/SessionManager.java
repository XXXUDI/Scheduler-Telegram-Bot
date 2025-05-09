package com.socompany.springschedulerbot.useceses.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SessionManager {

    private final Map<Long, String> userSession = new ConcurrentHashMap<>();

    public void setSession(Long chatId, String state) {
        userSession.put(chatId, state);
    }

    public void removeSession(Long chatId) {
        userSession.remove(chatId);
    }

    public boolean isAwaitingInput(Long chatId, String state) {
        return state.equals(userSession.get(chatId));
    }

}
