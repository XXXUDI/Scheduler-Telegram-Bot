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
import java.util.Map;


import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class SchedulerMenuCommand implements Command {

    private final TelegramBotService telegramBotService;
    private final UserService userService;


    private static final Map<Boolean, String> EMOJIS = Map.of(
            true, "✅",
            false, "❌"
    );

    @Override
    public void execute(CommonInfo commonInfo) {
        sendSchedulerMenuMessage(commonInfo);
    }

    private void sendSchedulerMenuMessage(CommonInfo commonInfo) {
        String text = getMenuText();

        // Завжди беремо актуальні дані користувача
        userService.findByChatId(commonInfo.getChatId()).ifPresentOrElse(
                user -> {
                    List<ButtonData> buttonDataList = buildButtons(user);
                    telegramBotService.editMessage(
                            commonInfo.getChatId(),
                            commonInfo.getMessageId(),
                            text,
                            buttonDataList,
                            2,
                            true
                    );
                },
                () -> {
                    userService.registerUser(commonInfo);
                    execute(commonInfo);
                });
    }



    private String getMenuText() {
        return """
                Налаштуй щоденні повідомлення: 

                Вибери лише те, що хочеш получати:
                """;
    }

    private List<ButtonData> buildButtons(UserResponseDto user) {
        log.info("Building buttons for user with chatId {}: Weather={}, Events={}, Bitcoin={}, Currency={}",
                user.getChatId(),
                user.isWeatherReminderEnabled(),
                user.isEventsReminderEnabled(),
                user.isBitcoinPriceReminderEnabled(),
                user.isCurrencyPriceReminderEnabled()
        );

        return List.of(
                new ButtonData("☀️ Погода " + EMOJIS.get(user.isWeatherReminderEnabled()), WEATHER.getCommand()),
                new ButtonData("🎉 Події " + EMOJIS.get(user.isEventsReminderEnabled()), EVENTS.getCommand()),
                new ButtonData("💰 Курс Bitcoin" + EMOJIS.get(user.isBitcoinPriceReminderEnabled()), BITCOIN.getCommand()),
                new ButtonData("📈 Курси валют " + EMOJIS.get(user.isCurrencyPriceReminderEnabled()), CURRENCY.getCommand()),
                new ButtonData("⏰ Час відправки: " + (user.getDailyReminderTime() != null ? user.getDailyReminderTime() : "09:00") + " 🕘 (Змінити)", CHANGE_DATE.getCommand())
        );
    }
}

