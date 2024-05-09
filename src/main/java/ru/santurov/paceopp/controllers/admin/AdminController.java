package ru.santurov.paceopp.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.santurov.paceopp.services.BeatService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    @GetMapping()
    public String adminDashboard() {
        return "admin/dashboard";
    }

}
