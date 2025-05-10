package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.ADD_TASK;
import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.DELETE_TASK;

@Component
@Slf4j
@RequiredArgsConstructor
public class ToDoMenuCommand implements Command {

    private final TelegramBotService telegramBotService;


    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Handling TodoMenuCommand from user: {}", commonInfo.getChatId());
        sendToDoMenu(commonInfo);
    }

    private void sendToDoMenu(CommonInfo commonInfo) {
        String answer = "\uD83D\uDCDD Твій список справ:\n" +
                "\n" +
                "1. \uD83D\uDCCC Зателефонувати лікарю — 14:00\n" +
                "2. \uD83D\uDED2 Купити продукти — 17:30\n" +
                "3. \uD83D\uDCDA Підготуватись до екзамену — 20:00\n" +
                "\n" +
                "Обери дію нижче ⬇\uFE0F";

        List<ButtonData> buttonData = List.of(
                new ButtonData("➕ Додати нову задачу", ADD_TASK.getCommand()),
                new ButtonData("\uD83D\uDDD1 Видалити задачу", DELETE_TASK.getCommand())
        );

        telegramBotService.editMessage(commonInfo.getChatId(), commonInfo.getMessageId(), answer, buttonData, 2, true);

    }


}
