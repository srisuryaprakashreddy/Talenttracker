package com.example.Talenttracker.exception;

import com.example.Talenttracker.controller.PageController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = PageController.class)
public class UiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(Model model, ResourceNotFoundException ex) {
        model.addAttribute("pageTitle", "Not Found — TalentTrack Lite");
        model.addAttribute("contentFragment", "pages/error :: content");
        model.addAttribute("errorMessage", ex.getMessage());
        return "layouts/main";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Model model, Exception ex) {
        model.addAttribute("pageTitle", "Error — TalentTrack Lite");
        model.addAttribute("contentFragment", "pages/error :: content");
        model.addAttribute("errorMessage", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.");
        return "layouts/main";
    }
}
