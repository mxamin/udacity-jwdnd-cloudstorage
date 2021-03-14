package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/home")
public class HomeController {
    private final UserService userService;
    private final NoteService noteService;
    private final CredentialService credentialService;

    public HomeController(UserService userService, NoteService noteService, CredentialService credentialService) {
        this.userService = userService;
        this.noteService = noteService;
        this.credentialService = credentialService;

        // TODO: remove it when you're done testing
        this.prepareData();
    }

    @GetMapping
    public String homeView(Authentication authentication, HttpServletRequest request, Model model) {
        String tab = request.getParameter("tab");
        if (tab == null)
            tab = "files";  // Default tab

        User user = this.userService.getUser(authentication.getName());
        model.addAttribute("notes", this.noteService.getNotes(user.getUserId()));
        model.addAttribute("credentials", this.credentialService.getCredentials(user.getUserId()));
        model.addAttribute("tab", tab);
        return "home";
    }

    private void prepareData() {
        User user = new User(null, "mxamin", null, "test1234", "Amin", "Solhizadeh");
        this.userService.addUser(user);
    }
}
