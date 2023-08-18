package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class HandleStart extends Handler {

    private HandleStart next;

    public HandleStart(AbsSender messageSender, MenuSender menuSender) {
        super(messageSender, menuSender);
    }


    public static HandleStart link(HandleStart first, HandleStart... chain) {
        HandleStart head = first;

        for (HandleStart nextLinkInChain : chain) {
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
                messageSender.execute(getSendMessage(chatId, startAnswer));
                menuSender.sendMenu(MenuType.MAIN, chatId);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return handleNext(update);
    }

    private SendMessage getSendMessage(long chatId, String startAnswer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(startAnswer);
        return sendMessage;
    }

}
