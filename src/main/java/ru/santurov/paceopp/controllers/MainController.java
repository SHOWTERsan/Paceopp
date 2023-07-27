package ru.santurov.paceopp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.santurov.paceopp.models.EmailMessage;
import ru.santurov.paceopp.serives.EmailService;

@Controller
public class MainController {
    private final EmailService emailService;

    @Autowired
    public MainController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String index(@ModelAttribute("EmailMessage") EmailMessage emailMessage) {
        return "index";
    }

    @PostMapping("/contact")
    public String sendEmail(@ModelAttribute("EmailMessage") @Valid EmailMessage emailMessage,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "index";

        emailService.sendMessage(emailMessage.getSubject(),emailMessage.getMessage());
        redirectAttributes.addFlashAttribute("showSuccessModal", true);

        return "redirect:/";
    }

}
