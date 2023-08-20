package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.menuSenders.MenuSender;
import dev.dus.dusbot.service.TelegramBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;


public abstract class Handler {

    protected TelegramBot messageSender;

    protected MenuSender menuSender;

    private Handler next;

    public Handler(TelegramBot messageSender, MenuSender menuSender, Handler next) {
        this.messageSender = messageSender;
        this.menuSender = menuSender;
        this.next = next;
    }

    public static Handler link(Handler first, Handler... chain){
        Handler head = first;

        for(Handler nextLinkInChain : chain){
            head.next = nextLinkInChain;
            head = nextLinkInChain;
        }
        return first;
    }

    public abstract boolean handle(Update update, Map<Long, MenuState> userMenuState);

    protected boolean handleNext(Update update,  Map<Long, MenuState> userMenuState) {
        if (next == null) {
            return true;
        }
        return next.handle(update, userMenuState);
    }

    public void setMessageSender(TelegramBot messageSender) {
        this.messageSender = messageSender;
    }

    public Handler getNext() {
        return next;
    }
}
