package com.socompany.springschedulerbot.useceses.listeners;

import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.service.TaskService;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.service.UserService;
import com.socompany.springschedulerbot.useceses.util.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.ADD_TASK;
import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.START;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class AddTaskListener extends MessageAbstractListener {

    private final SessionManager sessionManager;
    private final TelegramBotService telegramBotService;
    private final TaskService taskService;
    ;
    private static final String AWAITING_TASK_TIME = "AWAITING_TASK_TIME";

    @Override
    public boolean process(Update update) {
        if (update.hasMessage() && update.getMessage().getText() != null) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (sessionManager.isAwaitingInput(chatId, ADD_TASK.getCommand())) {
                sessionManager.setTemporaryData(chatId, "taskTitle", text);
                sessionManager.setStatus(chatId, AWAITING_TASK_TIME);

                try {
                    telegramBotService.createMessage(chatId, "⌚ Тепер введи час для задачі у форматі hh:mm (наприклад, 21:25)", List.of(), 1);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }

            if (sessionManager.isAwaitingInput(chatId, AWAITING_TASK_TIME)) {
                try {
                    LocalTime taskTime = parseTime(text);
                    String taskTitle = sessionManager.getTemporaryData(chatId, "taskTitle");

                    taskService.createTask(chatId, taskTitle, taskTime);

                    List<ButtonData> btns = List.of(new ButtonData("\uD83D\uDD19 Повернутись у головне меню", START.getCommand()));

                    telegramBotService.createMessage(chatId, "✅ Задача '" + taskTitle + "' створена на " + taskTime + ".", btns, 1);
                    sessionManager.clearSession(chatId);
                } catch (DateTimeParseException e) {
                    try {
                        telegramBotService.createMessage(chatId, "❌ Неправильний формат часу. Спробуй ще раз: hh:mm", List.of(), 1);
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        }
        return false;
    }

    private LocalTime parseTime(String timeText) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeText, formatter);
    }
}