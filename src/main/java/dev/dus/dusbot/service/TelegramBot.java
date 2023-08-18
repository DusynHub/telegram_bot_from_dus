package dev.dus.dusbot.service;

import dev.dus.dusbot.commandHandlers.SavePhotoHandler;
import dev.dus.dusbot.commandHandlers.SavePhotoMenuHandler;
import dev.dus.dusbot.commandHandlers.SavePhotoMessageHandler;
import dev.dus.dusbot.commandHandlers.Handle;
import dev.dus.dusbot.commandHandlers.HelpHandler;
import dev.dus.dusbot.commandHandlers.InlineMainMenuHandler;
import dev.dus.dusbot.commandHandlers.StartHandler;
import dev.dus.dusbot.config.DusBotConfig;
import dev.dus.dusbot.enums.Commands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.dus.dusbot.enums.Commands.*;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final DusBotConfig config;
    private final Map<Commands, Handle> commandHandlers = new HashMap<>();
    {
        commandHandlers.put(START, new StartHandler("/start", "Welcome message"));
        commandHandlers.put(HELP, new HelpHandler("/help", "Help information"));
        commandHandlers.put(MENU, new InlineMainMenuHandler("/menu", "Menu"));
        commandHandlers.put(SAVE_PHOTO_MESSAGE, new SavePhotoMessageHandler("/save_photo_message", "Save photo message"));
        commandHandlers.put(SAVE_PHOTO_MENU, new SavePhotoMenuHandler("/save_photo_menu", "save_photo_menu"));
        commandHandlers.put(SAVE_PHOTO, new SavePhotoHandler("/save_photo", "save_photo", this));
    }

    public TelegramBot(DusBotConfig config) {
        this.config = config;
        List<BotCommand> commands = new ArrayList<>();
        commands.add((BotCommand) commandHandlers.get(START));
        commands.add((BotCommand) commandHandlers.get(HELP));
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

        if(update.hasMessage() && update.getMessage().hasText() && !update.getMessage().hasPhoto()){
            text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();

            if(text.startsWith("/")){
                Handle commandHandler = null;
                if((commandHandler = commandHandlers.get(Commands.getCommand(extractCommandWithoutPostfix(text)))) != null){
                    Message updatedMsg = update.getMessage();
                    sendMessage(commandHandler.handle(updatedMsg));
                } else {
                    sendMessage(chatId, "Not available function yet");
                }
                callMainMenu(update);
                return;
            }

            log.info("Получено сообщение к боту по пути '{}'", text);
            sendMessage(chatId, "This is not command for bot");
            callMainMenu(update);
            return;
        }

        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            text = update.getMessage().getCaption();
            chatId = update.getMessage().getChatId();

            if(text.startsWith("#")){
                String[] tags = text.split(" ");

                callMainMenu(update);
                return;
            }
            log.info("Cообщение к боту по пути '{}'", text);
            sendMessage(chatId, "Тэги к фото должны начинаться с символа '#'  и быть разделены пробелом");
            callMainMenu(update);
            return;
        }

        if(update.hasCallbackQuery()){
            String callBackData = update.getCallbackQuery().getData();
            Commands action =  Commands.getCommand(callBackData);
            sendMessage(commandHandlers.get(Commands.getCommand(callBackData)).handle(update.getCallbackQuery()));
            if(action == SAVE_PHOTO_MESSAGE){
                callSavePhotoMenu(update);
                return;
            }

            if(action == SAVE_PHOTO){
                sendMessage(commandHandlers.get(SAVE_PHOTO).handle(update.getMessage()));
                return;
            }

            if(action == MENU){
                return;
            }

            callMainMenu(update);
        }
    }

    private void callMainMenu(Update update) {
        if(update.getMessage() == null){
            sendMessage(commandHandlers.get(MENU).handle(update.getCallbackQuery()));
            return;
        }
        sendMessage(commandHandlers.get(MENU).handle(update.getMessage()));
    }

    private void callSavePhotoMenu(Update update) {
        if(update.getMessage() == null){
            sendMessage(commandHandlers.get(SAVE_PHOTO_MENU).handle(update.getCallbackQuery()));
            return;
        }
        sendMessage(commandHandlers.get(SAVE_PHOTO_MENU).handle(update.getMessage()));
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
}
