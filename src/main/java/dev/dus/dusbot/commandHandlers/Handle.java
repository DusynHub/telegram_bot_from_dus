package dev.dus.dusbot.commandHandlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface Handle {

    SendMessage handle(Message message);

    SendMessage handle(CallbackQuery query);
}
