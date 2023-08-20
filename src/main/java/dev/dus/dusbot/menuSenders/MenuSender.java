package dev.dus.dusbot.menuSenders;

import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.handlers.Handler;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;


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
