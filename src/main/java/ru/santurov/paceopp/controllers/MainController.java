package ru.santurov.paceopp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.santurov.paceopp.DTO.EmailMessageDTO;
import ru.santurov.paceopp.DTO.KitDTO;
import ru.santurov.paceopp.models.Kit;
import ru.santurov.paceopp.security.UserDetails;
import ru.santurov.paceopp.services.*;

import java.util.Base64;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final ServiceManagementService serviceManagementService;
    private final ServiceItemService serviceItemService;
    private final KitService kitService;

    @GetMapping("/")
    public String index(Model model) {
        if (!model.containsAttribute("emailMessage")) {
            model.addAttribute("emailMessage", new EmailMessageDTO());
        }
        List<KitDTO> kits = kitService.findAll()
                .stream()
                .map(this::toKitDTO)
                .toList();
        model.addAttribute("kits", kits);
        model.addAttribute("services", serviceManagementService.findAll());
        model.addAttribute("items", serviceItemService.findAll());

        return "index";
    }

    private KitDTO toKitDTO(Kit futureKit) {
        KitDTO kitDTO = modelMapper.map(futureKit, KitDTO.class);
        if (futureKit.getImage() != null && futureKit.getImage().getData() != null) {
            String base64Encoded = Base64.getEncoder().encodeToString(futureKit.getImage().getData());
            kitDTO.setImage(base64Encoded);
        } else {
            kitDTO.setImage(null);
        }
        return kitDTO;
    }

    @GetMapping("/bad_request")
    public String badRequest() {
        return "bad_request";
    }

    @PostMapping("/contact")
    public String sendEmail(@AuthenticationPrincipal UserDetails userDetails,
                            @ModelAttribute("emailMessage") @Valid EmailMessageDTO emailMessageDTO,
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
