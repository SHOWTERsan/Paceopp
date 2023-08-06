package ru.santurov.paceopp.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.models.VerificationToken;
import ru.santurov.paceopp.serives.EmailService;
import ru.santurov.paceopp.serives.SignupService;
import ru.santurov.paceopp.serives.TokenService;
import ru.santurov.paceopp.serives.UserService;
import ru.santurov.paceopp.utils.UserValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final UserValidator userValidator;
    private final SignupService signupService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public AuthController(UserService userService, UserValidator userValidator, SignupService signupService, EmailService emailService, TokenService tokenService) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.signupService = signupService;
        this.emailService = emailService;
        this.tokenService = tokenService;
    }

    @GetMapping("/signin")
    public String loginPage() {
        return "authentication/signin";
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
        String token = emailService.sendValidateMessage(user);

        session.setAttribute("canAccessWaitingForVerification", true);

        return "redirect:/auth/waiting_for_verification?token=" + token;
    }

    @GetMapping("/forgot_password")
    public String forgotPasswordPage() {
        return "authentication/forgot_password";
    }

    @PostMapping("/forgot_password")
    public String forgotPasswordProcess(@RequestParam("email") String email) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            emailService.sendResetPasswordMessage(user);
        }
        return "redirect:/auth/password_reset_sent";
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("token") Optional<String> token) {
        if (token.isEmpty()) return "bad_request";

        Optional<VerificationToken> optionalToken = tokenService.findByToken(token.get());
        if (optionalToken.isPresent()) {
            VerificationToken verificationToken = optionalToken.get();
            User user = verificationToken.getUser();

            if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                userService.delete(user);
                return "authentication/verification_expired";
            }

            user.setVerified(true);
            scheduler.schedule(() -> tokenService.delete(verificationToken), 5100, TimeUnit.MILLISECONDS);
            userService.save(user);

            return "authentication/confirm";
        }
        else {
            return "bad_request";
        }
    }

    @GetMapping("/waiting_for_verification")
    public String waitingForVerification(@RequestParam("token") Optional<String> token,
                                         Model model,
                                         HttpSession session) {
        if (session.getAttribute("canAccessWaitingForVerification") == null ||
                token.isEmpty()) throw new AccessDeniedException("Access denied!");

        session.removeAttribute("canAccessWaitingForVerification");
        Optional<VerificationToken> optionalToken = tokenService.findByToken(token.get());
        if (optionalToken.isPresent()) {
            model.addAttribute("token", token.get());
            return "authentication/waiting_for_verification";
        }
        else {
            //TODO Page
            return "redirect:/auth/signup";
        }
    }

    @GetMapping("/check_verification")
    @ResponseBody
    public ResponseEntity<Map<String, String>> checkVerification(@RequestParam("token") String token) {
        Optional<VerificationToken> optionalToken = tokenService.findByToken(token);

        if (optionalToken.isPresent()) {
            VerificationToken vtoken = optionalToken.get();
            if (vtoken.getExpiryDate().isBefore(LocalDateTime.now()))
            {
                userService.delete(vtoken.getUser());
                return ResponseEntity.ok(Collections.singletonMap("status", "expired"));
            }
            else
            if (vtoken.getUser().isVerified())
                return ResponseEntity.ok(Collections.singletonMap("status", "verified"));
        }

        return ResponseEntity.ok(Collections.singletonMap("status", "not_verified"));
    }
}
