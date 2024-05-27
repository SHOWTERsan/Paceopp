package ru.santurov.paceopp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.santurov.paceopp.models.KitArchive;

@Repository
public interface KitArchiveRepository extends JpaRepository<KitArchive, Integer> {
}
