package dev.dus.dusbot.menuSenders;

import dev.dus.dusbot.enums.MenuType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component("main_menu")
public class MainMenu extends MenuSender{

    @Autowired
    public MainMenu(
        @Lazy DefaultAbsSender messageSender,
        @Qualifier("return_to_main_menu") MenuSender next) {
        super(messageSender,next);
    }

    public boolean sendMenu(MenuType menuType, long chatId) {
        if(menuType == MenuType.MAIN){
            try {
                messageSender.execute(getSendMessage(chatId));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return sendMenuNext(menuType, chatId);
    }

    private SendMessage getSendMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("DusynBot menu");

        InlineKeyboardMarkup markUpInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> firstRowInline = new ArrayList<>();
        List<InlineKeyboardButton> secondRowInline = new ArrayList<>();
        List<InlineKeyboardButton> thirdRowInLine = new ArrayList<>();


        InlineKeyboardButton startButton = new InlineKeyboardButton();
        startButton.setText("Welcome message");
        startButton.setCallbackData("START");

        InlineKeyboardButton savePhotoButton = new InlineKeyboardButton();
        savePhotoButton.setText("save photo");
        savePhotoButton.setCallbackData("SAVE_PHOTO_MESSAGE");

        InlineKeyboardButton getPhotoButton = new InlineKeyboardButton();
        getPhotoButton.setText("get all your photos");
        getPhotoButton.setCallbackData("GET_USERS_PHOTO");

        InlineKeyboardButton menuButton = new InlineKeyboardButton();
        menuButton.setText("menu");
        menuButton.setCallbackData("MENU");

        InlineKeyboardButton getPhotoByTagsButton = new InlineKeyboardButton();
        getPhotoByTagsButton.setText("get photo by tags");
        getPhotoByTagsButton.setCallbackData("GET_PHOTO_BY_TAGS_MESSAGE");

        firstRowInline.add(startButton);
        firstRowInline.add(menuButton);
        rowsInline.add(firstRowInline);

        secondRowInline.add(savePhotoButton);
        secondRowInline.add(getPhotoButton);
        rowsInline.add(secondRowInline);

        thirdRowInLine.add(getPhotoByTagsButton);
        rowsInline.add(thirdRowInLine);

        markUpInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markUpInline);
        return sendMessage;
    }
}
