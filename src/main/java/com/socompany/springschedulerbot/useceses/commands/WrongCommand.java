package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.START;

@Component
@Slf4j
@RequiredArgsConstructor
public class WrongCommand implements Command {

    private final TelegramBotService telegramBotService;

    @Override
    public void execute(CommonInfo commonInfo) {
        sendAndCreateMessage(commonInfo);
    }

    private void sendAndCreateMessage(CommonInfo commonInfo) {
        String text = "\uD83E\uDD16 Вибач, я не розпізнав цю команду.\n" +
                "\n" +
                "Спробуй скористатися меню або натисни /start, щоб повернутися на головний екран.\n";

        var buttonDataList = List.of(
                new ButtonData("\uD83D\uDCCB /start", START.getCommand())
        );

        telegramBotService.editMessage(commonInfo.getChatId(), commonInfo.getMessageId(), text, buttonDataList, 1, false);
    }
}
