package ru.santurov.paceopp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.santurov.paceopp.DTO.UserProfileDTO;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.services.UserService;
import ru.santurov.paceopp.utils.UserProfileValidator;

@Controller
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final UserProfileValidator userProfileValidator;

    @GetMapping("/user/profile")
    public String getProfile(@AuthenticationPrincipal(expression = "user") User user, Model model) {
        if (user == null) {
            return "redirect:/auth/signin";
        }

        if (!model.containsAttribute("userProfile")) {
            UserProfileDTO userProfileDTO = new UserProfileDTO();
            userProfileDTO.setUserName(user.getUsername());
            userProfileDTO.setEmail(user.getEmail());
            model.addAttribute("userProfile", userProfileDTO);
        }
        return "profile";
    }

    @PostMapping("/user/profile")
    public String updateProfile(@AuthenticationPrincipal(expression = "user") User user,
                                @ModelAttribute("userProfile") @Validated UserProfileDTO userProfileDTO,
                                BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        userProfileValidator.validate(userProfileDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userProfile", bindingResult);
            redirectAttributes.addFlashAttribute("userProfile", userProfileDTO);
            return "redirect:/user/profile";
        }
        userService.updateUserProfile(user, userProfileDTO);
        return "redirect:/user/profile?success";
    }
}
