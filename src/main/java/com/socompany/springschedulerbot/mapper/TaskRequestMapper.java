package com.socompany.springschedulerbot.mapper;

import com.socompany.springschedulerbot.persistant.dto.TaskRequestDto;
import com.socompany.springschedulerbot.persistant.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskRequestMapper implements Mapper<TaskRequestDto, Task>{

    @Override
    public Task map(TaskRequestDto from) {
        Task task = new Task();
        task.setTaskId(from.getTaskId());
        task.setTaskName(from.getTaskName());
        task.setReminderTime(from.getReminderTime());
        task.setUser(from.getUser());

        return task;
    }

    @Override
    public Task map(TaskRequestDto fromObj, Task toObj) {
        toObj.setTaskId(fromObj.getTaskId());
        toObj.setTaskName(fromObj.getTaskName());
        toObj.setReminderTime(fromObj.getReminderTime());
        toObj.setUser(fromObj.getUser());
        return toObj;

    }
}
