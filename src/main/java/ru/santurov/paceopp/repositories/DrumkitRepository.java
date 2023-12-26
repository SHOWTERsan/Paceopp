package ru.santurov.paceopp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.santurov.paceopp.models.Drumkit;

import java.util.List;

@Repository
public interface DrumkitRepository extends JpaRepository<Drumkit, Long> {

    List<Drumkit> findAll();

}
