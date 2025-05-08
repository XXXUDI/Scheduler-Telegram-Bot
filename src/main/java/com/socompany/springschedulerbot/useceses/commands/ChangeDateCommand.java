package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.service.UserService;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeDateCommand implements Command {

    private final UserService userService;
    private final TelegramBotService telegramBotService;

    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Handling change date command for user {}", commonInfo.getChatId());

        SendMessage msg = createAndSendMessage(commonInfo);
    }

    private SendMessage createAndSendMessage(CommonInfo commonInfo) {
        String text = "Буль ласка, введи час 🕑," +
                " коли я буду тобі надсилати щоденні звіти🤗\n\n" +
                "Вводь у форматі: **00:00**";

        SendMessage msg = new SendMessage();
        msg.setChatId(commonInfo.getChatId());
        msg.setText(text);
        msg.enableMarkdownV2(true);

        try {
            log.info("Sending message to user {}", commonInfo.getChatId());
            telegramBotService.createMessage(commonInfo.getChatId(), text, null, 1);
        } catch (TelegramApiException e) {
            log.error("Error sending message to user {}", commonInfo.getChatId(), e);
            e.printStackTrace();
        }

        return msg;
    }


}
