package com.socompany.springschedulerbot.useceses.listeners;

import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.persistant.enums.CountryCode;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.service.UserService;
import com.socompany.springschedulerbot.useceses.util.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.CHANGE_TIMEZONE;
import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.START;


@Component
@Slf4j
@RequiredArgsConstructor
public class SendNewTimeZoneListener extends MessageAbstractListener {

    private final SessionManager sessionManager;
    private final TelegramBotService telegramBotService;
    private final UserService userService;


    @Override
    public boolean process(Update update) {
        if (update.hasMessage() && update.getMessage().getText() != null) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (sessionManager.isAwaitingInput(chatId, CHANGE_TIMEZONE.getCommand())) {
                log.info("Processing user input for '/changeTimezone': {}", text);

                try {
                    CountryCode countryCode = CountryCode.getCountryCode(text);
                    userService.updateCountryCode(chatId, countryCode);
                    log.info("Updated country code for user {}: {}", chatId, countryCode);

                    try {
                        // TODO: add country emoji after time zone info
                        String confirmationText = "✅ Часовий пояс встановлено на: "+ countryCode.getTimeZone() + "\n" +
                                "Твої щоденні нагадування тепер будуть приходити за місцевим часом.\n";

                        List<ButtonData> btns = List.of(new ButtonData("\uD83D\uDD19 Повернутись у головне меню", START.getCommand()));

                        telegramBotService.createMessage(chatId, confirmationText, btns, 1);
                    } catch (TelegramApiException e) {
                        log.error("Error sending confirmation to user {}", chatId, e);
                    }
                    sessionManager.clearSession(chatId);
                    return true;

                } catch (IllegalArgumentException e) {
                    log.warn("Country code is invalid for user {}: {}", chatId, text);

                    try {
                        log.info("Sending invalid format message to user {}", chatId);
                        telegramBotService.createMessage(chatId,
                                "*Будь ласка, введи код твоєї країни у правильному форматі.*",
                                List.of(), 1);

                    } catch (TelegramApiException ex) {
                        log.error("Error sending invalid format message to user {}", chatId, ex);
                    }
                }
            }
        }
        return false;
    }
}
