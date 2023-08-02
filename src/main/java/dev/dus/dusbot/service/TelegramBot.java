package dev.dus.dusbot.service;

import dev.dus.dusbot.config.DusBotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final DusBotConfig config;

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

        if(update.hasMessage() && update.getMessage().hasText()){
            text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
        }

        if(text == null){
            return;
        }

        if(text.equals("/start")){
            log.info("Получен запрос к боту по пути '/start'");
            startCommandAction(chatId, update.getMessage().getFrom().getFirstName());
            return;
        }

        log.info("Получен запрос к боту по пути '{}'", text);
        sendMessage(chatId, "Not available function yet");
    }

    private void startCommandAction(long chatId, String name){
        String startAnswer = String.format("Hi, %s. It's DusynBot", name);
        sendMessage(chatId, startAnswer);
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
}
