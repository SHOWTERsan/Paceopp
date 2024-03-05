package ru.santurov.paceopp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.santurov.paceopp.models.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
}
