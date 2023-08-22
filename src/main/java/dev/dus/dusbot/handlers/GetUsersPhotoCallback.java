package dev.dus.dusbot.handlers;

import dev.dus.dusbot.enums.MenuState;
import dev.dus.dusbot.enums.MenuType;
import dev.dus.dusbot.model.FilePath;
import dev.dus.dusbot.repository.FilePathRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GetUsersPhotoCallback extends Handler {

    private final FilePathRepository filePathRepository;

    @Autowired
    public GetUsersPhotoCallback(
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

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (callbackQuery.getData().equals("GET_USERS_PHOTO")) {
                long chatId = callbackQuery.getMessage().getChatId();
                User currentUser = callbackQuery.getFrom();
                try {

                    List<FilePath> userPhotos = filePathRepository.getAllPhotosByUserId(currentUser.getId());
                    for (FilePath photo: userPhotos) {
                        String path = photo.getPathInString();
                        File photoFile =  new File(path);
                        InputFile inputFile =  new InputFile(photoFile, "photo");
                        SendPhoto message = new SendPhoto();
                        message.setPhoto(inputFile);
                        message.setChatId(chatId);
                        messageSender.execute(message);
                    }
                    menuSender.sendMenu(MenuType.MAIN, chatId);
                    userMenuState.put(currentUser.getId(), MenuState.SAVE_PHOTO_MESSAGE);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }
        }
        log.info("[{}]>>> requested method handleNext(update,  userMenuState)", this.getClass().getSimpleName());
        return handleNext(update, userMenuState);
    }
}
