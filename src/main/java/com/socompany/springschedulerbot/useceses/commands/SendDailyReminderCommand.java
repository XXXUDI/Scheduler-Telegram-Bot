package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.START;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendDailyReminderCommand implements Command {

    private final TelegramBotService telegramBotService;

    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Handling send daily reminder command for user {}", commonInfo.getChatId());
        sendDailyReminderMessage(commonInfo);
    }

    private void sendDailyReminderMessage(CommonInfo commonInfo) {
        String text = "Доброе утро! \uD83C\uDF1E Вот твоя подборка на сегодня:\n" +
                "\n" +
                "\uD83D\uDCCD Погода в [Город]: +18°C, ясно ☀\uFE0F  \n" +
                "\uD83C\uDF89 Праздник: Всемирный день кофе ☕  \n" +
                "\uD83D\uDCB0 Биткоин: $62,540  \n" +
                "\uD83D\uDCB1 USD → EUR: 0.92\n" +
                "\n" +
                "Хорошего дня! \uD83D\uDE0A\n";

        List<ButtonData> buttons = List.of(new ButtonData("📦 Головне меню", START.getCommand()));
        try {
            telegramBotService.createMessage(commonInfo.getChatId(), text, buttons, 1);
        } catch (TelegramApiException e) {
            log.error("Error sending message to user {}", commonInfo.getChatId(), e);
            e.printStackTrace();
        }
    }
}
