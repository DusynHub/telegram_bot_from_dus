package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component("save_photo")
public class HandlerSavePhoto extends Handler {


    @Autowired
    public HandlerSavePhoto(
            @Lazy DefaultAbsSender messageSender,
            @Qualifier("main_menu") MenuSender menuSender,
            @Qualifier("wrong_photo_sending") Handler next) {
        super(messageSender, menuSender, next);
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {

        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            User currentUser = message.getFrom();
            long userId = message.getFrom().getId();

            if (userMenuState.getOrDefault(userId, MenuState.START) != MenuState.SAVE_PHOTO_MESSAGE) {
                return handleNext(update, userMenuState);
            }
            String currentDate = message.getDate().toString();
            java.io.File downloadedToBotChat = downloadPhotoByFilePath(getFilePath(getPhoto(message)));

            java.io.File dir
                    = new java.io.File("C:\\Users\\Kansa\\dev\\telegram_storage\\" + userId);
            if(!dir.exists()){
                dir.mkdirs();
            }
            java.io.File localFile = new java.io.File( dir,currentDate + ".png");

            String savePhotoMessage = String.format("Dear %s, your photo have been saved", currentUser.getFirstName() );
            try(
                    FileInputStream downloadedToBotChatFileInputStream = new FileInputStream(downloadedToBotChat);
                    FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
                    BufferedInputStream bis = new BufferedInputStream(downloadedToBotChatFileInputStream);
                    BufferedOutputStream bos = new BufferedOutputStream(localFileOutputStream);
            ) {
                int b = 0;
                while (b != -1){
                    b = bis.read();
                    bos.write(b);
                }
                //FileUtils.copyInputStreamToFile(new FileInputStream(downloadedToBotChat), localFile);
                messageSender.execute(getSendMessage(chatId, savePhotoMessage));
                menuSender.sendMenu(MenuType.MAIN, chatId);
                userMenuState.put(currentUser.getId(), MenuState.SAVE_PHOTO);
            } catch (IOException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return handleNext(update, userMenuState);
    }

    private SendMessage getSendMessage(long chatId, String startAnswer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(startAnswer);
        return sendMessage;
    }

    private PhotoSize getPhoto(Message message) {
        // Check that the update contains a message and the message has a photo

        if (message.hasPhoto()) {
            // When receiving a photo, you usually get different sizes of it
            List<PhotoSize> photos = message.getPhoto();
            // We fetch the bigger photo
            return photos.stream()
                    .max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);
        }
        // Return null if not found
        return null;
    }

    public String getFilePath(PhotoSize photo) {
        Objects.requireNonNull(photo);
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(photo.getFileId());
        try {
            // We execute the method using AbsSender::execute method.
            File file = messageSender.execute(getFileMethod);
            // We now have the file_path
            return file.getFilePath();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public java.io.File downloadPhotoByFilePath(String filePath) {
        try {
            // Download the file calling AbsSender::downloadFile method
            return messageSender.downloadFile(filePath);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

}
