package com.socompany.springschedulerbot.service;

import com.socompany.springschedulerbot.mapper.TaskRequestMapper;
import com.socompany.springschedulerbot.mapper.TaskResponseMapper;
import com.socompany.springschedulerbot.persistant.dto.TaskResponseDto;
import com.socompany.springschedulerbot.persistant.entity.Task;
import com.socompany.springschedulerbot.persistant.entity.User;
import com.socompany.springschedulerbot.repository.TaskRepository;
import com.socompany.springschedulerbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskResponseMapper responseMapper;
    private final TaskRequestMapper requestMapper;
    private final UserRepository userRepository;
    private final TaskResponseMapper taskResponseMapper;

    public TaskResponseDto createTask(Long chatId, String taskTitle, LocalTime taskTime) {
        log.info("Creating task for user {} with title {} and time {}", chatId, taskTitle, taskTime);
        Task task = new Task();
        task.setTaskName(taskTitle);
        task.setReminderTime(taskTime);
        task.setUser(userRepository.findByChatId(chatId)
                .orElseThrow());

        return responseMapper.map(taskRepository.save(task));
    }

    public List<TaskResponseDto> findTasksForCurrentTime() {
        List<Task> tasks = taskRepository.findAll();
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        return tasks.stream()
                .filter(task -> {
                    String timeZone = task.getUser().getCountryCode().getTimeZone();
                    LocalTime taskLocalTime = now.atDate(ZonedDateTime.now().toLocalDate())
                            .atZone(ZoneId.systemDefault())
                            .withZoneSameInstant(ZoneId.of(timeZone))
                            .toLocalTime();
                    return taskLocalTime.equals(task.getReminderTime());
                }).map(taskResponseMapper::map).toList();
    }


    public List<TaskResponseDto> findAllTasksByUserChatId(Long chatId) {
        log.info("Fetching tasks for user with chatId {}", chatId);

        // Получаем пользователя через репозиторий
        User user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for chatId: " + chatId));

        // Возвращаем все задачи, связанные с пользователем
        return taskRepository.findByUser(user).stream().map(responseMapper::map).toList();
    }

    public boolean deleteTaskById(Integer taskId) {
        return taskRepository.findById(taskId)
                .map(task -> {
                    taskRepository.delete(task);
                    taskRepository.flush();
                    return true;
                }).orElse(false);
    }

}
