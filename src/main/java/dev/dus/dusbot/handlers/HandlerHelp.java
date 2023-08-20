package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

public class HandlerHelp extends Handler {

    private static final String HELP_TEXT = "This is DusynBot \n\n" +
            "Type /start to receive welcome message\n\n" +
            "Type /help to receive help message again";

    public HandlerHelp(DefaultAbsSender messageSender, MenuSender menuSender) {
        super(messageSender, menuSender);
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {
        if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().hasPhoto()) {

            Message message = update.getMessage();

            if (!message.getText().startsWith("/help")) {
                return handleNext(update, userMenuState);
            }

            long chatId = message.getChatId();
            User currentUser = message.getFrom();
            try {
                messageSender.execute(getSendMessage(chatId));
                menuSender.sendMenu(MenuType.MAIN, chatId);
                userMenuState.put(currentUser.getId(), MenuState.HELP);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return handleNext(update, userMenuState);
    }

    private  SendMessage getSendMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(HELP_TEXT);
        return sendMessage;
    }

}
