package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

public class HandlerSavePhotoMessageCallback extends Handler {


    public HandlerSavePhotoMessageCallback(DefaultAbsSender messageSender, MenuSender menuSender) {
        super(messageSender, menuSender);
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {

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
                return false;
            }
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
