package com.socompany.springschedulerbot.service;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.useceses.handlers.CommandHandler;
import com.socompany.springschedulerbot.useceses.listeners.MessageAbstractListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.BACK;
import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.START;

@Component
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {

    private final String botName;
    private final CommandHandler commandHandler;
    private final List<MessageAbstractListener> messageAbstractListeners;

    public TelegramBotService(Environment env,
                              @Lazy CommandHandler commandHandler,
                              @Lazy List<MessageAbstractListener> messageAbstractListeners) {
        super(env.getProperty("bot.token"));

        this.botName = env.getProperty("bot.name");
        this.commandHandler = commandHandler;
        this.messageAbstractListeners = messageAbstractListeners;

        List<BotCommand> commandList = List.of(
                new BotCommand(START.getCommand(), "Отримайте стартове меню бота! 🤖"));

        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e)  {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(processMessageListeners(update)) {
            return;
        }

        if(update.hasMessage() && update.getMessage().hasText()) {
            log.info("Received update with text: " + update.getMessage().getText());
            Message message = update.getMessage();
            String messageText = message.getText();
            commandHandler.handleCommand(messageText, getCommonInfo(message));
        }
        else if(update.hasCallbackQuery()) {
            log.info("Received update with CallbackQuery from: {}",  update.getCallbackQuery().getFrom().getFirstName());
            CallbackQuery callbackQuery = update.getCallbackQuery();
            long chatId = callbackQuery.getMessage().getChatId();
            int messageId = callbackQuery.getMessage().getMessageId();
            String data =  callbackQuery.getData();

            CommonInfo commonInfo = getCommonInfo(messageId, chatId);
            commandHandler.handleCommand(data, commonInfo);
        }
    }

    private boolean processMessageListeners(Update update) {
        for(MessageAbstractListener listener : messageAbstractListeners) {
            if(listener.process(update)) {
                return true;
            }
        }
        return false;
    }

    public void editMessage(Long chatId, int messageId, String text, List<ButtonData> buttons, int buttonsPerRow, boolean showBackButton) {

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.enableMarkdown(true);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);

        InlineKeyboardMarkup inlineKeyboardMarkup = buildKeyboard(buttons, buttonsPerRow, showBackButton);
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.warn("Edit failed, maybe message was deleted.");
            try {
                createMessage(chatId, text, buttons, buttonsPerRow);
            } catch (TelegramApiException ex) {
                log.error("Create  message failed, error message: {}", ex.getMessage(), ex);
            }
        }
    }

    public void createMessage(Long chatId, String text, List<ButtonData> buttons, int buttonsPerRow) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        InlineKeyboardMarkup inlineKeyboardMarkup = buildKeyboard(buttons, buttonsPerRow, false);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error with SendMessage", e);
        }

    }


    private InlineKeyboardMarkup buildKeyboard(List<ButtonData> buttons, int buttonsPerRow, boolean addBackButton) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if(buttonsPerRow <= 0) {
            throw new RuntimeException("Buttons per row should be greater than 0");
        }

        for (int i = 0; i < buttons.size(); i += buttonsPerRow) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < i + buttonsPerRow && j < buttons.size(); j++) {
                ButtonData btn = buttons.get(j);
                InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
                keyboardButton.setText(btn.text());
                keyboardButton.setCallbackData(btn.callbackData());
                row.add(keyboardButton);
            }
            rows.add(row);
        }

        // Add Back Button if needed (addBackButton = true)
        if (addBackButton) {
            InlineKeyboardButton backButton = new InlineKeyboardButton();
            backButton.setText("\uD83D\uDD19 Назад");
            backButton.setCallbackData(BACK.getCommand());

            List<InlineKeyboardButton> backRow = new ArrayList<>();
            backRow.add(backButton);
            rows.add(backRow);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }


    private CommonInfo getCommonInfo(Message message) {
        CommonInfo ci = new CommonInfo();
        ci.setChatId(message.getChatId());
        ci.setMessageId(message.getMessageId());
        return ci;
    }

    private  CommonInfo getCommonInfo(int messageId, long chatId) {
        CommonInfo ci = new CommonInfo();
        ci.setMessageId(messageId);
        ci.setChatId(chatId);
        return ci;
    }

}