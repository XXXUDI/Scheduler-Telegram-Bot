package com.socompany.springschedulerbot.useceses.commands;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.ButtonData;
import com.socompany.springschedulerbot.persistant.dto.TaskResponseDto;
import com.socompany.springschedulerbot.service.TaskService;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.service.UserService;
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
    private final TaskService taskService;
    private final UserService userService;


    @Override
    public void execute(CommonInfo commonInfo) {
        log.info("Handling TodoMenuCommand from user: {}", commonInfo.getChatId());

        userService.findByChatId(commonInfo.getChatId()).ifPresentOrElse(
                user -> sendToDoMenu(commonInfo),
                () -> {
                    userService.registerUser(commonInfo);
                    execute(commonInfo);
                });
    }

    private void sendToDoMenu(CommonInfo commonInfo) {

        StringBuilder stringBuilder = new StringBuilder("\uD83D\uDCDD Вітаємо у ToDo списку! Ось твій список задач на сьогодні:\n\n");

        var tasks = taskService.findAllTasksByUserChatId(commonInfo.getChatId());

        if(tasks.isEmpty()){
            stringBuilder.append("\n\uD83D\uDCDD У тебе поки немає жодної задачі.\n" +
                    "\n" +
                    "Натисни кнопку нижче, щоб додати першу! ⬇\uFE0F");
        } else {
            for (TaskResponseDto task : tasks) {
                stringBuilder.append("- *").append(task.getTaskName()).append("* - *").append(task.getReminderTime().toString()).append("*\n");
            }
            stringBuilder.append("\nОбери дію нижче ⬇\uFE0F");
        }

        List<ButtonData> buttonData = List.of(
                new ButtonData("➕ Додати нову задачу", ADD_TASK.getCommand()),
                new ButtonData("\uD83D\uDDD1 Видалити задачу", DELETE_TASK.getCommand())
        );

        telegramBotService.editMessage(commonInfo.getChatId(), commonInfo.getMessageId(), stringBuilder.toString(), buttonData, 2, true);

    }


}
