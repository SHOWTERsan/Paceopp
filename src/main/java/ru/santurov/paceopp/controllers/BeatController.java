package ru.santurov.paceopp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.santurov.paceopp.DTO.BeatDTO;
import ru.santurov.paceopp.services.BeatService;

import java.util.List;

@Controller
@RequestMapping("/beats")
@RequiredArgsConstructor
public class BeatController {

    private final BeatService beatService;

    @GetMapping("")
    public String getBeats(Model model) {
        List<BeatDTO> beats = beatService.findAll().stream().map(beatService::toBeatDTO).toList();
            model.addAttribute("beats", beats);
        return "beats";
    }
}
