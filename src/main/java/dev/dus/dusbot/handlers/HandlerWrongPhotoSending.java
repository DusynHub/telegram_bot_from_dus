package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component("wrong_photo_sending")
public class HandlerWrongPhotoSending extends Handler {

    @Autowired
    public HandlerWrongPhotoSending(
            @Lazy DefaultAbsSender messageSender,
            @Qualifier("main_menu") MenuSender menuSender,
            @Lazy Handler next) {
        super(messageSender, menuSender, null);
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {

        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            User currentUser = message.getFrom();
            long userId = message.getFrom().getId();
            String savePhotoMessage = String.format("Dear %s, you cant't send photo in that menu", currentUser.getFirstName() );
            try {
                messageSender.execute(getSendMessage(chatId, savePhotoMessage));
                menuSender.sendMenu(MenuType.MAIN, chatId);
                userMenuState.put(userId, MenuState.START);
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
