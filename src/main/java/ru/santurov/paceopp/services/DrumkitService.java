package ru.santurov.paceopp.services;

import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.Drumkit;
import ru.santurov.paceopp.repositories.DrumkitRepository;

import java.util.List;

@Service
public class DrumkitService {
    private final DrumkitRepository drumkitRepository;

    public DrumkitService(DrumkitRepository drumkitRepository) {
        this.drumkitRepository = drumkitRepository;
    }


    public List<Drumkit> findAll() {
        return drumkitRepository.findAll();
    }

}
