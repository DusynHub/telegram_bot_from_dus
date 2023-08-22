package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import dev.dus.dusbot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Component
@Slf4j
public class GetPhotoByTagInfoCallback extends Handler {

    @Autowired
    public GetPhotoByTagInfoCallback(
    ) {
        super(null, null, null);
        log.info("[{}]>>> {} bean has been created",
                this.getClass().getSimpleName(),
                this.getClass().getSimpleName());
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (callbackQuery.getData().equals("GET_PHOTO_BY_TAGS_MESSAGE")) {
                long chatId = callbackQuery.getMessage().getChatId();
                User currentUser = callbackQuery.getFrom();
                String geyPhotoByTagsInfo
                        = String.format("Send tags divided by space, %s, please. \n" +
                        "Example \"some_tag another_tag \"" , currentUser.getFirstName());
                try {
                    messageSender.execute(getSendMessage(chatId, geyPhotoByTagsInfo));
                    menuSender.sendMenu(MenuType.BACK_TO_MAIN, chatId);
                    userMenuState.put(currentUser.getId(), MenuState.GET_PHOTO_BY_TAGS_MESSAGE);
                    log.info("[{}]>>> Info about tags format was sent", this.getClass().getSimpleName());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }
        }
        log.info("[{}]>>> requested method handleNext(update,  userMenuState)", this.getClass().getSimpleName());
        return handleNext(update, userMenuState);
    }

    private SendMessage getSendMessage(long chatId, String startAnswer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(startAnswer);
        return sendMessage;
    }

}
