package com.socompany.springschedulerbot.useceses.listeners;

import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.service.TaskService;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.useceses.util.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteTaskListener extends MessageAbstractListener {

    private final SessionManager sessionManager;
    private final TelegramBotService telegramBotService;
    private final TaskService taskService;

    @Override
    public boolean process(Update update) {
        if(update.hasCallbackQuery() && update.getCallbackQuery().getData() != null) {
            String text = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            if(sessionManager.isAwaitingInput(chatId, DELETE_TASK.getCommand())) {
                log.info("Processing user input for '/deleteTask': {}", text);
                if(text.equals(CANCEL_DELETING_TASK.getCommand())) {
                    sessionManager.clearSession(chatId);

                    var btns = List.of(new ButtonData("\uD83D\uDD19 Головне меню", START.getCommand()));
                    sessionManager.clearSession(chatId);
                    try {
                        telegramBotService.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                        telegramBotService.createMessage(chatId, "✅ Скасовано. Повернітся до головного меню.", btns, 1);
                    } catch (TelegramApiException e) {
                        log.error("Error sending confirmation to user {}", chatId, e);
                        return true;
                    }

                    return true;
                }
                Integer taskId = Integer.parseInt(text);
                log.info("Deleting task with id: {}", taskId);
                if(taskService.deleteTaskById(taskId)) {
                    log.info("Task with id: {} successfully deleted", taskId);
                    var btns = List.of(new ButtonData("\uD83D\uDD19 Головне меню", START.getCommand()));
                    sessionManager.clearSession(chatId);
                    try {
                        telegramBotService.createMessage(chatId, "✅ Задачу видалено", btns, 1);
                    } catch (TelegramApiException e) {
                        log.error("Error sending confirmation to user {}", chatId, e);
                        return true;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
