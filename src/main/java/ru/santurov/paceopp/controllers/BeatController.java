package ru.santurov.paceopp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.santurov.paceopp.DTO.BeatDTO;
import ru.santurov.paceopp.models.Audio;
import ru.santurov.paceopp.models.Beat;
import ru.santurov.paceopp.services.BeatService;

import java.util.List;
import java.util.Optional;

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
    @GetMapping("/{id}/audio")
    @ResponseBody
    public ResponseEntity<byte[]> getBeatAudio(@PathVariable int id) {
        Optional<Beat> beatOptional = beatService.findById(id);
        if (beatOptional.isPresent()) {
            Beat beat = beatOptional.get();
            Optional<Audio> mp3Audio = beat.getAudioFiles().stream()
                    .filter(audio -> "mp3".equalsIgnoreCase(audio.getFileFormat()))
                    .findFirst();
            if (mp3Audio.isPresent()) {
                Audio audio = mp3Audio.get();
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("audio/mpeg"))
                        .body(audio.getData());
            }
        }
        return ResponseEntity.notFound().build();
    }
}
