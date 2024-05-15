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

    public ru.santurov.paceopp.models.Service updateService(Long id, ru.santurov.paceopp.models.Service updatedService) {
        ru.santurov.paceopp.models.Service service = serviceRepository.findById(id).orElseThrow(() ->  new RuntimeException("Service not found with id " + id));

        service.setName(updatedService.getName());
        service.setPrice(updatedService.getPrice());
        service.setItems(updatedService.getItems());

        return serviceRepository.save(service);
    }
}
