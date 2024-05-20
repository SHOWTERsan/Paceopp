package ru.santurov.paceopp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.santurov.paceopp.models.Kit;

import java.util.List;

@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {

    List<Kit> findAll();

}
