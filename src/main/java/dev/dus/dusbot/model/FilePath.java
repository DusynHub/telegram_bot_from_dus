package dev.dus.dusbot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class FilePath {

    private  Long id;

    private  String filePathPrefix;

    private  String storageName;

    private  long userId;

    private  String fileName;

    public FilePath(Long id, String filePathPrefix, String storageName, long userId, String fileName) {
        this.id = id;
        this.filePathPrefix = filePathPrefix;
        this.storageName = storageName;
        this.userId = userId;
        this.fileName = fileName;
    }

    public String getPathInString(){
        return filePathPrefix + "\\" + storageName + "\\" + userId  + "\\" + fileName;
    }
}
