package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {
    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public List<File> getFiles(Integer userId) {
        return this.fileMapper.getFiles(userId);
    }

    public File getFile(Integer fileId, Integer userId) {
        return this.fileMapper.getFile(fileId, userId);
    }

    public boolean isDuplicateFileName(Integer userId, String fileName) {
        return fileMapper.getFileByFileName(userId, fileName) != null;
    }

    public Integer addFile(Integer userId, MultipartFile fileUpload) {
        // Duplicate filename check
        Long fileSize = fileUpload.getSize();
        String fileName = fileUpload.getOriginalFilename();

        if (fileSize == 0 && fileName.equals(""))
            // Empty file
            return -1;

        if (this.isDuplicateFileName(userId, fileName))
            // Duplicate file
            return -2;

        byte[] fileData;
        try{
            fileData = fileUpload.getBytes();
        } catch (IOException e) {
            return -1;
        }

        File file = new File(null, userId, fileName, fileUpload.getContentType(), Long.toString(fileSize), fileData);
        return this.fileMapper.addFile(file);
    }

    public void deleteFile(Integer fileId, Integer userId) {
        this.fileMapper.deleteFile(fileId, userId);
    }
}
