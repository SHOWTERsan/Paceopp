package ru.santurov.paceopp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.santurov.paceopp.DTO.ForgotPasswordRequestDTO;
import ru.santurov.paceopp.DTO.ResetPasswordRequestDTO;
import ru.santurov.paceopp.DTO.SignupFormDTO;
import ru.santurov.paceopp.models.TokenType;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.models.VerificationToken;
import ru.santurov.paceopp.services.*;
import ru.santurov.paceopp.utils.ResetPasswordValidator;
import ru.santurov.paceopp.utils.SignUpValidator;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final SignUpValidator signUpValidator;
    private final SignupService signupService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ResetPasswordValidator resetPasswordValidator;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/verificationExpired")
    public String verificationExpired() {
        return "authentication/verification_expired";
    }

    @GetMapping("/signin")
    public String signInPage(HttpServletRequest request, Model model) {
        handleSignInError(request, model);
        return "authentication/signin";
    }
    private void handleSignInError(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("error") != null) {
            model.addAttribute("error", request.getSession().getAttribute("error"));
            request.getSession().removeAttribute("error");
        }
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
        signUpValidator.validate(signupForm, bindingResult);
        if (!signupForm.getPassword().equals(signupForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "error.passwordConfirm", "Passwords do not match");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.signupForm", bindingResult);
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
            model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequestDTO());
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
            return "redirect:/auth/forgot_password";
        }
        processForgotPasswordRequest(forgotPasswordRequest);
        return "redirect:/auth/password_reset_sent";
    }

    private void processForgotPasswordRequest(ForgotPasswordRequestDTO forgotPasswordRequest) {
        String email = forgotPasswordRequest.getEmail();
        Optional<User> userOptional = userService.findByEmail(email);
        userOptional.ifPresent(emailService::sendForgotPasswordMessage);
    }

    @GetMapping("/resetPassword")
    public String resetPasswordPage(@RequestParam("token") Optional<String> token,
                                    Model model) {
        if (!model.containsAttribute("resetPasswordRequest")) {
            model.addAttribute("resetPasswordRequest", new ResetPasswordRequestDTO());
        }
        if (token.isEmpty()) return "redirect:/bad_request";
        Optional<VerificationToken> optionalToken = tokenService.findByToken(token.get());
        if (optionalToken.isPresent()) {
            VerificationToken verificationToken = optionalToken.get();
            if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                tokenService.delete(verificationToken);
                return "redirect:/auth/verification_expired"; //TODO add page
            }
            model.addAttribute("token", token.get());
            return "authentication/reset_password";
        } else {
            return "redirect:/bad_request";
        }
    }

    @PostMapping("/reset_password_process")
    public String resetPasswordProcess(@ModelAttribute("resetPasswordRequest") @Valid ResetPasswordRequestDTO resetPasswordRequest,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       @RequestParam("token") String token) {
        resetPasswordValidator.validate(resetPasswordRequest, bindingResult);
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.resetPasswordRequest", bindingResult);
            redirectAttributes.addFlashAttribute("resetPasswordRequest", resetPasswordRequest);
            return "redirect:/auth/resetPassword?token=" + token;
        }
        processResetPasswordRequest(resetPasswordRequest,token);
        return "redirect:/auth/signin";
    }

    private void processResetPasswordRequest(ResetPasswordRequestDTO resetPasswordRequest, String token) {
        Optional<VerificationToken> optionalToken = tokenService.findByToken(token);
        if (optionalToken.isPresent()) {
            VerificationToken verificationToken = optionalToken.get();
            User user = verificationToken.getUser();
            user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            userService.save(user);
            tokenService.delete(verificationToken);
        }
    }

    @GetMapping("/confirmEmail")
    public String confirmEmail(@RequestParam("token") Optional<String> token) {
        if (token.isEmpty()) return "redirect:/bad_request";
        Optional<VerificationToken> optionalToken = tokenService.findByToken(token.get());
        return optionalToken.map(this::processEmailConfirmation).orElse("redirect:/bad_request");
    }

    private String processEmailConfirmation(VerificationToken verificationToken) {
        User user = verificationToken.getUser();
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            userService.delete(user);
            return "redirect:/auth/verification_expired";
        }
        user.setVerified(true);
        userService.save(user);
        simpMessagingTemplate.convertAndSend("/topic/checkVerificationStatus/" + user.getUuid(), "updated");
        return "authentication/confirm";
    }

    @GetMapping("/waiting_for_verification")
    public String waitingForVerification(@RequestParam("uuid") Optional<String> uuid,
                                         Model model,
                                         HttpSession session) {
        if (session.getAttribute("canAccessWaitingForVerification") == null || uuid.isEmpty()) return "redirect:/bad_request";
        session.removeAttribute("canAccessWaitingForVerification");
        Optional<User> user = userService.findByUuid(uuid.get());
        Optional<VerificationToken> optionalToken = tokenService.getTokenByUserAndType(user, TokenType.EMAIL_VERIFICATION);
        if (optionalToken.isPresent()) {
            model.addAttribute("uuid", uuid.get());
            return "authentication/waiting_for_verification";
        } else {
            return "redirect:/auth/verification_expired";
        }
    }
    //TODO add resend email verification
    //TODO add delete account after second time verification expired
}
