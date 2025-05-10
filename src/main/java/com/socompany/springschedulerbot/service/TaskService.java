package com.socompany.springschedulerbot.service;

import com.socompany.springschedulerbot.mapper.TaskRequestMapper;
import com.socompany.springschedulerbot.mapper.TaskResponseMapper;
import com.socompany.springschedulerbot.persistant.dto.TaskResponseDto;
import com.socompany.springschedulerbot.persistant.entity.Task;
import com.socompany.springschedulerbot.repository.TaskRepository;
import com.socompany.springschedulerbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskResponseMapper responseMapper;
    private final TaskRequestMapper requestMapper;
    private final UserRepository userRepository;

    public TaskResponseDto createTask(Long chatId, String taskTitle, LocalTime taskTime) {
        log.info("Creating task for user {} with title {} and time {}", chatId, taskTitle, taskTime);
        Task task = new Task();
        task.setTaskName(taskTitle);
        task.setReminderTime(taskTime);
        task.setUser(userRepository.findByChatId(chatId)
                .orElseThrow());

        return responseMapper.map(taskRepository.save(task));
    }


    // TODO: implement

}
