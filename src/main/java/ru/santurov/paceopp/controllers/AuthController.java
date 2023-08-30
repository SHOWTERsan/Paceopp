package ru.santurov.paceopp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.santurov.paceopp.DTO.ForgotPasswordRequestDTO;
import ru.santurov.paceopp.DTO.SignupFormDTO;
import ru.santurov.paceopp.models.TokenType;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.models.VerificationToken;
import ru.santurov.paceopp.services.*;
import ru.santurov.paceopp.utils.SignUpValidator;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final SignUpValidator signUpValidator;
    private final SignupService signupService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public AuthController(UserService userService, SignUpValidator signUpValidator, SignupService signupService, EmailService emailService, TokenService tokenService, ModelMapper modelMapper, SimpMessagingTemplate simpMessagingTemplate) {
        this.userService = userService;
        this.signUpValidator = signUpValidator;
        this.signupService = signupService;
        this.emailService = emailService;
        this.tokenService = tokenService;
        this.modelMapper = modelMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    @GetMapping("verificationExpired")
    public String verificationExpired() {
        return "authentication/verification_expired";
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
        if (!model.containsAttribute("signupForm")) {
            model.addAttribute("signupForm", new SignupFormDTO());
        }
        return "authentication/signup";
    }

    @PostMapping("/signup_process")
    public String signupProcess(@ModelAttribute("signupForm") @Valid SignupFormDTO signupForm,
                                BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                HttpSession session) {
        signUpValidator.validate(signupForm,bindingResult);
        if (!signupForm.getPassword().equals(signupForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "error.passwordConfirm", "Passwords do not match");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("signupForm", signupForm);
            return "redirect:/auth/signup";
        }
        User user = toUser(signupForm);
        signupService.createUser(user);
        emailService.sendValidateMessage(user);

        session.setAttribute("canAccessWaitingForVerification", true);

        return "redirect:/auth/waiting_for_verification?uuid=" + user.getUuid();
    }

    private User toUser(Object futureUser) {
        return modelMapper.map(futureUser, User.class);
    }

    @GetMapping("/password_reset_sent")
    public String passwordResetSent() {
        return "authentication/password_reset_sent";
    }

    @GetMapping("/forgot_password")
    public String forgotPasswordPage(Model model) {
        if (!model.containsAttribute("forgotPasswordRequest")) {
            model.addAttribute("resetPasswordRequest", new ForgotPasswordRequestDTO());
        }
        return "authentication/forgot_password";
    }

    @PostMapping("/forgot_password_process")
    public String forgotPasswordProcess(@ModelAttribute("forgotPasswordRequest") @Valid ForgotPasswordRequestDTO forgotPasswordRequest,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.forgotPasswordRequest", bindingResult);
            redirectAttributes.addFlashAttribute("forgotPasswordRequest", forgotPasswordRequest);
            return "authentication/forgot_password";
        }
        String email = forgotPasswordRequest.getEmail();
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            emailService.sendForgotPasswordMessage(user);
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
            userService.save(user);

            simpMessagingTemplate.convertAndSend("/topic/checkVerificationStatus/"+user.getUuid(), "updated");

            return "authentication/confirm";
        }
        else {
            return "redirect:/bad_request";
        }
    }

    @GetMapping("/waiting_for_verification")
    public String waitingForVerification(@RequestParam("uuid") Optional<String> uuid,
                                         Model model,
                                         HttpSession session) {
        if (session.getAttribute("canAccessWaitingForVerification") == null ||
                uuid.isEmpty()) return "redirect:/bad_request";

        session.removeAttribute("canAccessWaitingForVerification");
        Optional<User> user = userService.findByUuid(uuid.get());
        Optional<VerificationToken> optionalToken = tokenService.getTokenByUserAndType(user, TokenType.EMAIL_VERIFICATION);

        if (optionalToken.isPresent()) {
            model.addAttribute("uuid", uuid.get());
            return "authentication/waiting_for_verification";
        }
        else {
            return "redirect:/auth/verification_expired";
        }
    }
}
