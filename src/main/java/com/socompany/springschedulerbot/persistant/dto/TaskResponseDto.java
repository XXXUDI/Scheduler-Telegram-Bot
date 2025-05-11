package com.socompany.springschedulerbot.persistant.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Data
@Setter
@Getter
public class TaskResponseDto {
    private Integer taskId;
    private String taskName;
    private LocalTime reminderTime;
    private UserResponseDto userResponseDto;
}
