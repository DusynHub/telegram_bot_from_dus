package dev.dus.dusbot.mapper;

import dev.dus.dusbot.model.FilePath;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilePathMapper {

    public static FilePath mapToFilePath(ResultSet rs) throws SQLException {
        return new FilePath(
                rs.getLong("id"),
                rs.getString("file_path_prefix"),
                rs.getString("storage_name"),
                rs.getLong("user_id"),
                rs.getString("file_name")
        );
    }
}
