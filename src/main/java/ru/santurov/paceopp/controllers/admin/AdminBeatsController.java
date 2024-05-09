package ru.santurov.paceopp.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.santurov.paceopp.models.Beat;
import ru.santurov.paceopp.services.BeatService;

import java.util.List;

@Controller
@RequestMapping("/api/beats")
@RequiredArgsConstructor
public class AdminBeatsController {

    private final BeatService beatService;

    @GetMapping("")
    public ResponseEntity<List<Beat>> getBeats() {
            List<Beat> beats = beatService.findAll();
        return ResponseEntity.ok(beats);
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

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Beat> updateBeat(@PathVariable int id,
                                           @RequestParam("name") String name,
                                           @RequestParam("bpm") int bpm,
                                           @RequestParam(value = "image", required = false) MultipartFile image,
                                           @RequestParam(value = "audio", required = false) MultipartFile audio) {
        try {
            Beat updatedBeat = beatService.updateBeat(id, name, bpm, image, audio);
            return ResponseEntity.ok(updatedBeat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public String deleteBeat(@PathVariable Long id) {
//        beatService.deleteBeat(id);
        return "redirect:/beats";
    }
}
