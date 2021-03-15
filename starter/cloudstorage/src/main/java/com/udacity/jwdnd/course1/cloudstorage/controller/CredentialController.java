package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CredentialController {
    private final EncryptionService encryptionService;
    private final UserService userService;
    private final CredentialService credentialService;

    public CredentialController(EncryptionService encryptionService, UserService userService, CredentialService credentialService) {
        this.encryptionService = encryptionService;
        this.userService = userService;
        this.credentialService = credentialService;
    }

    @PostMapping("/credentials")
    public String addCredential(Authentication authentication, @ModelAttribute Credential credential, Model model) {
        User user = this.userService.getUser(authentication.getName());
        credential.setUserId(user.getUserId());
        credential.setKey(this.encryptionService.getRandomKey());
        credential.setPassword(this.encryptionService.encryptValue(credential.getPassword(), credential.getKey()));

        if (credential.getCredentialId() == null)
            this.credentialService.addCredential(credential);
        else
            this.credentialService.updateCredential(credential);

        model.addAttribute("success", true);
        return "result";
    }

    @GetMapping("/credentials/delete/{credentialId}")
    public String deleteNote(Authentication authentication, @PathVariable Integer credentialId, Model model) {
        User user = this.userService.getUser(authentication.getName());
        this.credentialService.deleteCredential(credentialId, user.getUserId());

        model.addAttribute("success", true);
        return "result";
    }
}
