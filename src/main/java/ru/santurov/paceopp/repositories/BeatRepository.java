package ru.santurov.paceopp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.santurov.paceopp.models.Beat;

@Repository
public interface BeatRepository extends JpaRepository<Beat, Integer> {
}
