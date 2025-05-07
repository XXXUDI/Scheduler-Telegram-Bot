package com.socompany.springschedulerbot.useceses.handlers;

import com.socompany.springschedulerbot.common.CommonInfo;

public interface CommandHandler {

    public void handleCommand(String command, CommonInfo commonInfo);
}
