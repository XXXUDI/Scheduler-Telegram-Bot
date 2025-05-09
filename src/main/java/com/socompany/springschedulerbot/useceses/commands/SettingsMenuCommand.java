package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.persistant.dto.UserResponseDto;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.service.UserService;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.CHANGE_TIMEZONE;
import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.UNSUPPORTED_COMMAND;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettingsMenuCommand implements Command {

    private final UserService userService;
    private final TelegramBotService telegramBotService;

    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Handling settings command for user {}", commonInfo.getChatId());

        long chatId = commonInfo.getChatId();

        userService.findByChatId(chatId).ifPresentOrElse(
                user -> sendSettingsMenu(commonInfo, user),
                () -> {
                    userService.registerUser(commonInfo);
                    execute(commonInfo);
                });
    }

    private void sendSettingsMenu(CommonInfo commonInfo, UserResponseDto userResponseDto) {

        StringBuilder sb = new StringBuilder("⚙\uFE0F Вітаємо в меню налаштувань! \n\n Ось твої налаштування: \n\n");

        sb.append("Часовий пояс / Країна: " + userResponseDto.getCountryCode() + "\n");
        sb.append("Валюта для курсів: USD \n\n");

        sb.append("*Нижче можеш вибрати, що хочеш змінити:*");

        List<ButtonData> buttons = List.of(
                new ButtonData("\uD83C\uDF0D Часовий пояс", CHANGE_TIMEZONE.getCommand()),
                new ButtonData("\uD83E\uDE99 Валюта для курсів", UNSUPPORTED_COMMAND.getCommand()));

        telegramBotService.editMessage(commonInfo.getChatId(), commonInfo.getMessageId(), sb.toString(), buttons, 2, true);

    }
}
