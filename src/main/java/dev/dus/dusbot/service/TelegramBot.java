package dev.dus.dusbot.service;

import dev.dus.dusbot.config.DusBotConfig;
import dev.dus.dusbot.handlers.HandlerHelp;
import dev.dus.dusbot.handlers.HandlerStart;
import dev.dus.dusbot.handlers.Handler;
import dev.dus.dusbot.menuSenders.MainMenuSender;
import dev.dus.dusbot.menuSenders.MenuSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private MenuSender menuSender;
    private Handler handler;
    private boolean hasMenu = false;
    private boolean hasHandlers = false;

    private final DusBotConfig config;

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
        if(!hasMenu){
            configureMenu();
        }

        if(!hasHandlers){
            configureHandlers();
        }

        handler.handle(update);
    }

    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        try{
            execute(message);
        } catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }


    private void sendMessage(SendMessage message){
        try{
            execute(message);
        } catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private String extractCommandWithoutPostfix(String text){
        int atSignIndex = text.indexOf('@');
        return text.contains("@") ? text.substring(1, atSignIndex):text;
    }

    private void configureMenu(){
        menuSender =  MenuSender.link(new MainMenuSender(this));
        hasMenu = true;
    }

    private void configureHandlers(){
        handler = Handler.link(new HandlerStart(this, menuSender),
                new HandlerHelp(this, menuSender));
        hasHandlers = true;
    }
}
