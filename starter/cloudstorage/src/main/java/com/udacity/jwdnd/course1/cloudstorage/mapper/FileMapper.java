package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {
    @Select("SELECT fileid, userid, filename, contenttype, filesize, filedata FROM FILES WHERE userid = #{userId}")
    List<File> getFiles(Integer userId);

    @Select("SELECT fileid, userid, filename, contenttype, filesize, filedata FROM FILES WHERE fileid = #{fileId} AND userid = #{userId}")
    File getFile(Integer fileId, Integer userId);

    @Select("SELECT fileid, userid, filename, contenttype, filesize, filedata FROM FILES WHERE userid = #{userId} AND filename = #{fileName}")
    File getFileByFileName(Integer userId, String fileName);


    @Insert("INSERT INTO FILES (userid, filename, contenttype, filesize, filedata) VALUES (#{userId}, #{fileName}, #{contentType}, #{fileSize}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    Integer addFile(File file);

    @Delete("DELETE FROM FILES WHERE fileid = #{fileId}")
    void deleteFile(Integer fileId);
}
