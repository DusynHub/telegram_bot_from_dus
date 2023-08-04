package dev.dus.dusbot.commandHandlers;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public class StartHandler extends BotCommand implements Handle{


    public StartHandler() {
    }

    public StartHandler(@NonNull String command, @NonNull String description) {
        super(command, description);
    }

    @Override
    public SendMessage handle(Message message) {
        long chatId = message.getChatId();
        User currentUser = message.getFrom();
        String startAnswer = String.format("Hi, %s. It's DusynBot", currentUser.getFirstName());

        return getSendMessage(chatId, startAnswer);
    }

    @Override
    public SendMessage handle(CallbackQuery query) {

        long chatId = query.getMessage().getChatId();
        User currentUser = query.getFrom();
        String startAnswer = String.format("Hi, %s. It's DusynBot", currentUser.getFirstName());
        
        return getSendMessage(chatId, startAnswer);
    }


    private SendMessage getSendMessage(long chatId, String startAnswer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(startAnswer);
        return sendMessage;
    }
}
