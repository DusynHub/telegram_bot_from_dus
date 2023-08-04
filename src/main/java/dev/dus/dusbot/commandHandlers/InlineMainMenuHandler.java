package dev.dus.dusbot.commandHandlers;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineMainMenuHandler extends BotCommand implements Handle{


    public InlineMainMenuHandler() {
    }

    public InlineMainMenuHandler(@NonNull String command, @NonNull String description) {
        super(command, description);
    }

    @Override
    public SendMessage handle(Message message) {
        long chatId = message.getChatId();
        return getSendMessage(chatId);
    }

    @Override
    public SendMessage handle(CallbackQuery query) {
        long chatId = query.getMessage().getChatId();
        return getSendMessage(chatId);
    }

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
