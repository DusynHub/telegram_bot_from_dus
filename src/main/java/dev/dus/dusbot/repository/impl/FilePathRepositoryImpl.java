package dev.dus.dusbot.repository.impl;

import dev.dus.dusbot.model.FilePath;
import dev.dus.dusbot.repository.FilePathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class FilePathRepositoryImpl implements FilePathRepository {


    private final DataSource dataSource;

    @Autowired
    public FilePathRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Long addNewPhoto(FilePath filePath) {
        String sql =
                "INSERT INTO file_path(file_path_prefix, storage_name, user_id, file_name) " +
                "VALUES(?, ?, ?, ?)";

        Long insertedFilePathId = null;

        try(
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql,
                                            Statement.RETURN_GENERATED_KEYS);
        ){
            pstmt.setString(1, filePath.getFilePathPrefix());
            pstmt.setString(2, filePath.getStorageName());
            pstmt.setLong(3, filePath.getUserId());
            pstmt.setString(4, filePath.getFileName());

            int affectedRows = pstmt.executeUpdate();

            if(affectedRows == 0){
                throw new SQLException("Creating file path failed, no rows affected.");
            }

            try(ResultSet generatedKeys = pstmt.getGeneratedKeys()){
                insertedFilePathId = generatedKeys.getLong(1);
                filePath.setId(insertedFilePathId);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return insertedFilePathId;
    }

    @Override
    public List<FilePath> getAllPhotosByUserId(long userId) {
        return null;
    }
}
