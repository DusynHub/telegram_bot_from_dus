package dev.dus.dusbot.commandHandlers;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
public class SavePhotoHandler extends BotCommand implements Handle {


    private final DefaultAbsSender sender;

    public SavePhotoHandler(DefaultAbsSender sender) {
        this.sender = sender;
    }

    public SavePhotoHandler(@NonNull String command, @NonNull String description, DefaultAbsSender sender) {
        super(command, description);
        this.sender = sender;
    }

    @Override
    public SendMessage handle(Message message) {
        long chatId = message.getChatId();
        User currentUser = message.getFrom();
        String savePhotoMessage = String.format("Send photo, %s, please.", currentUser.getFirstName());

        java.io.File f = downloadPhotoByFilePath(getFilePath(getPhoto(message)));
        long userId = message.getFrom().getId();
        String currentDate = message.getDate().toString();
        java.io.File localFile
                = new java.io.File("C:\\Users\\Kansa\\dev\\telegram_storage\\" + userId + "\\" + currentDate + ".png");
        try {
            FileUtils.copyInputStreamToFile(new FileInputStream(f), localFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getSendMessage(chatId, savePhotoMessage);
    }

    @Override
    public SendMessage handle(CallbackQuery query) {

        long chatId = query.getMessage().getChatId();
        User currentUser = query.getFrom();
        String savePhotoMessage = String.format("Send photo, %s, please.", currentUser.getFirstName());

        java.io.File f = downloadPhotoByFilePath(getFilePath(getPhoto(query.getMessage())));
        long userId = query.getFrom().getId();
        String currentDate = query.getMessage().getEditDate().toString();
        java.io.File localFile = new java.io.File("C:\\Users\\Kansa\\dev\\telegram_storage\\" + userId + "\\" + currentDate + ".png");
        try {
            FileUtils.copyInputStreamToFile(new FileInputStream(f), localFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getSendMessage(chatId, savePhotoMessage);
    }


    private SendMessage getSendMessage(long chatId, String startAnswer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(startAnswer);
        return sendMessage;
    }

    private PhotoSize getPhoto(Message message) {
        log.info("Extract photo in getPhoto(Message message) method");
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
            File file = sender.execute(getFileMethod);
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
            return sender.downloadFile(filePath);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
