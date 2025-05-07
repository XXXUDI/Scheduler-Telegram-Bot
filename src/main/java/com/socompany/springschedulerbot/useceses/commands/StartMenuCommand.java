package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.persistant.dto.UserRequestDto;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.service.UserService;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class StartMenuCommand implements Command {

    private final TelegramBotService telegramBotService;
    @Value("${bot.name}")
    private String botName;

    private final UserService userService;


    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Received start menu command");

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setChatId(commonInfo.getChatId());

        userService.save(userRequestDto);

        sendStartMenu(commonInfo);
    }

    private void sendStartMenu(CommonInfo commonInfo) {
        String answer = "Вітаю в " + botName + "! \uD83C\uDF31\n\n" +
                "*Що я можу?*\n" +
                "🔔 Відправляти тобі повідомлення кожного дня в заданий час (погода, події, біткоін та інше)\n" +
                "📝 Допоможу не забути про важливі речі через ToDo список.\n\n" +
                "Хочеш налаштувати?";

        List<ButtonData> buttonDataList = List.of(
                new ButtonData("🕑 Налаштувати щоденні нагадування", "/scheduler"),
                new ButtonData("📝 Керувати ToDo ", "/trends"),
                new ButtonData("⚙️ Налаштування", "/settings"),
                new ButtonData("❓ Допомога", "/help")
        );

        try {
            if (commonInfo.getMessageId() != 0) {
                telegramBotService.editMessage(
                        commonInfo.getChatId(),
                        commonInfo.getMessageId(),
                        answer,
                        buttonDataList,
                        2,
                        false
                );
            } else {
                telegramBotService.createMessage(
                        commonInfo.getChatId(),
                        answer,
                        buttonDataList,
                        2
                );
            }
        } catch (TelegramApiException e) {
            log.error("Telegram API Exception", e);
        }
    }
}
