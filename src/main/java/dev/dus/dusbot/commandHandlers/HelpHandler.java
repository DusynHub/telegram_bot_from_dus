package dev.dus.dusbot.commandHandlers;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public class HelpHandler extends BotCommand implements Handle{

    private static String HELP_TEXT = "This is DusynBot \n\n" +
            "Type /start to receive welcome message\n\n" +
            "Type /help to receive help message again";

    public HelpHandler() {
    }

    public HelpHandler(@NonNull String command, @NonNull String description) {
        super(command, description);
    }

    @Override
    public SendMessage handle(Message message) {
        long chatId = message.getChatId();
        User currentUser = message.getFrom();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(HELP_TEXT);

        return sendMessage;
    }
}
