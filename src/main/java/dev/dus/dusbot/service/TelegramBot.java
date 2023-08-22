package dev.dus.dusbot.service;

import dev.dus.dusbot.config.DusBotConfig;
import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.handlers.*;
import dev.dus.dusbot.menuSenders.MainMenu;
import dev.dus.dusbot.menuSenders.MenuSender;
import dev.dus.dusbot.menuSenders.ReturnToMainMenu;
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

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private  Handler handler;

    private MenuSender sender;

    private boolean isInitialized = false;

    private final DusBotConfig config;

    private final Map<Long, MenuState> userMenuState = new HashMap<>();

    @Autowired
    public TelegramBot(StartCommand startCommand,
                       HelpCommand helpCommand,
                       StartCallback startCallback,
                       SavePhotoInfoCallback savePhotoInfoCallback,
                       GetPhotoByTagInfoCallback getPhotoByTagInfoCallback,
                       GetUsersPhotoCallback getUsersPhotoCallback,
                       SavePhotoMessage savePhotoMessage,
                       GetUsersPhotoByTagMessage getUsersPhotoByTagMessage,
                       InvalidMessageTags invalidMessageTags,
                       InvalidMessageWithPhoto invalidMessageWithPhoto,
                       MainMenu mainMenu,
                       ReturnToMainMenu returnToMainMenu,
                       MainMenuCallback mainMenuCallback,
                       DusBotConfig config) {
        this.config = config;
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Welcome message"));
        commands.add(new BotCommand("/help", "Help information"));
        String commandsStr = commands.stream().map( curCom-> (curCom.getCommand() + "")).toString();
        log.info("Bot command: commandsStr  were registered");
        try{
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            log.error("Something went wrong while bot initialization");
            throw new RuntimeException(e);
        }

        sender = MenuSender.link(
                mainMenu,
                returnToMainMenu
        );

        handler = Handler.link(
                startCommand,
                helpCommand,
                startCallback,
                savePhotoInfoCallback,
                mainMenuCallback,
                savePhotoMessage,
                getUsersPhotoByTagMessage,
                getPhotoByTagInfoCallback,
                getUsersPhotoCallback,
                invalidMessageTags,
                invalidMessageWithPhoto
        );

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
        log.info("Update received");
        initializeBot();
        handler.handle(update, userMenuState);
    }


    private void initializeBot(){
        if(!isInitialized){
            initMenuSendersWithBot();
            initHandlersWithBot();
            isInitialized = true;
        }
    }


    private void initHandlersWithBot(){
        Handler head = handler;
        while(head!=null){
            head.setMessageSender(this);
            head.setMenuSender(sender);
            head = head.getNext();
        }
    }

    private void initMenuSendersWithBot(){
        MenuSender head = sender;
        while(head!=null){
            head.setMessageSender(this);
            head = head.getNext();
        }
    }
}
