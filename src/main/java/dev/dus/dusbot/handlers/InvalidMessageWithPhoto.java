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
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Component
public class InvalidMessageWithPhoto extends Handler {

    @Autowired
    public InvalidMessageWithPhoto(
            @Lazy TelegramBot messageSender,
            @Lazy MenuSender menuSender,
            @Lazy Handler next
    ) {
        super(null, null, null);
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {

        if (update.hasMessage()
                && update.getMessage().hasPhoto()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            User currentUser = message.getFrom();
            long userId = message.getFrom().getId();
            String savePhotoMessage = String.format("Dear %s, you cant't send photo in that menu", currentUser.getFirstName() );

//            if (userMenuState.getOrDefault(userId, MenuState.START)
//                    != MenuState.SAVE_PHOTO_MESSAGE) {
//                return handleNext(update, userMenuState);
//            }

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
