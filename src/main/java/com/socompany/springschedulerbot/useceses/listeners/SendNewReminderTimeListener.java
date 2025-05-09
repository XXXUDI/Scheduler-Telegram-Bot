package com.socompany.springschedulerbot.useceses.listeners;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.service.UserService;
import com.socompany.springschedulerbot.useceses.commands.StartMenuCommand;
import com.socompany.springschedulerbot.useceses.util.SessionManager;
import com.socompany.springschedulerbot.useceses.util.StateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.START;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendNewReminderTimeListener extends MessageAbstractListener {

    private final UserService userService;
    private final TelegramBotService telegramBotService;
    private final SessionManager sessionManager;

    @Override
    public boolean process(Update update) {
        // Проверяем, является ли это сообщением от пользователя
        if (update.hasMessage() && update.getMessage().getText() != null) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            // Проверяем, ожидаем ли мы от пользователя ввода времени
            if (sessionManager.isAwaitingInput(chatId, "CHANGE_DATE")) {
                log.info("Processing user input for '/changeDate': {}", text);

                // Проверяем формат времени HH:mm
                try {
                    // Валидируем формат времени
                    LocalTime time = LocalTime.parse(text, DateTimeFormatter.ofPattern("HH:mm"));

                    userService.updateReminderTime(chatId, time);
                    log.info("Updated daily reminder time for user {}: {}", chatId, time);

                    try {
                        String confirmationText = "Час успішно встановлено 🕑: " + time.toString();

                        List<ButtonData> btns = List.of(new ButtonData("\uD83D\uDD19 Повернутись у головне меню", START.getCommand()));

                        telegramBotService.createMessage(chatId, confirmationText, btns, 1);
                    } catch (Exception e) {
                        log.error("Failed to send confirmation to user {}", chatId, e);
                    }
                    // Обновление состояния заканчивается
                    sessionManager.removeSession(chatId);
                    return true;
                } catch (DateTimeParseException e) {
                    log.warn("Time format is invalid for user {}: {}", chatId, text);

                    // Сообщаем об ошибке формата
                    try {
                        telegramBotService.createMessage(chatId,
                                "⌛ Введи час у правильному форматі, будь ласка: **HH:mm**, наприклад, 09:00.",
                                List.of(), 1);
                    } catch (Exception ex) {
                        log.error("Error sending invalid format message to user {}", chatId, ex);
                    }
                }
            }
        }
        return false;
    }
}