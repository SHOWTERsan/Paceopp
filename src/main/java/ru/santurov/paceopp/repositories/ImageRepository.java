package ru.santurov.paceopp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.santurov.paceopp.models.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
}
