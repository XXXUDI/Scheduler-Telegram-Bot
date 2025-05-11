package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.persistant.enums.CountryCode;
import com.socompany.springschedulerbot.service.CryptoService;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.service.UserService;
import com.socompany.springschedulerbot.service.WeatherService;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.START;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendDailyReminderCommand implements Command {

    private final TelegramBotService telegramBotService;
    private final UserService userService;
    private final CryptoService cryptoService;
    private final WeatherService weatherService;

    public static final Map<CountryCode, String> COUNTRY_CAPITALS = Map.ofEntries(
            Map.entry(CountryCode.US, "Washington"),
            Map.entry(CountryCode.CA, "Ottawa"),
            Map.entry(CountryCode.GB, "London"),
            Map.entry(CountryCode.DE, "Berlin"),
            Map.entry(CountryCode.FR, "Paris"),
            Map.entry(CountryCode.IT, "Rome"),
            Map.entry(CountryCode.ES, "Madrid"),
            Map.entry(CountryCode.PL, "Warsaw"),
            Map.entry(CountryCode.RU, "Moscow"),
            Map.entry(CountryCode.UA, "Kyiv"),
            Map.entry(CountryCode.IN, "New Delhi"),
            Map.entry(CountryCode.CN, "Beijing"),
            Map.entry(CountryCode.JP, "Tokyo"),
            Map.entry(CountryCode.KR, "Seoul"),
            Map.entry(CountryCode.BR, "Brasília"),
            Map.entry(CountryCode.AU, "Canberra"),
            Map.entry(CountryCode.ZA, "Pretoria"), // ???
            Map.entry(CountryCode.NG, "Abuja"),
            Map.entry(CountryCode.TR, "Ankara"),
            Map.entry(CountryCode.MX, "Mexico City")
    );

    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Handling send daily reminder command for user {}", commonInfo.getChatId());
        sendDailyReminderMessage(commonInfo);
    }

    private void sendDailyReminderMessage(CommonInfo commonInfo) {


        userService.findByChatId(commonInfo.getChatId()).ifPresentOrElse(user -> {

            String bitcoinPrice = String.format("%.1f", cryptoService.getBitcoinPrice());
            String text = "Доброго ранку! \uD83C\uDF1E Ось твоя підбірка на сьогодні:\n" +
                    "\n" +
                    "\uD83D\uDCCD Погода в " + COUNTRY_CAPITALS.get(user.getCountryCode()) +
                    ": "+ weatherService.getTemperatureByCity(user.getCountryCode()) +
                    "°C ☀\uFE0F  \n" +
                    "\uD83C\uDF89 Св'ято: Наразі не підтримується... ☕  \n" +
                    "\uD83D\uDCB0 Bitcoin: *" + bitcoinPrice +"$* \n" +
                    "\uD83D\uDCB1 USD → EUR: 0.92\n" +
                    "\n" +
                    "Гарного дня! \uD83D\uDE0A\n";

            List<ButtonData> buttons = List.of(new ButtonData("📦 Головне меню", START.getCommand()));
            try {
                telegramBotService.createMessage(commonInfo.getChatId(), text, buttons, 1);
            } catch (TelegramApiException e) {
                log.error("Error sending message to user {}", commonInfo.getChatId(), e);
                e.printStackTrace();
            }
        }, () -> log.error("Error sending message to user {}", commonInfo.getChatId()));


    }
}
