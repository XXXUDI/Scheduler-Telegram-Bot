package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import com.socompany.springschedulerbot.useceses.util.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.CHANGE_DATE;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeDateCommand implements Command {

    private final SessionManager sessionManager;
    private final TelegramBotService telegramBotService;

    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Handling change date command for user {}", commonInfo.getChatId());

        sessionManager.setSession(commonInfo.getChatId(), CHANGE_DATE.getCommand());

        createAndSendMessage(commonInfo);

    }

    private void createAndSendMessage(CommonInfo commonInfo) {
        String text = "Буль ласка, введи час 🕑," +
                " коли я буду тобі надсилати щоденні звіти🤗\n\n" +
                "Вводь у форматі: **00:00**";

        try {
            log.info("Sending message to user {}", commonInfo.getChatId());
            telegramBotService.createMessage(commonInfo.getChatId(), text, List.of(/*0 elem.*/), 1);
        } catch (TelegramApiException e) {
            log.error("Error sending message to user {}", commonInfo.getChatId(), e);
            e.printStackTrace();
        }

    }


}
