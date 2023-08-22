package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import dev.dus.dusbot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Component
@Slf4j
public class InvalidMessageWithPhoto extends Handler {

    @Autowired
    public InvalidMessageWithPhoto() {
        super(null, null, null);
        log.info("[{}]>>> {} bean has been created",
                this.getClass().getSimpleName(),
                this.getClass().getSimpleName());
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {

        log.info("[{}]>>> {} request to check 'update'",
                this.getClass().getSimpleName(),
                this.getClass().getSimpleName());

        if (update.hasMessage()
                && update.getMessage().hasPhoto()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            User currentUser = message.getFrom();
            long userId = message.getFrom().getId();
            String invalidMessageWithPhotoAnswer = String.format("Dear %s, you cant't send photo in that menu", currentUser.getFirstName());

            try {
                messageSender.execute(getSendMessage(chatId, invalidMessageWithPhotoAnswer));
                menuSender.sendMenu(MenuType.MAIN, chatId);
                userMenuState.put(userId, MenuState.START);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

            log.info("[{}]>>> {} sent message {}",
                    this.getClass().getSimpleName(),
                    this.getClass().getSimpleName(),
                    invalidMessageWithPhotoAnswer);
            return false;
        }

        log.info("[{}]>>> requested method handleNext(update,  userMenuState)",
                this.getClass().getSimpleName());

        return handleNext(update, userMenuState);
    }

    private SendMessage getSendMessage(long chatId, String startAnswer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(startAnswer);
        return sendMessage;
    }
}
