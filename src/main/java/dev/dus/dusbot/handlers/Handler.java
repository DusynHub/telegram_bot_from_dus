package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.menuSenders.MenuSender;
import dev.dus.dusbot.service.TelegramBot;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Getter
@Setter
@Slf4j
public abstract class Handler {

    protected TelegramBot messageSender;

    protected MenuSender menuSender;

    private Handler next;

    public Handler(TelegramBot messageSender, MenuSender menuSender, Handler next) {
        this.messageSender = messageSender;
        this.menuSender = menuSender;
        this.next = next;
    }

    public static Handler link(Handler first, Handler... chain) {
        Handler head = first;

        log.info("[Handler]>>> {} is first link chain", head.getClass().getSimpleName());

        for (int i = 0; i < chain.length; i++) {
            head.next = chain[i];
            head = chain[i];
            log.info("[Handler]>>> {} is {} link chain", head.getClass().getSimpleName(), i + 2);
        }
        return first;
    }

    public abstract boolean handle(Update update, Map<Long, MenuState> userMenuState);

    protected boolean handleNext(Update update, Map<Long, MenuState> userMenuState) {
        if (next == null) {
            return true;
        }
        return next.handle(update, userMenuState);
    }

    public Handler getNext() {
        return next;
    }
}
