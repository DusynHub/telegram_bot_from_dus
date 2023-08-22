package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import dev.dus.dusbot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
@Component
@Slf4j
public class StartCommand extends Handler {

    @Autowired
    public StartCommand(
            @Lazy TelegramBot messageSender,
            @Lazy MenuSender menuSender,
            @Lazy Handler next
    ) {
        super(null, null, null);
        log.info("[{}]>>> {} bean has been created",
                this.getClass().getSimpleName(),
                this.getClass().getSimpleName());
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {
        log.info("[{}]>>> {} request to check update",
                this.getClass().getSimpleName(),
                this.getClass().getSimpleName());
        if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().hasPhoto()) {
            Message message = update.getMessage();
            if (!message.getText().startsWith("/start")) {
                log.info("[{}]>>> Text = {} is not a command",
                        this.getClass().getSimpleName(),
                        message.getText());
                return handleNext(update, userMenuState);
            }
            long chatId = message.getChatId();
            User currentUser = message.getFrom();
            String startAnswer =
                    String.format(  "Hi, %s. It's DusynBot \n" +
                                    "You can save photo with tag \n" +
                                    "And then get photo(s) by multiple tags \n"
                            , currentUser.getFirstName());
            try {
                messageSender.execute(getSendMessage(chatId, startAnswer));
                menuSender.sendMenu(MenuType.MAIN, chatId);
                userMenuState.put(currentUser.getId(), MenuState.START);
                log.info("[{}]>>> {} answered to  {} command",
                        this.getClass().getSimpleName(),
                        this.getClass().getSimpleName(),
                        message.getText());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return handleNext(update, userMenuState);
    }

    private SendMessage getSendMessage(long chatId, String startAnswer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(startAnswer);
        return sendMessage;
    }

}
