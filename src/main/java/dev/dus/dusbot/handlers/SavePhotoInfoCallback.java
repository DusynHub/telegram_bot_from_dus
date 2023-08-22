package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Component
@Slf4j
public class SavePhotoInfoCallback extends Handler {

    @Autowired
    public SavePhotoInfoCallback(
    ) {
        super(null, null, null);
        log.info("[{}]>>> {} bean has been created",
                this.getClass().getSimpleName(),
                this.getClass().getSimpleName());
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {
        log.info("[{}]>>> {} request to check 'update'",
                this.getClass().getSimpleName(),
                this.getClass().getSimpleName());
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (callbackQuery.getData().equals("SAVE_PHOTO_MESSAGE")) {
                long chatId = callbackQuery.getMessage().getChatId();
                User currentUser = callbackQuery.getFrom();
                String savePhotoMessage
                        = String.format("Send photo, %s, please.", currentUser.getFirstName());
                try {
                    messageSender.execute(getSendMessage(chatId, savePhotoMessage));
                    menuSender.sendMenu(MenuType.BACK_TO_MAIN, chatId);
                    userMenuState.put(currentUser.getId(), MenuState.SAVE_PHOTO_MESSAGE);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                log.info("[{}]>>> {} answered to {} callback",
                        this.getClass().getSimpleName(),
                        this.getClass().getSimpleName(),
                        callbackQuery.getData());
                return false;
            }
            log.info("[{}]>>> Callback data = {} is not equal to SAVE_PHOTO_MESSAGE",
                    this.getClass().getSimpleName(),
                    callbackQuery.getData());
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
