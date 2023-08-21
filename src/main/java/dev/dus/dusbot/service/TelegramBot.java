package dev.dus.dusbot.service;

import dev.dus.dusbot.config.DusBotConfig;
import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.handlers.*;
import dev.dus.dusbot.menuSenders.MenuSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

@Component("telegram_bot")
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final Handler handler;

    private final MenuSender sender;

    private final DusBotConfig config;

    private final Map<Long, MenuState> userMenuState = new HashMap<>();

    @Autowired
    public TelegramBot(@Qualifier("handler_chain_link_1") Handler handler,
                       @Qualifier("main_menu") MenuSender sender,
                       DusBotConfig config) {
        this.handler = handler;
        this.sender = sender;
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
        initHandlersWithBot();
        initMenuSenders();
        handler.handle(update, userMenuState);
    }

    private void initHandlersWithBot(){
        Handler head = handler;
        while(head!=null){
            head.setMessageSender(this);
            head = head.getNext();
        }
    }

    private void initMenuSenders(){
        MenuSender head = sender;
        while(head!=null){
            head.setMessageSender(this);
            head = head.getNext();
        }
    }
}
