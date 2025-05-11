package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.service.TaskService;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.useceses.commands.interfaces.Command;
import com.socompany.springschedulerbot.useceses.util.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.CANCEL_DELETING_TASK;
import static com.socompany.springschedulerbot.useceses.commands.enums.CommandType.DELETE_TASK;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteTaskCommand implements Command {

    private final TelegramBotService telegramBotService;
    private final TaskService taskService;
    private final SessionManager sessionManager;

    @Override
    public void execute(CommonInfo commonInfo) {
       log.info("Handling delete task command for user {}", commonInfo.getChatId());
       sessionManager.setSession(commonInfo.getChatId(), DELETE_TASK.getCommand());

       createAndSendMessage(commonInfo);
    }

    private void createAndSendMessage(CommonInfo commonInfo) {
        StringBuilder text = new StringBuilder("\uD83D\uDDD1 Вибери задачу, яку хочеш *видалити*, або натистни '*Скасувати*'.\n\n");

        var tasks = taskService.findAllTasksByUserChatId(commonInfo.getChatId());

        List<ButtonData> buttons = new ArrayList<>();

        if(tasks.isEmpty()) {
            text.append("*P.S. схоже ваш список задач пустий.*");
        } else {
            for (var task : tasks) {
                buttons.add(new ButtonData(task.getTaskName(), String.valueOf(task.getTaskId())));
            }
        }

        buttons.add(new ButtonData("❌Скасувати", CANCEL_DELETING_TASK.getCommand()));

        try {
            log.info("Sending message to user {}", commonInfo.getChatId());
            telegramBotService.createMessage(commonInfo.getChatId(), text.toString(), buttons, 2);
        } catch (TelegramApiException e) {
            log.error("Error sending message to user {}", commonInfo.getChatId(), e);
            e.printStackTrace();
        }


    }
}
