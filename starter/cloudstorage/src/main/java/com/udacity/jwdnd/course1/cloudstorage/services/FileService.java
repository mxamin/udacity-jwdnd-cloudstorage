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

    public File getFile(Integer userId, Integer fileId) {
        return this.fileMapper.getFile(userId, fileId);
    }

    public boolean isDuplicateFileName(Integer userId, String fileName) {
        return fileMapper.getFileByFileName(userId, fileName) != null;
    }

    public Integer addFile(Integer userId, MultipartFile fileUpload) {
        // Duplicate filename check
        Long fileSize = fileUpload.getSize();
        String fileName = fileUpload.getOriginalFilename();

        if (fileSize == 0 && fileName == "")
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
}
