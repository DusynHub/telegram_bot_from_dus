package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.model.FilePath;
import dev.dus.dusbot.model.Tag;
import dev.dus.dusbot.repository.FilePathRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.*;

@Component
@ComponentScan("dev")
@Slf4j
public class SavePhotoFromMessage extends Handler {

    @Value("${file.path.prefix}")
    private String filePathPrefix;

    @Value("${file.path.storage.name}")
    private String storageName;

    private final FilePathRepository filePathRepository;

    @Autowired
    public SavePhotoFromMessage(
            FilePathRepository filePathRepository
    ) {
        super(null, null, null);
        this.filePathRepository = filePathRepository;
        log.info("[{}]>>> {} bean has been created",
                this.getClass().getSimpleName(),
                this.getClass().getSimpleName());
    }

    public boolean handle(Update update, Map<Long, MenuState> userMenuState) {
        log.info("[{}]>>> {} request to check 'update'",
                this.getClass().getSimpleName(),
                this.getClass().getSimpleName());
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            User currentUser = message.getFrom();
            long userId = message.getFrom().getId();
            String caption = message.getCaption();

            if (userMenuState.getOrDefault(userId, MenuState.START) != MenuState.SAVE_PHOTO_MESSAGE) {
                log.info("[{}]>>> {} received photo in a wrong menu. Previous menu must be {}",
                        this.getClass().getSimpleName(),
                        this.getClass().getSimpleName(),
                        MenuState.SAVE_PHOTO_MESSAGE);
                return handleNext(update, userMenuState);
            }

            if (caption == null || !caption.startsWith("#")) {
                log.info("[{}]>>> {} received photo with wrong tags in caption",
                        this.getClass().getSimpleName(),
                        this.getClass().getSimpleName());
                String answer = "Tags in caption should start with '#' symbol. Pls try again " +
                        "Please send photo with tags in caption";
                try {
                    messageSender.execute(getSendMessage(chatId, answer));
                    menuSender.sendMenu(MenuType.BACK_TO_MAIN, chatId);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                log.info("[{}]>>> {} sent message to send photo with correct tags",
                        this.getClass().getSimpleName(),
                        this.getClass().getSimpleName());
                return false;
            }

            String currentDate = message.getDate().toString();
            java.io.File downloadedToBotChat = downloadPhotoByFilePath(getFilePath(getPhoto(message)));
            java.io.File dir
                    = new java.io.File(filePathPrefix + "\\" + storageName + "\\" + userId);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = currentDate + ".png";
            java.io.File localFile = new java.io.File(dir, fileName);

            String savePhotoMessage = String.format("Dear %s, your photo have been saved", currentUser.getFirstName());
            try (
                    FileInputStream downloadedToBotChatFileInputStream = new FileInputStream(downloadedToBotChat);
                    FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
                    BufferedInputStream bis = new BufferedInputStream(downloadedToBotChatFileInputStream);
                    BufferedOutputStream bos = new BufferedOutputStream(localFileOutputStream);
            ) {
                int byteReaderCounter = 0;
                while (byteReaderCounter != -1) {
                    byteReaderCounter = bis.read();
                    bos.write(byteReaderCounter);
                }
                //FileUtils.copyInputStreamToFile(new FileInputStream(downloadedToBotChat), localFile);
                userMenuState.put(currentUser.getId(), MenuState.SAVE_PHOTO);
                Long insertedPhotoId = filePathRepository.addNewPhoto(new FilePath(
                        null,
                        filePathPrefix,
                        storageName,
                        userId,
                        fileName
                ));

                String[] tags = Arrays.copyOfRange(caption.split("#"), 1, caption.split("#").length);
                List<Long> tagIds = new ArrayList<>();
                for (String tag : tags) {
                    tagIds.add(filePathRepository.addNewTag(new Tag(null, tag)));
                }

                for (Long tagId : tagIds) {
                    filePathRepository.addNewFilePathToTag(insertedPhotoId, tagId);
                }

                messageSender.execute(getSendMessage(chatId, savePhotoMessage));
                menuSender.sendMenu(MenuType.MAIN, chatId);

                log.info("[{}]>>> {} photo with tags: {} has been saved",
                        this.getClass().getSimpleName(),
                        this.getClass().getSimpleName(),
                        Arrays.toString(tags));

            } catch (IOException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        log.info("[{}]>>> requested method handleNext(update,  userMenuState)",
                this.getClass().getSimpleName());
        return handleNext(update, userMenuState);
    }

    private SendMessage getSendMessage(long chatId, String startAnswer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(startAnswer);
        return sendMessage;
    }

    private PhotoSize getPhoto(Message message) {
        log.info("[{}]>>> requested method getPhoto(Message message)",
                this.getClass().getSimpleName());
        if (message.hasPhoto()) {
            List<PhotoSize> photos = message.getPhoto();
            return photos.stream()
                    .max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);
        }
        return null;
    }

    public String getFilePath(PhotoSize photo) {
        log.info("[{}]>>> requested method getFilePath(PhotoSize photo)",
                this.getClass().getSimpleName());
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
        log.info("[{}]>>> requested method downloadPhotoByFilePath(String filePath)",
                this.getClass().getSimpleName());
        try {
            return messageSender.downloadFile(filePath);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
