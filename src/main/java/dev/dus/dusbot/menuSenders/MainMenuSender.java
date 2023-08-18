package dev.dus.dusbot.menuSenders;

import dev.dus.dusbot.enums.MenuType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


public class MainMenuSender extends MenuSender{

    private MainMenuSender next;

    public MainMenuSender(AbsSender messageSender) {
        super(messageSender);
    }


    public static MainMenuSender link(MainMenuSender first, MainMenuSender... chain){
        MainMenuSender head = first;

        for(MainMenuSender nextLinkInChain : chain){
            head.next = nextLinkInChain;
            head = nextLinkInChain;
        }

        return first;
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
    };


    private SendMessage getSendMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("DusynBot menu");

        InlineKeyboardMarkup markUpInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> firstRowInline = new ArrayList<>();
        List<InlineKeyboardButton> secondRowInline = new ArrayList<>();


        InlineKeyboardButton startButton = new InlineKeyboardButton();
        startButton.setText("Welcome message");
        startButton.setCallbackData("START");

        InlineKeyboardButton savePhotoButton = new InlineKeyboardButton();
        savePhotoButton.setText("Save photo");
        savePhotoButton.setCallbackData("SAVE_PHOTO_MESSAGE");

        InlineKeyboardButton getPhotoButton = new InlineKeyboardButton();
        getPhotoButton.setText("Get photo");
        getPhotoButton.setCallbackData("GET_PHOTO");

        InlineKeyboardButton menuButton = new InlineKeyboardButton();
        menuButton.setText("Menu");
        menuButton.setCallbackData("MENU");

        firstRowInline.add(startButton);
        firstRowInline.add(menuButton);
        rowsInline.add(firstRowInline);

        secondRowInline.add(savePhotoButton);
        secondRowInline.add(getPhotoButton);
        rowsInline.add(secondRowInline);

        markUpInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markUpInline);
        return sendMessage;
    }
}
