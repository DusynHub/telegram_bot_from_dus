package dev.dus.dusbot.handlers;

import dev.dus.dusbot.menuSenders.MenuSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;


public abstract class Handler {

    protected AbsSender messageSender;

    protected MenuSender menuSender;

    private Handler next;

    public Handler(AbsSender messageSender, MenuSender menuSender) {
        this.messageSender = messageSender;
        this.menuSender = menuSender;
    }

    public static Handler link(Handler first, Handler... chain){
        Handler head = first;

        for(Handler nextLinkInChain : chain){
            head.next = nextLinkInChain;
            head = nextLinkInChain;
        }
        return first;
    }

    public abstract boolean handle(Update update);

    protected boolean handleNext(Update update) {
        if (next == null) {
            return true;
        }
        return next.handle(update);
    }
}
