package ru.santurov.paceopp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.serives.SignupService;
import ru.santurov.paceopp.utils.UserValidator;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserValidator userValidator;
    private final SignupService signupService;

    @Autowired
    public AuthController(UserValidator userValidator, SignupService signupService) {
        this.userValidator = userValidator;
        this.signupService = signupService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "authentication/login";
    }

    @GetMapping("/signup")
    public String signUpPage(@ModelAttribute("user")User user) {
        return "authentication/signup";
    }

    @PostMapping("/signup")
    public String signupProcess(@ModelAttribute("user") @Valid User user,
                                BindingResult bindingResult) {
        userValidator.validate(user,bindingResult);
        if (bindingResult.hasErrors()) return "authentication/signup";

        signupService.createUser(user);

        return "redirect:/authentication/login";
    }
}
