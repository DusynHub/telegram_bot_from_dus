package dev.dus.dusbot.menuSenders;

import dev.dus.dusbot.enums.MenuType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component("return_to_main_menu")
public class ReturnToMainMenu extends MenuSender{


    public ReturnToMainMenu(
            @Lazy DefaultAbsSender messageSender,
            @Lazy MenuSender next) {
        super(messageSender, null);
    }

    public boolean sendMenu(MenuType menuType, long chatId) {
        if(menuType == MenuType.BACK_TO_MAIN){
            try {
                messageSender.execute(getSendMessage(chatId));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return sendMenuNext(menuType, chatId);
    }

    private SendMessage getSendMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("press button if you want return to main menu");

        InlineKeyboardMarkup markUpInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> firstRowInline = new ArrayList<>();

        InlineKeyboardButton menuButton = new InlineKeyboardButton();
        menuButton.setText("back to main menu");
        menuButton.setCallbackData("MENU");

        firstRowInline.add(menuButton);
        rowsInline.add(firstRowInline);

        markUpInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markUpInline);
        return sendMessage;
    }
}
