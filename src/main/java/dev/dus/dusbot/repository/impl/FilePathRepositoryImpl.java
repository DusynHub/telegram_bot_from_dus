package dev.dus.dusbot.repository.impl;

import dev.dus.dusbot.mapper.FilePathMapper;
import dev.dus.dusbot.model.FilePath;
import dev.dus.dusbot.model.Tag;
import dev.dus.dusbot.repository.FilePathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
                "INSERT INTO photo_path_win(file_path_prefix, storage_name, user_id, file_name) " +
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
                generatedKeys.next();
                insertedFilePathId = generatedKeys.getLong(1);
                filePath.setId(insertedFilePathId);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return insertedFilePathId;
    }


    @Override
    public Long addNewTag(Tag tag) {
        String sql =
                "INSERT INTO tags(tag) " +
                "VALUES(?) " +
                "ON CONFLICT (tag) DO UPDATE SET tag=tags.tag";

        Long insertedFilePathId = null;

        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
        ){
            pstmt.setString(1, tag.getTag());

            int affectedRows = pstmt.executeUpdate();

            if(affectedRows == 0){
                throw new SQLException("Creating file path failed, no rows affected.");
            }

            try(ResultSet generatedKeys = pstmt.getGeneratedKeys()){
                generatedKeys.next();
                insertedFilePathId = generatedKeys.getLong(1);
                tag.setId(insertedFilePathId);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return insertedFilePathId;
    }

    @Override
    public void addNewFilePathToTag(Long photoId, Long tagId) {
        String sql =
                "INSERT INTO photo_path_win_to_tags(photo_id, tag_id) " +
                "VALUES(?,?)";

        Long insertedFilePathId = null;

        try(
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
        ){
            pstmt.setLong(1, photoId);
            pstmt.setLong(2, tagId);

            int affectedRows = pstmt.executeUpdate();

            if(affectedRows == 0){
                throw new SQLException("Creating file path failed, no rows affected.");
            }

//            try(ResultSet generatedKeys = pstmt.getGeneratedKeys()){
//                generatedKeys.next();
//                insertedFilePathId = generatedKeys.getLong(1);
//                tag.setId(insertedFilePathId);
//            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<FilePath> getAllPhotosByUserId(long userId) {

        List<FilePath> result =  new ArrayList<>();

        String sql =
                "SELECT ppw.id " +
                        ", ppw.file_path_prefix" +
                        ", ppw.storage_name" +
                        ", ppw.user_id" +
                        ", ppw.file_name " +
                "FROM photo_path_win ppw " +
                "WHERE ppw.user_id = ? ";

        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
        ){
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                result.add(FilePathMapper.mapToFilePath(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    @Override
    public List<FilePath> getAllUserPhotosByTag(long userId, Object[] tags) {
        List<FilePath> result =  new ArrayList<>();
        int havingParam = tags.length;

        String sql =
                "SELECT    ppw.id " +
                        ", ppw.file_path_prefix" +
                        ", ppw.storage_name" +
                        ", ppw.user_id" +
                        ", ppw.file_name " +
                        ", t.tag " +
                "FROM photo_path_win ppw " +
                "INNER JOIN photo_path_win_to_tags ppwtt ON ppw.id = ppwtt.photo_id " +
                "INNER JOIN tags t ON ppwtt.tag_id = t.id " +
                "WHERE ppw.user_id = ? " +
                                    "AND t.tag = ANY (?) " +
                "GROUP BY  ppw.id " +
                        ", ppw.file_path_prefix" +
                        ", ppw.storage_name" +
                        ", ppw.user_id" +
                        ", ppw.file_name " +
                        ", t.tag " +
                "HAVING COUNT(ppw.id) = ? ";

        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
        ){
            Array tagsToInsert = connection.createArrayOf("text", tags);
            System.out.println(tagsToInsert.toString());
            pstmt.setLong(1, userId);
            pstmt.setArray(2, tagsToInsert);
            pstmt.setInt(3, havingParam);

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                result.add(FilePathMapper.mapToFilePath(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
