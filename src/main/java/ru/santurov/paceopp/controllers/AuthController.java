package ru.santurov.paceopp.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.repositories.UserRepository;
import ru.santurov.paceopp.serives.EmailService;
import ru.santurov.paceopp.serives.SignupService;
import ru.santurov.paceopp.utils.UserValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final SignupService signupService;
    private final EmailService emailService;

    @Autowired
    public AuthController(UserRepository userRepository, UserValidator userValidator, SignupService signupService, EmailService emailService) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.signupService = signupService;
        this.emailService = emailService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "authentication/login";
    }

    @GetMapping("/signup")
    public String signUpPage(@ModelAttribute("user")User user) {
        return "authentication/signup";
    }
    @GetMapping("verificationExpired")
    public String verificationExpired() {
        return "authentication/verification_expired";
    }

    @PostMapping("/signup")
    public String signupProcess(@ModelAttribute("user") @Valid User user,
                                BindingResult bindingResult, HttpSession session) {
        userValidator.validate(user,bindingResult);
        if (bindingResult.hasErrors()) return "authentication/signup";

        signupService.createUser(user);
        emailService.sendValidateMessage(user.getEmail(), user.getUuid());

        session.setAttribute("canAccessWaitingForVerification", true);

        return "redirect:/auth/waiting_for_verification?uuid=" + user.getUuid();
    }

    @GetMapping("/confirm")
    public ResponseEntity<Void> confirmEmail(@RequestParam("uuid") String uuid) {
        Optional<User> user = userRepository.findByUuid(uuid);
        if (user.isPresent()) {
            user.get().setVerified(true);
            userRepository.save(user.get());

            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/waiting_for_verification")
    public String waitingForVerification(@RequestParam("uuid") Optional<String> uuid,
                                         Model model,
                                         HttpSession session) {
        if (session.getAttribute("canAccessWaitingForVerification") == null ||
                uuid.isEmpty()) {
            return "redirect:/auth/login";
        }
        session.removeAttribute("canAccessWaitingForVerification");
        Optional<User> user = userRepository.findByUuid(uuid.get());
        if (user.isPresent()) {
            model.addAttribute("uuid", uuid.get());
            return "authentication/waiting_for_verification";
        }
        else {
            return "redirect:/auth/signup";
        }
    }

    @GetMapping("/check_verification")
    @ResponseBody
    public ResponseEntity<Map<String, String>> checkVerification(@RequestParam("uuid") String uuid) {
        Optional<User> userOptional = userRepository.findByUuid(uuid);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getVerificationExpireTime().isBefore(LocalDateTime.now()))
            {
                userRepository.delete(user);
                return ResponseEntity.ok(Collections.singletonMap("status", "expired"));
            }
            else
            if (userOptional.get().isVerified())
                return ResponseEntity.ok(Collections.singletonMap("status", "verified"));
        }

        return ResponseEntity.ok(Collections.singletonMap("status", "not_verified"));
    }

}
