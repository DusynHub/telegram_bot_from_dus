package dev.dus.dusbot.service;

import dev.dus.dusbot.commandHandlers.Handle;
import dev.dus.dusbot.commandHandlers.HelpHandler;
import dev.dus.dusbot.commandHandlers.StartHandler;
import dev.dus.dusbot.config.DusBotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
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

    private static String HELP_TEXT = "This is DusynBot \n\n" +
            "Type /start to receive welcome message\n\n" +
            "Type /help to receive help message again";

    private final DusBotConfig config;
    private final Map<String, BotCommand> commandHandlers = new HashMap<>();
    {
        commandHandlers.put("/start", new StartHandler("/start", "Welcome message"));
        commandHandlers.put("/help", new HelpHandler("/help", "Help information"));
    }

    public TelegramBot(DusBotConfig config) {
        this.config = config;
        List<BotCommand> commands = new ArrayList<>();
        commands.add(commandHandlers.get("/start"));
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

        String text = null;
        long chatId = -1;
        User currentUser = null;

        if(update.hasMessage() && update.getMessage().hasText()){
            text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
        }

        if(!text.startsWith("/")){
            log.info("Получено сообщение к боту по пути '{}'", text);
            sendMessage(chatId, "This is not command for bot");
            return;
        }

        Handle commandHandler = null;
        if( (commandHandler = (Handle) commandHandlers.get(extractCommandWithoutPostfix(text))) != null){
            sendMessage(commandHandler.handle(update.getMessage()));
        } else {
            sendMessage(chatId, "Not available function yet");
        }


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
        return text.contains("@") ? text.substring(0, atSignIndex):text;
    }
}
