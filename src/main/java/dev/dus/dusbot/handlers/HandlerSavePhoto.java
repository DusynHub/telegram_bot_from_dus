package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.menuSenders.MenuSender;
import dev.dus.dusbot.model.FilePath;
import dev.dus.dusbot.repository.FilePathRepository;
import dev.dus.dusbot.service.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
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

@Component("handler_chain_link_5")
@ComponentScan("dev")
public class HandlerSavePhoto extends Handler {

    @Value("${file.path.prefix}")
    private String filePathPrefix;

    @Value("${file.path.storage.name}")
    private String storageName;

    private final FilePathRepository filePathRepository;

    @Autowired
    public HandlerSavePhoto(
            @Lazy TelegramBot messageSender,
            @Qualifier("main_menu") MenuSender menuSender,
            @Qualifier("handler_chain_link_6") Handler next, FilePathRepository filePathRepository) {
        super(messageSender, menuSender, next);
        this.filePathRepository = filePathRepository;
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

            String caption = message.getCaption();
            if(!caption.startsWith("#")){
                String answer = "Tags in caption should start with '#' symbol. Pls try again";
                try {
                    messageSender.execute(getSendMessage(chatId, answer));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            String currentDate = message.getDate().toString();
            java.io.File downloadedToBotChat = downloadPhotoByFilePath(getFilePath(getPhoto(message)));

            java.io.File dir
                    = new java.io.File(filePathPrefix + "\\" + storageName +"\\" + userId);
            if(!dir.exists()){
                dir.mkdirs();
            }

            String fileName = currentDate + ".png";
            java.io.File localFile = new java.io.File( dir,fileName);

            String savePhotoMessage = String.format("Dear %s, your photo have been saved", currentUser.getFirstName() );
            try(
                    FileInputStream downloadedToBotChatFileInputStream = new FileInputStream(downloadedToBotChat);
                    FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
                    BufferedInputStream bis = new BufferedInputStream(downloadedToBotChatFileInputStream);
                    BufferedOutputStream bos = new BufferedOutputStream(localFileOutputStream);
            ) {
                int byteReaderCounter = 0;
                while (byteReaderCounter != -1){
                    byteReaderCounter = bis.read();
                    bos.write(byteReaderCounter);
                }
                //FileUtils.copyInputStreamToFile(new FileInputStream(downloadedToBotChat), localFile);
                messageSender.execute(getSendMessage(chatId, savePhotoMessage));
                menuSender.sendMenu(MenuType.MAIN, chatId);
                userMenuState.put(currentUser.getId(), MenuState.SAVE_PHOTO);
                Long insertedId = filePathRepository.addNewPhoto(new FilePath(
                        null,
                        filePathPrefix,
                        storageName,
                        userId,
                        fileName
                ));
                System.out.println(insertedId.longValue());
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
        if (message.hasPhoto()) {
            List<PhotoSize> photos = message.getPhoto();
            return photos.stream()
                    .max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);
        }
        return null;
    }

    public String getFilePath(PhotoSize photo) {
        Objects.requireNonNull(photo);
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(photo.getFileId());
        try {
            File file = messageSender.execute(getFileMethod);
            return file.getFilePath();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public java.io.File downloadPhotoByFilePath(String filePath) {
        try {
            return messageSender.downloadFile(filePath);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
