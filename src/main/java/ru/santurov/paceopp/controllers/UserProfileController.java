package ru.santurov.paceopp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.santurov.paceopp.DTO.UserProfileDTO;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.services.EmailService;
import ru.santurov.paceopp.services.UserService;

import java.util.HashMap;
import java.util.Map;
//TODO add anti spam
@Controller
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final EmailService emailService;

    @GetMapping("/user/profile")
    public String getProfile(@AuthenticationPrincipal(expression = "user") User user, Model model) {
        if (user == null) {
            return "redirect:/auth/signin";
        }

        User updatedUser = userService.findById(user.getId()).get();

        if (!model.containsAttribute("userProfile")) {
            UserProfileDTO userProfileDTO = new UserProfileDTO();
            userProfileDTO.setUserName(updatedUser.getUsername());
            userProfileDTO.setEmail(updatedUser.getEmail());
            model.addAttribute("userProfile", userProfileDTO);
        }
        return "profile";
    }

    @PostMapping("/user/profile/email")
    public ResponseEntity<Map<String, String>> updateEmail(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestParam(name = "newEmail") String newEmail) {

        Map<String, String> response = new HashMap<>();

        if (newEmail == null || newEmail.isEmpty()) {
            response.put("email", "Почту нужно ввести обязательно");
            return ResponseEntity.badRequest().body(response);
        }

        if (!newEmail.matches("^[\\w\\-.]+@([\\w-]+\\.)+[\\w-]{2,}$")) {
            response.put("email", "Неверный формат почты");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.findByEmail(newEmail).isPresent()) {
            response.put("email", "Почта уже используется");
            return ResponseEntity.badRequest().body(response);
        }

        user.setTempEmail(newEmail);
        userService.save(user);
        emailService.sendValidateMessage(user);
        response.put("uuid", user.getUuid());
        return ResponseEntity.ok(response);
    }



    @PostMapping("/user/profile/username")
    public ResponseEntity<Map<String, String>> updateUsername(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestParam(name = "newUsername") String newUsername) {

        Map<String, String> response = new HashMap<>();

        if (newUsername == null || newUsername.isEmpty()) {
            response.put("username", "Имя пользователя обязательно для ввода");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.findByUsername(newUsername).isPresent()) {
            response.put("username", "Имя пользователя уже используется");
            return ResponseEntity.badRequest().body(response);
        }

        user.setUsername(newUsername);
        userService.save(user);

        response.put("message", "Имя пользователя успешно обновлено");
        return ResponseEntity.ok(response);
    }

}
