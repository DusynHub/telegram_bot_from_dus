package dev.dus.dusbot.commandHandlers;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

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
        return getSendMessage(chatId);
    }

    @Override
    public SendMessage handle(CallbackQuery query) {
        long chatId = query.getMessage().getChatId();
        return getSendMessage(chatId);
    }

    private  SendMessage getSendMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(HELP_TEXT);
        return sendMessage;
    }
}