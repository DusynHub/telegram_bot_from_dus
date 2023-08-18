package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class HandlerHelp extends Handler {

    private static final String HELP_TEXT = "This is DusynBot \n\n" +
            "Type /start to receive welcome message\n\n" +
            "Type /help to receive help message again";

    private HandlerHelp next;

    public HandlerHelp(AbsSender messageSender, MenuSender menuSender) {
        super(messageSender, menuSender);
    }


    public static HandlerHelp link(HandlerHelp first, HandlerHelp... chain) {
        HandlerHelp head = first;

        for (HandlerHelp nextLinkInChain : chain) {
            head.next = nextLinkInChain;
            head = nextLinkInChain;
        }
        return first;
    }

    public boolean handle(Update update) {

        if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().hasPhoto()) {
            Message message = update.getMessage();

            if (!message.getText().startsWith("/")) {
                return handleNext(update);
            }

            long chatId = message.getChatId();
            User currentUser = message.getFrom();
            String startAnswer = String.format("Hi, %s. It's DusynBot", currentUser.getFirstName());
            try {
                messageSender.execute(getSendMessage(chatId));
                menuSender.sendMenu(MenuType.MAIN, chatId);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return handleNext(update);
    }

    private  SendMessage getSendMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(HELP_TEXT);
        return sendMessage;
    }

}
