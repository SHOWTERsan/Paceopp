package ru.santurov.paceopp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.santurov.paceopp.models.Audio;

@Repository
public interface AudioRepository extends JpaRepository<Audio,Long> {
    void deleteByBeatIdAndId(int beatId, Long audioId);
}
