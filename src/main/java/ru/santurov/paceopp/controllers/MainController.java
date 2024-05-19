package ru.santurov.paceopp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.santurov.paceopp.DTO.DrumkitDTO;
import ru.santurov.paceopp.DTO.EmailMessageDTO;
import ru.santurov.paceopp.models.Drumkit;
import ru.santurov.paceopp.services.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final DrumkitService drumkitService;
    private final ServiceManagementService serviceManagementService;
    private final ServiceItemService serviceItemService;

    @GetMapping("/")
    public String index(Model model) {
        if (!model.containsAttribute("emailMessage")) {
            model.addAttribute("emailMessage", new EmailMessageDTO());
        }
        List<DrumkitDTO> drumkits = drumkitService.findAll()
                .stream()
                .map(this::toDrumkitDTO)
                .toList();
        model.addAttribute("drumkits", drumkits);
        model.addAttribute("services", serviceManagementService.findAll());
        model.addAttribute("items", serviceItemService.findAll());

        return "index";
    }

    private DrumkitDTO toDrumkitDTO(Drumkit drumkit) {
        return modelMapper.map(drumkit, DrumkitDTO.class);
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
            return "redirect:/#write-to-us";
        }

        emailService.sendMessage(emailMessageDTO.getSubject(), emailMessageDTO.getMessage());
        redirectAttributes.addFlashAttribute("showSuccessModal", true);

        return "redirect:/#write-to-us";
    }

}
