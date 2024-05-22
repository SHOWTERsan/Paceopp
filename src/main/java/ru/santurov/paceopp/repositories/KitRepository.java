package ru.santurov.paceopp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.santurov.paceopp.models.Kit;


@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {
}

