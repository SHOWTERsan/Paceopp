package ru.santurov.paceopp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.santurov.paceopp.models.Beat;
import ru.santurov.paceopp.services.BeatService;

@Controller
@RequestMapping("/beats")
@RequiredArgsConstructor
public class BeatController {

    private final BeatService beatService;

    @GetMapping("")
    public String getBeats(Model model) {
            model.addAttribute("beats", beatService.findAll());
        return "beats";
    }

    @GetMapping("/{id}")
    public String getBeat(@PathVariable Long id, Model model) {
//        model.addAttribute("beat", beatService.getBeatById(id));
        return "beat";
    }

    @GetMapping("/new")
    public String newBeat(Model model) {
//        model.addAttribute("beat", new Beat());
        return "new";
    }

    @PostMapping("")
    public String createBeat(@ModelAttribute Beat beat) {
//        beatService.saveBeat(beat);
        return "redirect:/beats";
    }

    @GetMapping("/{id}/edit")
    public String editBeat(@PathVariable Long id, Model model) {
//        model.addAttribute("beat", beatService.getBeatById(id));
        return "edit";
    }

    @PatchMapping("/{id}")
    public String updateBeat(@PathVariable Long id, @ModelAttribute Beat beat) {
//        beatService.updateBeat(id, beat);
        return "redirect:/beats";
    }

    @DeleteMapping("/{id}")
    public String deleteBeat(@PathVariable Long id) {
//        beatService.deleteBeat(id);
        return "redirect:/beats";
    }

}
