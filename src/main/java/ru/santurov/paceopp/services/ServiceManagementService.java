package ru.santurov.paceopp.services;

import org.springframework.stereotype.Service;
import ru.santurov.paceopp.repositories.ServiceRepository;

import java.util.List;

@Service
public class ServiceManagementService {
    private final ServiceRepository serviceRepository;

    public ServiceManagementService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<ru.santurov.paceopp.models.Service> findAll() {
        return serviceRepository.findAll();
    }


}
