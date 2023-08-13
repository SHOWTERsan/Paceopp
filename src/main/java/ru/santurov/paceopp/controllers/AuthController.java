package ru.santurov.paceopp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.santurov.paceopp.DTO.ResetPasswordRequestDTO;
import ru.santurov.paceopp.models.TokenType;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.models.VerificationToken;
import ru.santurov.paceopp.services.*;
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
    public String signInPage(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("error") != null) {
            model.addAttribute("error", request.getSession().getAttribute("error"));
            request.getSession().removeAttribute("error");
        }
        return "authentication/signin";
    }

    @GetMapping("/signup")
    public String signUpPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "authentication/signup";
    }
    @GetMapping("verificationExpired")
    public String verificationExpired() {
        return "authentication/verification_expired";
    }

    @GetMapping("/bad_request")
    public String badRequest() {
        return "bad_request";
    }

    @PostMapping("/signup_process")
    public String signupProcess(@ModelAttribute("user") @Valid User user,
                                BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                HttpSession session) {
        userValidator.validate(user,bindingResult);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/auth/signup";
        }
        signupService.createUser(user);
        String token = tokenService.generateToken(user, TokenType.EMAIL_VERIFICATION);
        emailService.sendValidateMessage(user, token);


        session.setAttribute("canAccessWaitingForVerification", true);

        return "redirect:/auth/waiting_for_verification?token=" + token;
    }

    @GetMapping("/password_reset_sent")
    public String passwordResetSent() {
        return "authentication/password_reset_sent";
    }

    @GetMapping("/forgot_password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("resetPasswordRequest", new ResetPasswordRequestDTO());
        return "authentication/forgot_password";
    }

    @PostMapping("/forgot_password")
    public String forgotPasswordProcess(@ModelAttribute("resetPasswordRequest") @Valid ResetPasswordRequestDTO resetPasswordRequest,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "authentication/forgot_password";
        String email = resetPasswordRequest.getEmail();
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (userService.findByEmail(email).isEmpty()) return "redirect:/auth/password_reset_sent";
            emailService.sendResetPasswordMessage(user);
        }
        return "redirect:/auth/password_reset_sent";
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("token") Optional<String> token) {
        if (token.isEmpty()) return "redirect:/bad_request";

        Optional<VerificationToken> optionalToken = tokenService.findByToken(token.get());
        if (optionalToken.isPresent()) {
            VerificationToken verificationToken = optionalToken.get();
            User user = verificationToken.getUser();

            if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                userService.delete(user);
                return "redirect:/auth/verification_expired";
            }

            user.setVerified(true);
            scheduler.schedule(() -> tokenService.delete(verificationToken), 5100, TimeUnit.MILLISECONDS);
            userService.save(user);

            return "authentication/confirm";
        }
        else {
            return "redirect:/bad_request";
        }
    }

    @GetMapping("/waiting_for_verification")
    public String waitingForVerification(@RequestParam("token") Optional<String> token,
                                         Model model,
                                         HttpSession session) {
        if (session.getAttribute("canAccessWaitingForVerification") == null ||
                token.isEmpty()) return "redirect:/bad_request";

        session.removeAttribute("canAccessWaitingForVerification");
        Optional<VerificationToken> optionalToken = tokenService.findByToken(token.get());
        if (optionalToken.isPresent()) {
            model.addAttribute("token", token.get());
            return "authentication/waiting_for_verification";
        }
        else {
            return "redirect:/auth/verification_expired";
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
        else {
            return ResponseEntity.ok(Collections.singletonMap("status", "token_not_found"));
        }

        return ResponseEntity.ok(Collections.singletonMap("status", "not_verified"));
    }
}
