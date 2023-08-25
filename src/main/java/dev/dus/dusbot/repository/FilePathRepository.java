package dev.dus.dusbot.repository;


import dev.dus.dusbot.model.FilePath;
import dev.dus.dusbot.model.Tag;

import java.util.List;

public interface FilePathRepository {

    Long addNewPhoto(FilePath filePath);

    Long addNewTag(Tag tag);

    void addNewFilePathToTag(Long photoId, Long tagId);

    List<FilePath> getAllPhotosByUserId(long userId);

    List<FilePath> getAllUserPhotosByTag(long userId, Object[] tags);
}
