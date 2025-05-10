package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class AboutMenuCommand implements Command {

    private final TelegramBotService telegramBotService;


    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Handling about menu command for user {}", commonInfo.getChatId());
        createAndSendMessage(commonInfo);
    }

    private void createAndSendMessage(CommonInfo commonInfo) {
        String text = "ℹ\uFE0F *SpringSchedulerBot* — це бот, який допоможе тобі:\n\n" +
                "- получати корисну інформацию в потрібний час\n" +
                "- нагадувати про важливі задачі.\n" +
                "\n" +
                "Команди:\n" +
                "- /start — головне меню\n" +
                "- /todo — список задач\n" +
                "\n" +
                "Маєш питання або пропозиції? Пиши розробнику: @FXAlex46";

        List<ButtonData> buttonDataList = List.of(
                new ButtonData("🕑 Щоденні нагадування", SCHEDULER.getCommand()),
                new ButtonData("📝 ToDo список ", TODO_MENU.getCommand())
        );

        telegramBotService.editMessage(commonInfo.getChatId(), commonInfo.getMessageId(), text, buttonDataList, 2, true);
    }
}
