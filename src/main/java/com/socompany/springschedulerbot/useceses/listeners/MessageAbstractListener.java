package com.socompany.springschedulerbot.useceses.listeners;


import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class MessageAbstractListener {
    public abstract boolean process(Update update);
}
