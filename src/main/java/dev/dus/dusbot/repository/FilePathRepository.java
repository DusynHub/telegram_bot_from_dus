package dev.dus.dusbot.repository;


import dev.dus.dusbot.model.FilePath;

import java.util.List;

public interface FilePathRepository {

    Long addNewPhoto(FilePath filePath);

    List<FilePath> getAllPhotosByUserId(long userId);
}
