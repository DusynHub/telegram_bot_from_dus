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

public class SavePhotoMenuHandler extends BotCommand implements Handle{

    public SavePhotoMenuHandler() {
    }

    public SavePhotoMenuHandler(@NonNull String command, @NonNull String description) {
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
        sendMessage.setText("save photo menu");

        InlineKeyboardMarkup markUpInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> firstRowInline = new ArrayList<>();

        InlineKeyboardButton savePhotoButton = new InlineKeyboardButton();
        savePhotoButton.setText("save downloaded photo");
        savePhotoButton.setCallbackData("SAVE_PHOTO");

        InlineKeyboardButton menuButton = new InlineKeyboardButton();
        menuButton.setText("back to main menu");
        menuButton.setCallbackData("MENU");

        firstRowInline.add(savePhotoButton);
        firstRowInline.add(menuButton);
        rowsInline.add(firstRowInline);

        markUpInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markUpInline);
        return sendMessage;
    }
}
