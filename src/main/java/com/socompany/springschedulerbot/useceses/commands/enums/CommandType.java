package com.socompany.springschedulerbot.useceses.commands.enums;

public enum CommandType {
    START ("/start"),
    SCHEDULER ("/scheduler"),
    BACK ("/back"),
    WEATHER ("/weather"),
    EVENTS ("/events"),
    BITCOIN ("/bitcoin"),
    CURRENCY ("/currency"),
    CHANGE_DATE ("/changeDate"),
    CHANGE_TIMEZONE("/changeTimezone"),
    CHANGE_LANGUAGE("/changeLanguage"),
    ABOUT ("/about"),
    SETTINGS ("/settings"),
    TODO_MENU("/todoList"),
    ADD_TASK("/addTask"),
    DELETE_TASK("/deleteTask"),
    CANCEL_DELETING_TASK("/cancelDeletingTask"),
    UNSUPPORTED_COMMAND("/unsupported"),
    WRONG_COMMAND("/wrongCommand");



    private final String command;

    CommandType(String command) {
        this.command = command;
    }

    public String getCommand() {
        return String.valueOf(command);
    }

    public static CommandType getCommandType(String command) {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.getCommand().equals(command)) {
                return commandType;
            }
        }
        throw new IllegalArgumentException("Invalid command type");
    }
}
