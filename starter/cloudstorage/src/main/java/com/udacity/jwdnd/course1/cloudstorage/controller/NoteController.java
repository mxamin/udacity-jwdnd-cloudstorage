package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NoteController {
    private final UserService userService;
    private final NoteService noteService;

    public NoteController(UserService userService, NoteService noteService) {
        this.userService = userService;
        this.noteService = noteService;
    }

    @PostMapping("/notes")
    public String addNote(Authentication authentication, @ModelAttribute Note note, Model model) {
        User user = this.userService.getUser(authentication.getName());
        note.setUserId(user.getUserId());
        if (note.getNoteId() == null)
            this.noteService.addNote(note);
        else
            this.noteService.updateNote(note);

        model.addAttribute("success", true);
        return "result";
    }

    @GetMapping("/notes/delete/{noteId}")
    public String deleteNote(Authentication authentication, @PathVariable Integer noteId, Model model) {
        User user = this.userService.getUser(authentication.getName());
        this.noteService.deleteNote(noteId, user.getUserId());

        model.addAttribute("success", true);
        return "result";
    }
}
