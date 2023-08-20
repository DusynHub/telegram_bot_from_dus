package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.menuSenders.MenuSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;


public abstract class Handler {

    protected DefaultAbsSender messageSender;

    protected MenuSender menuSender;

    private Handler next;

    public Handler(DefaultAbsSender messageSender, MenuSender menuSender, Handler next) {
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
}
