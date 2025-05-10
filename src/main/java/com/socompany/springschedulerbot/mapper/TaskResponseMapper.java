package com.socompany.springschedulerbot.mapper;

import com.socompany.springschedulerbot.persistant.dto.TaskResponseDto;
import com.socompany.springschedulerbot.persistant.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskResponseMapper implements Mapper<Task, TaskResponseDto> {

    private final UserResponseMapper userResponseMapper;

    public TaskResponseMapper(UserResponseMapper userResponseMapper) {
        this.userResponseMapper = userResponseMapper;
    }

    @Override
    public TaskResponseDto map(Task from) {
        TaskResponseDto responseDto = new TaskResponseDto();
        responseDto.setTaskName(from.getTaskName());
        responseDto.setReminderTime(from.getReminderTime());
        responseDto.setUserResponseDto(userResponseMapper.map(from.getUser()));
        return responseDto;
    }
}
