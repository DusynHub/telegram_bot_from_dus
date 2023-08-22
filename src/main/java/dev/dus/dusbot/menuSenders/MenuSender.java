package dev.dus.dusbot.menuSenders;

import dev.dus.dusbot.enums.MenuType;
import org.telegram.telegrambots.bots.DefaultAbsSender;


public abstract class MenuSender {

    protected DefaultAbsSender messageSender;

    private MenuSender next;

    public MenuSender(DefaultAbsSender messageSender, MenuSender next) {
        this.messageSender = messageSender;
        this.next = next;
    }

    public static MenuSender link(MenuSender first, MenuSender... chain){
        MenuSender head = first;
        for(MenuSender nextLinkInChain : chain){
            head.next = nextLinkInChain;
            head = nextLinkInChain;
        }
        return first;
    }

    public abstract boolean sendMenu(MenuType menuType, long chatId);

    protected boolean sendMenuNext(MenuType menuType,  long chatId) {
        if (next == null) {
            return true;
        }
        return next.sendMenu(menuType, chatId);
    }

    public DefaultAbsSender getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(DefaultAbsSender messageSender) {
        this.messageSender = messageSender;
    }

    public MenuSender getNext() {
        return next;
    }
}
