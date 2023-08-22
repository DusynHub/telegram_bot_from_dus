package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import dev.dus.dusbot.model.FilePath;
import dev.dus.dusbot.repository.FilePathRepository;
import dev.dus.dusbot.service.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component
public class MainMenuCallback extends Handler {

    @Autowired
    public MainMenuCallback(
            @Lazy TelegramBot messageSender,
            @Lazy MenuSender menuSender,
            @Lazy Handler next,
            FilePathRepository filePathRepository
    ) {
        super(null, null, null);
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (callbackQuery.getData().equals("MENU")) {
                long chatId = callbackQuery.getMessage().getChatId();
                menuSender.sendMenu(MenuType.MAIN, chatId);
                return false;
            }
        }
        return handleNext(update, userMenuState);
    }
}
