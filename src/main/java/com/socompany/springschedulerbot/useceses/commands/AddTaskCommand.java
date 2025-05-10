package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import com.socompany.springschedulerbot.useceses.util.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.ADD_TASK;

@Component
@Slf4j
@RequiredArgsConstructor
public class AddTaskCommand implements Command {

    private final SessionManager sessionManager;
    private final TelegramBotService telegramBotService;

    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Handling add task command for user {}", commonInfo.getChatId());
        sessionManager.setSession(commonInfo.getChatId(), ADD_TASK.getCommand());

        createAndSendMessage(commonInfo);
    }

    private void createAndSendMessage(CommonInfo c) {
        String text = "\uD83C\uDD95 Введи назву нової задачі.\n" +
                "\n" +
                "Приклад: Сходити в магазин";

        try {
            log.info("Sending message to user {}", c.getChatId());
            telegramBotService.createMessage(c.getChatId(),text, List.of(), 1);
        } catch (TelegramApiException e) {
            log.error("Error sending message to user {}", c.getChatId(), e);
            e.printStackTrace();
        }
    }
}
