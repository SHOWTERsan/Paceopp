package ru.santurov.paceopp.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.santurov.paceopp.DTO.AdminBeatDTO;
import ru.santurov.paceopp.models.Audio;
import ru.santurov.paceopp.models.Beat;
import ru.santurov.paceopp.services.BeatService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/beats")
@RequiredArgsConstructor
public class AdminBeatsController {

    private final BeatService beatService;

    @GetMapping("")
    public ResponseEntity<List<AdminBeatDTO>> getBeats() {
        List<AdminBeatDTO> beats = beatService.findALlAdminBeatsDTO();
        return ResponseEntity.ok(beats);
    }

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminBeatDTO> updateBeat(@PathVariable int id,
                                           @RequestParam("name") String name,
                                           @RequestParam("bpm") int bpm,
                                           @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

            AdminBeatDTO updatedBeat = beatService.updateBeat(id, name, bpm, image);
            return ResponseEntity.ok(updatedBeat);

    }
    @GetMapping("/{beatId}/audio")
    public ResponseEntity<List<Audio>> getAudios(@PathVariable int beatId) {
        try {
            Beat beat = beatService.findById(beatId).orElseThrow(() -> new RuntimeException("Beat not found with id " + beatId));
            List<Audio> audios = beat.getAudioFiles();
            return ResponseEntity.ok(audios);
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{beatId}/audio")
    public ResponseEntity<?> uploadAudioToBeat(@PathVariable int beatId, @RequestParam("audio") List<MultipartFile> audioFiles) {
        try {
            for (MultipartFile file : audioFiles) {
                String fileFormat = getFileExtension(file.getOriginalFilename());
                beatService.addAudioToBeat(beatId, file, fileFormat);
            }
            return ResponseEntity.ok("Audio files uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminBeatDTO> createBeat(@RequestParam("name") String name,
                                           @RequestParam("bpm") int bpm,
                                           @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            AdminBeatDTO newBeat = beatService.createBeat(name, bpm, image);
            return ResponseEntity.ok(newBeat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeat(@PathVariable int id) {
        try {
            beatService.deleteBeat(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/{beatId}/audio/{audioId}")
    public ResponseEntity<?> deleteAudioFromBeat(@PathVariable int beatId, @PathVariable Long audioId) {
        try {
            beatService.deleteAudioFromBeat(beatId, audioId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
