package dev.dus.dusbot.enums;

public enum Commands {

    START,
    GET_PHOTO,
    SAVE_PHOTO,
    HELP,
    GET_PHOTO_MESSAGE,
    SAVE_PHOTO_MESSAGE,
    SAVE_PHOTO_MENU,
    NOT_COMMAND,
    MENU;

    public static Commands getCommand(String name){
        try {
            return Commands.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NOT_COMMAND;
        }
    }
}
