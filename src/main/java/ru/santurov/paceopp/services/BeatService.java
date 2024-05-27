package ru.santurov.paceopp.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.santurov.paceopp.DTO.AdminBeatDTO;
import ru.santurov.paceopp.models.Audio;
import ru.santurov.paceopp.models.Beat;
import ru.santurov.paceopp.models.Image;
import ru.santurov.paceopp.repositories.AudioRepository;
import ru.santurov.paceopp.repositories.BeatRepository;
import ru.santurov.paceopp.repositories.ImageRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeatService {
    private final BeatRepository beatRepository;
    private final ImageRepository imageRepository;
    private final AudioRepository audioRepository;

    public List<Beat> findAll() {
        return beatRepository.findAll();
    }

    public List<AdminBeatDTO> findALlAdminBeatsDTO() {
        List<Beat> beats = beatRepository.findAll();
        return beats.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    private AdminBeatDTO convertToDTO(Beat beat) {
        AdminBeatDTO dto = new AdminBeatDTO();
        dto.setId(beat.getId());
        dto.setBpm(beat.getBpm());
        dto.setImage(beat.getImage());
        dto.setName(beat.getName());
        dto.setHasAudios(beat.getAudioFiles() != null);

        return dto;
    }
    public Optional<Beat> findById(int id) {
        return beatRepository.findById(id);
    }

    public Beat updateBeat(int id, String name, int bpm, MultipartFile imageFile) throws IOException {
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

        return beatRepository.save(beat);
    }

    public void addAudioToBeat(int beatId, MultipartFile audioFile, String format) throws IOException {
        Beat beat = beatRepository.findById(beatId).orElseThrow(() -> new RuntimeException("Beat not found with id " + beatId));
        Audio audio = new Audio();
        audio.setBeat(beat);
        audio.setFileFormat(format);
        audio.setData(audioFile.getBytes());

        audioRepository.save(audio);
    }

    public Beat createBeat(String name, int bpm, MultipartFile imageFile) throws IOException {
        Beat beat = new Beat();
        beat.setName(name);
        beat.setBpm(bpm);

        if (imageFile != null && !imageFile.isEmpty()) {
            Image image = new Image();
            image.setName(imageFile.getOriginalFilename());
            image.setData(imageFile.getBytes());
            image = imageRepository.save(image); // Save and get updated image with ID
            beat.setImage(image);
        }

        return beatRepository.save(beat);
    }
    @Transactional
    public void deleteBeat(int id) {
        beatRepository.deleteById(id);
    }
    @Transactional
    public void deleteAudioFromBeat(int beatId, Long audioId) {
        audioRepository.deleteByBeatIdAndId(beatId, audioId);
    }
}
