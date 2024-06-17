package ru.santurov.paceopp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.Audio;
import ru.santurov.paceopp.repositories.AudioRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AudioService {
    private final AudioRepository audioRepository;

    public Optional<Audio> findById(Long id) {
        return audioRepository.findById(id);
    }

    public Resource loadAsResource(Audio audio) {
        try {
            Path tempFile = Files.createTempFile("audio_", "." + audio.getFileFormat());
            Files.write(tempFile, audio.getData());
            Resource resource = new UrlResource(tempFile.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + audio.getId());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read file: " + audio.getId(), e);
        }
    }
}
