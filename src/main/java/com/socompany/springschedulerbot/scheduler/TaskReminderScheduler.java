package com.socompany.springschedulerbot.scheduler;

import com.socompany.springschedulerbot.persistant.dto.TaskResponseDto;
import com.socompany.springschedulerbot.service.TaskService;
import com.socompany.springschedulerbot.service.TelegramBotService;
import com.socompany.springschedulerbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskReminderScheduler {

    private final TelegramBotService telegramBotService;

    private final TaskService taskService;


    @Scheduled(cron = "0 * * * * *")
    private void sendTaskReminderMessage() {
        log.info("Task Reminder Scheduler triggered...");

        List<TaskResponseDto> tasks = taskService.findTasksForCurrentTime();

        for (TaskResponseDto task : tasks) {
            Long chatId = task.getUserResponseDto().getChatId();
            String message = "⏰ Не забудьте виконати задачу:  *" + task.getTaskName() + "*";
            try {
                telegramBotService.createMessage(chatId, message, List.of(), 1);
                taskService.deleteTaskById(task.getTaskId());
            } catch (TelegramApiException e) {
                log.error("Error sending message to user {}", chatId, e);
            }
        }
    }
}
