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

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.CHANGE_TIMEZONE;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeTimeZoneCommand implements Command {

    private final SessionManager sessionManager;
    private final TelegramBotService telegramBotService;

    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Handling change time zone command for user {}", commonInfo.getChatId());
        sessionManager.setSession(commonInfo.getChatId(), CHANGE_TIMEZONE.getCommand());

        createAndSendMessage(commonInfo);
    }

    private void createAndSendMessage(CommonInfo commonInfo) {
        String text = "\uD83C\uDF0D Щоб я міг правильно показувати погоду та надсилати повідомлення у твоєму часовому поясі, обери країну.\n" +
                "\n" +
                "Введи код країни зі списку нижче (наприклад: `UA`):\n" +
                "\n" +
                "\uD83C\uDF10 Доступні коди країн:\n" +
                "\uD83C\uDDFA\uD83C\uDDF8 US  | \uD83C\uDDE8\uD83C\uDDE6 CA  | \uD83C\uDDEC\uD83C\uDDE7 GB  | \uD83C\uDDE9\uD83C\uDDEA DE  | \uD83C\uDDEB\uD83C\uDDF7 FR  \n" +
                "\uD83C\uDDEE\uD83C\uDDF9 IT  | \uD83C\uDDEA\uD83C\uDDF8 ES  | \uD83C\uDDF5\uD83C\uDDF1 PL  | \uD83C\uDDFA\uD83C\uDDE6 UA  | \uD83C\uDDF7\uD83C\uDDFA RU  \n" +
                "\uD83C\uDDEE\uD83C\uDDF3 IN  | \uD83C\uDDE8\uD83C\uDDF3 CN  | \uD83C\uDDEF\uD83C\uDDF5 JP  | \uD83C\uDDF0\uD83C\uDDF7 KR  | \uD83C\uDDE7\uD83C\uDDF7 BR  \n" +
                "\uD83C\uDDE6\uD83C\uDDFA AU  | \uD83C\uDDFF\uD83C\uDDE6 ZA  | \uD83C\uDDF3\uD83C\uDDEC NG  | \uD83C\uDDF9\uD83C\uDDF7 TR  | \uD83C\uDDF2\uD83C\uDDFD MX  \n" +
                "\n" +
                "❗\uFE0FБудь уважним: саме цей вибір визначає час щоденного повідомлення.\n";

        try {
            log.info("Sending message to user {}", commonInfo.getChatId());
            telegramBotService.createMessage(commonInfo.getChatId(), text, List.of(), 1);
        } catch (TelegramApiException e) {
            log.error("Error sending message to user {}", commonInfo.getChatId(), e);
            e.printStackTrace();
        }
    }
}
