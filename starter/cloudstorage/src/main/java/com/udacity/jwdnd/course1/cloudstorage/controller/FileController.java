package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileController {
    private final UserService userService;
    private final FileService fileService;

    public FileController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

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

    @GetMapping("/files/{fileId}")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> getFile(Authentication authentication, @PathVariable Integer fileId) {
        User user = this.userService.getUser(authentication.getName());
        File file = this.fileService.getFile(user.getUserId(), fileId);
        ByteArrayResource resource = new ByteArrayResource(file.getFileData());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getFileName())
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(Long.parseLong(file.getFileSize()))
                .body(resource);
    }
}
