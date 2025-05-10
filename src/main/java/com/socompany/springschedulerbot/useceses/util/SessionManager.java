package com.socompany.springschedulerbot.useceses.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SessionManager {

    private final Map<Long, UserSession> userSession = new ConcurrentHashMap<>();

    /**
     * Устанавливает сессию с текущим состоянием для пользователя
     */
    public void setSession(Long chatId, String state) {
        userSession.put(chatId, new UserSession(state));
    }

    /**
     * Удаляет сессию пользователя
     */
    public void clearSession(Long chatId) {
        userSession.remove(chatId);
    }

    /**
     * Проверяет, ожидает ли пользователь определенный ввод
     */
    public boolean isAwaitingInput(Long chatId, String state) {
        UserSession session = userSession.get(chatId);
        return session != null && state.equals(session.getState());
    }

    /**
     * Устанавливает временные данные для пользователя
     */
    public void setTemporaryData(Long chatId, String key, String value) {
        UserSession session = userSession.computeIfAbsent(chatId, k -> new UserSession(null));
        session.setTemporaryData(key, value);
    }

    /**
     * Получает временные данные пользователя
     */
    public String getTemporaryData(Long chatId, String key) {
        UserSession session = userSession.get(chatId);
        return session != null ? session.getTemporaryData(key) : null;
    }

    /**
     * Устанавливает новый статус (состояние) для сессии пользователя
     */
    public void setStatus(Long chatId, String status) {
        UserSession session = userSession.computeIfAbsent(chatId, k -> new UserSession(null));
        session.setState(status);
    }

    /**
     * Возвращает текущий статус (состояние) пользователя
     */
    public String getStatus(Long chatId) {
        UserSession session = userSession.get(chatId);
        return session != null ? session.getState() : null;
    }

    /**
     * Внутренний класс для хранения состояния и временных данных пользователя
     */
    private static class UserSession {
        private String state; // Текущее состояние (например, AWAITING_TASK_TITLE)
        private final Map<String, String> temporaryData = new ConcurrentHashMap<>();

        public UserSession(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
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