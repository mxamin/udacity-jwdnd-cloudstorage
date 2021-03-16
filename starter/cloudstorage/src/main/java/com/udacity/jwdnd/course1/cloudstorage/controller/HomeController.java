package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/home")
public class HomeController {
    private final EncryptionService encryptionService;
    private final UserService userService;
    private final FileService fileService;
    private final NoteService noteService;
    private final CredentialService credentialService;

    public HomeController(EncryptionService encryptionService, UserService userService, FileService fileService, NoteService noteService, CredentialService credentialService) {
        this.encryptionService = encryptionService;
        this.userService = userService;
        this.fileService = fileService;
        this.noteService = noteService;
        this.credentialService = credentialService;
    }

    @GetMapping
    public String homeView(Authentication authentication, Model model) {

        User user = this.userService.getUser(authentication.getName());

        List<Credential> credentials = this.credentialService.getCredentials(user.getUserId());
        for (Credential credential: credentials) {
            credential.setPlainPassword(this.encryptionService.decryptValue(credential.getPassword(), credential.getKey()));
        }

        model.addAttribute("files", this.fileService.getFiles(user.getUserId()));
        model.addAttribute("notes", this.noteService.getNotes(user.getUserId()));
        model.addAttribute("credentials", credentials);
        return "home";
    }
}
