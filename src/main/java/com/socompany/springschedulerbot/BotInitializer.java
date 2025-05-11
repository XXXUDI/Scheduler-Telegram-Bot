package com.socompany.springschedulerbot;

import com.socompany.springschedulerbot.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
@RequiredArgsConstructor
public class BotInitializer {
    private final TelegramBotService bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        log.info("Initializing Bot: {}", bot.getBotUsername());
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
    }
}
