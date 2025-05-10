package com.socompany.springschedulerbot.persistant.dto;

import com.socompany.springschedulerbot.persistant.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Data
@Getter
@Setter
public class TaskRequestDto {
    private Integer taskId;
    private String taskName;
    private LocalTime reminderTime;
    private User user;
}
