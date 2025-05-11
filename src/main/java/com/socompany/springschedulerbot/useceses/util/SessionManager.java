package com.socompany.springschedulerbot.useceses.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SessionManager {

    private final Map<Long, UserSession> userSession = new ConcurrentHashMap<>();

    /**
     * Sets a session with the current state for the user
     */
    public void setSession(Long chatId, String state) {
        userSession.put(chatId, new UserSession(state));
    }

    /**
     * Deletes a user session
     */
    public void clearSession(Long chatId) {
        userSession.remove(chatId);
    }

    /**
     * Checks if the user is expecting a specific input.
     */
    public boolean isAwaitingInput(Long chatId, String state) {
        UserSession session = userSession.get(chatId);
        return session != null && state.equals(session.getState());
    }

    /**
     * Sets temporary data for the user.
     */
    public void setTemporaryData(Long chatId, String key, String value) {
        UserSession session = userSession.computeIfAbsent(chatId, k -> new UserSession(null));
        session.setTemporaryData(key, value);
    }

    /**
     * Gets temporary user data
     */
    public String getTemporaryData(Long chatId, String key) {
        UserSession session = userSession.get(chatId);
        return session != null ? session.getTemporaryData(key) : null;
    }

    /**
     * Sets a new status (state) for a user session
     */
    public void setStatus(Long chatId, String status) {
        UserSession session = userSession.computeIfAbsent(chatId, k -> new UserSession(null));
        session.setState(status);
    }

    /**
     * Returns the current status (state) of the user
     */
    public String getStatus(Long chatId) {
        UserSession session = userSession.get(chatId);
        return session != null ? session.getState() : null;
    }

    /**
     *  Inner class for storing user state and temporary data
     */
    private static class UserSession {
        @Setter
        @Getter
        private String state; // Текущее состояние (например, AWAITING_TASK_TITLE)
        private final Map<String, String> temporaryData = new ConcurrentHashMap<>();

        public UserSession(String state) {
            this.state = state;
        }

        public void setTemporaryData(String key, String value) {
            temporaryData.put(key, value);
        }

        public String getTemporaryData(String key) {
            return temporaryData.get(key);
        }
    }
}