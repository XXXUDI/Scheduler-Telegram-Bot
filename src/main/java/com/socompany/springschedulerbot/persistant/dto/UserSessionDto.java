package com.socompany.springschedulerbot.persistant.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSessionDto {
    private boolean isWaitingForResponse;
    private long messageId;

    public UserSessionDto(boolean isWaitingForResponse, long messageId) {
        this.isWaitingForResponse = isWaitingForResponse;
        this.messageId = messageId;
    }

    public UserSessionDto() {
    }
}
