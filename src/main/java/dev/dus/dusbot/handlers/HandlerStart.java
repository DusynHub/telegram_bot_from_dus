package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import dev.dus.dusbot.service.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
@Component("second")
public class HandlerStart extends Handler {

    @Autowired
    public HandlerStart(
            @Lazy TelegramBot messageSender,
            @Qualifier("main_menu") MenuSender menuSender,
            @Qualifier("help_handler") Handler next) {
        super(messageSender, menuSender, next);
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {

        if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().hasPhoto()) {
            Message message = update.getMessage();

            if (!message.getText().startsWith("/start")) {
                return handleNext(update, userMenuState);
            }

            long chatId = message.getChatId();
            User currentUser = message.getFrom();
            String startAnswer = String.format("Hi, %s. It's DusynBot", currentUser.getFirstName());
            try {
                messageSender.execute(getSendMessage(chatId, startAnswer));
                menuSender.sendMenu(MenuType.MAIN, chatId);
                userMenuState.put(currentUser.getId(), MenuState.START);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return handleNext(update, userMenuState);
    }

    private SendMessage getSendMessage(long chatId, String startAnswer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(startAnswer);
        return sendMessage;
    }

}
