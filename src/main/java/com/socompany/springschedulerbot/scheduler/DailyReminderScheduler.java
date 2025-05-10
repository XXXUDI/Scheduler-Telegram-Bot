package com.socompany.springschedulerbot.scheduler;

import com.socompany.springschedulerbot.common.CommonInfo;
import com.socompany.springschedulerbot.persistant.dto.UserResponseDto;
import com.socompany.springschedulerbot.service.UserService;
import com.socompany.springschedulerbot.useceses.commands.SendDailyReminderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyReminderScheduler {
    private final UserService userService;
    private final SendDailyReminderCommand dailyReminderCommand;

    @Scheduled(cron = "0 * * * * *") // Every minute
    public void sendDailyReminderMessage() {
        log.info("Sending daily reminder messages");
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        List<UserResponseDto> users = userService.findByDailyReminderForReminderAtCurrentTime();


        for (UserResponseDto dto : users) {
            if(dto.isReminderEnabled()) {
                log.info("Sending daily reminder message to user {}", dto.getChatId());
                CommonInfo cm = new CommonInfo();
                cm.setChatId(dto.getChatId());
                dailyReminderCommand.execute(cm);
            }
        }
    }
}
