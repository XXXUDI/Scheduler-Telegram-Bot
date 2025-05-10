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

import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.*;


@Component
@Slf4j
@RequiredArgsConstructor
public class StartMenuCommand implements Command {

    private final TelegramBotService telegramBotService;
    @Value("${bot.name}")
    private String botName;


    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Handling start command for user {}", commonInfo.getChatId());

        sendStartMenu(commonInfo);
    }

    private void sendStartMenu(CommonInfo commonInfo) {
        String answer = "Вітаю в " + botName + "! \uD83C\uDF31\n\n" +
                "*Ось з чим я можу тобі допомогти:*\n\n" +
                "🔔 Відправляти тобі повідомлення кожного дня в заданий час (погода, події, біткоін та інше)\n" +
                "📝 Допоможу не забути про важливі речі через ToDo список.\n\n" +
                "Хочеш налаштувати?";

        List<ButtonData> buttonDataList = List.of(
                new ButtonData("🕑 Щоденні нагадування", SCHEDULER.getCommand()),
                new ButtonData("📝 ToDo список ", TODO_MENU.getCommand()),
                new ButtonData("⚙️ Налаштування", SETTINGS.getCommand()),
                new ButtonData("❓ Допомога", ABOUT.getCommand())
        );

        telegramBotService.editMessage(commonInfo.getChatId(), commonInfo.getMessageId(), answer, buttonDataList, 2, false);
    }
}