package ru.santurov.paceopp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.santurov.paceopp.models.Beat;
import ru.santurov.paceopp.models.Image;
import ru.santurov.paceopp.repositories.BeatRepository;
import ru.santurov.paceopp.repositories.ImageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BeatService {
    private final BeatRepository beatRepository;
    private final ImageRepository imageRepository;

    public List<Beat> findAll() {
        return beatRepository.findAll();
    }

    public Beat findById(int id) {
        return beatRepository.findById(id).orElse(null);
    }

    public Beat updateBeat(int id, String name, int bpm, MultipartFile imageFile, MultipartFile audio) throws IOException {
        Beat beat = beatRepository.findById(id).orElseThrow(() -> new RuntimeException("Beat not found with id " + id));

        beat.setName(name);
        beat.setBpm(bpm);

        if (imageFile != null && !imageFile.isEmpty()) {
            Image image = new Image();
            image.setName(imageFile.getOriginalFilename());
            image.setData(imageFile.getBytes());
            image = imageRepository.save(image); // Save and get updated image with ID
            beat.setImage(image);
        }

        if (audio != null && !audio.isEmpty()) {
            Path audioLocation = Paths.get("src/main/resources/static/resbeats/" + audio.getOriginalFilename());
            Files.copy(audio.getInputStream(), audioLocation, StandardCopyOption.REPLACE_EXISTING);
            beat.setAudioPath("/resbeats/" + audio.getOriginalFilename());
        }

        return beatRepository.save(beat);
    }

    public void deleteById(int id) {
        beatRepository.deleteById(id);
    }
}
