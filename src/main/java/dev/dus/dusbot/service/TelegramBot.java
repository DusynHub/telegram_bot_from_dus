package dev.dus.dusbot.service;

import dev.dus.dusbot.config.DusBotConfig;
import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.handlers.*;
import dev.dus.dusbot.menuSenders.MainMenuSender;
import dev.dus.dusbot.menuSenders.MenuSender;
import dev.dus.dusbot.menuSenders.ReturnToMainMenuSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final Handler handler;

    private final MenuSender menuSender;

    private final DusBotConfig config;

    private final Map<Long, MenuState> userMenuState = new HashMap<>();

    public TelegramBot(DusBotConfig config) {
        this.config = config;
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Welcome message"));
        commands.add(new BotCommand("/help", "Help information"));
        try{
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            log.error("Something went wrong while bot initialization");
            throw new RuntimeException(e);
        }
        menuSender = MenuSender.link(new MainMenuSender(this),
                new ReturnToMainMenuSender(this));
        handler = Handler.link(new HandlerStart(this, menuSender),
                new HandlerHelp(this, menuSender),
                new HandlerStartCallback(this, menuSender),
                new HandlerSavePhotoMessageCallback(this, menuSender),
                new HandlerSavePhoto(this, menuSender),
                new HandlerWrongPhotoSending(this,menuSender));
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        handler.handle(update, userMenuState);
    }

    private String extractCommandWithoutPostfix(String text){
        int atSignIndex = text.indexOf('@');
        return text.contains("@") ? text.substring(1, atSignIndex):text;
    }
}
