package ru.santurov.paceopp.services;

import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.Beat;
import ru.santurov.paceopp.repositories.BeatRepository;

import java.util.List;

@Service
public class BeatService {
    private final BeatRepository beatRepository;

    public BeatService(BeatRepository beatRepository) {
        this.beatRepository = beatRepository;
    }

    public List<Beat> findAll() {
        return beatRepository.findAll();
    }

    public Beat findById(int id) {
        return beatRepository.findById(id).orElse(null);
    }

    public void save(Beat beat) {
        beatRepository.save(beat);
    }

    public void deleteById(int id) {
        beatRepository.deleteById(id);
    }
}
