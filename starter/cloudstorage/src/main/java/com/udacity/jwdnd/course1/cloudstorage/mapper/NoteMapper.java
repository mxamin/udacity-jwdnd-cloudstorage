package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoteMapper {
    @Select("SELECT noteid, userid, notetitle, notedescription FROM NOTES WHERE userid = #{userId}")
    List<Note> getNotes(Integer userId);

    @Insert("INSERT INTO NOTES (userid, notetitle, notedescription) VALUES (#{userId}, #{noteTitle}, #{noteDescription})")
    @Options(useGeneratedKeys = true, keyProperty = "noteId")
    Integer addNote(Note note);

    @Delete("DELETE FROM NOTES WHERE noteid = #{noteId}")
    void deleteNote(Integer noteId);
}
