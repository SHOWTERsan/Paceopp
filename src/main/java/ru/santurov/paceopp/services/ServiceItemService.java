package ru.santurov.paceopp.services;

import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.ServiceItem;
import ru.santurov.paceopp.repositories.ServiceItemRepository;

import java.util.List;

@Service
public class ServiceItemService {
    private final ServiceItemRepository serviceItemRepository;

    public ServiceItemService(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    public List<ServiceItem> findAll() {
        return serviceItemRepository.findAll();
    }
}
