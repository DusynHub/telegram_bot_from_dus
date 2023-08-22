package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.model.FilePath;
import dev.dus.dusbot.repository.FilePathRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GetUsersPhotoByTagFromMessage extends Handler {

    private final FilePathRepository filePathRepository;

    @Autowired
    public GetUsersPhotoByTagFromMessage(
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

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            User currentUser = message.getFrom();
            long userId = message.getFrom().getId();
            String[] tags = message.getText().split(" ");

            if (userMenuState.getOrDefault(userId, MenuState.START) != MenuState.GET_PHOTO_BY_TAGS_MESSAGE) {
                log.info("[{}]>>> {} received photo in a wrong menu. Previous menu must be {}",
                        this.getClass().getSimpleName(),
                        this.getClass().getSimpleName(),
                        MenuState.GET_PHOTO_BY_TAGS_MESSAGE);
                return handleNext(update, userMenuState);
            }

            try {
                List<FilePath> userPhotos = filePathRepository.getAllUserPhotosByTag(userId, tags);
                for (FilePath photo : userPhotos) {
                    String path = photo.getPathInString();
                    File photoFile = new File(path);
                    InputFile inputFile = new InputFile(photoFile, "photo");
                    SendPhoto sendPhotoMessage = new SendPhoto();
                    sendPhotoMessage.setPhoto(inputFile);
                    sendPhotoMessage.setChatId(chatId);
                    messageSender.execute(sendPhotoMessage);
                }
                menuSender.sendMenu(MenuType.MAIN, chatId);
                userMenuState.put(currentUser.getId(), MenuState.START);

                log.info("[{}]>>> {} photo(s) with tags: {} have been sent",
                        this.getClass().getSimpleName(),
                        this.getClass().getSimpleName(),
                        Arrays.toString(tags));

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

            return false;

        }
        log.info("[{}]>>> requested method handleNext(update,  userMenuState)", this.getClass().getSimpleName());
        return handleNext(update, userMenuState);
    }
}
