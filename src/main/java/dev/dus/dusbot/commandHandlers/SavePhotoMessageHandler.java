package dev.dus.dusbot.commandHandlers;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public class SavePhotoMessageHandler extends BotCommand implements Handle{


    public SavePhotoMessageHandler() {
    }

    public SavePhotoMessageHandler(@NonNull String command, @NonNull String description) {
        super(command, description);
    }

    @Override
    public SendMessage handle(Message message) {
        long chatId = message.getChatId();
        User currentUser = message.getFrom();
        String GetPhotoAnswer = String.format("Send photo, %s, please.", currentUser.getFirstName());

        return getSendMessage(chatId, GetPhotoAnswer);
    }

    @Override
    public SendMessage handle(CallbackQuery query) {

        long chatId = query.getMessage().getChatId();
        User currentUser = query.getFrom();
        String savePhotoMessage = String.format("Send photo, %s, please.", currentUser.getFirstName());

        return getSendMessage(chatId, savePhotoMessage);
    }


    private SendMessage getSendMessage(long chatId, String startAnswer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(startAnswer);
        return sendMessage;
    }
}
