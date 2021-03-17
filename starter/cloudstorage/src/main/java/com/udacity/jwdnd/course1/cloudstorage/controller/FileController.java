package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {
    private final UserService userService;
    private final FileService fileService;

    public FileController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    // Create file
    @PostMapping("/files")
    public String addFile(Authentication authentication, @RequestParam("fileUpload") MultipartFile fileUpload, Model model){
        User user = this.userService.getUser(authentication.getName());
        Integer fileId = this.fileService.addFile(user.getUserId(), fileUpload);
        switch (fileId) {
            case -1:
                model.addAttribute("error", true);
                break;
            case -2:
                model.addAttribute("errorMessage", "Duplicate file.");
                break;
            default:
                model.addAttribute("success", true);
        }

        return "result";
    }

    // Download file
    @GetMapping("/files/{fileId}")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> getFile(Authentication authentication, @PathVariable Integer fileId) {
        User user = this.userService.getUser(authentication.getName());
        File file = this.fileService.getFile(fileId, user.getUserId());
        ByteArrayResource resource = new ByteArrayResource(file.getFileData());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getFileName())
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(Long.parseLong(file.getFileSize()))
                .body(resource);
    }

    // Delete file
    @GetMapping("/files/delete/{fileId}")
    public String deleteFile(Authentication authentication, @PathVariable Integer fileId, Model model) {
        User user = this.userService.getUser(authentication.getName());
        this.fileService.deleteFile(fileId, user.getUserId());

        model.addAttribute("success", true);
        return "result";
    }
}
