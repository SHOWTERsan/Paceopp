package ru.santurov.paceopp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.santurov.paceopp.DTO.EmailMessageDTO;
import ru.santurov.paceopp.services.EmailService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    private final EmailService emailService;

    @Autowired
    public MainController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String index(Model model) {
        if (!model.containsAttribute("emailMessage")) {
            model.addAttribute("emailMessage", new EmailMessageDTO());
        }
        return "index";
    }
    @GetMapping("/bad_request")
    public String badRequest() {
        return "bad_request";
    }
    @PostMapping("/contact")
    public String sendEmail(@ModelAttribute("emailMessage") @Valid EmailMessageDTO emailMessageDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.emailMessage", bindingResult);
            redirectAttributes.addFlashAttribute("emailMessage", emailMessageDTO);
            return "redirect:/";
        }

        emailService.sendMessage(emailMessageDTO.getSubject(), emailMessageDTO.getMessage());
        redirectAttributes.addFlashAttribute("showSuccessModal", true);

        return "redirect:/";
    }

}
