package ru.santurov.paceopp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.ServiceItem;
import ru.santurov.paceopp.repositories.ServiceItemRepository;
import ru.santurov.paceopp.repositories.ServiceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceManagementService {
    private final ServiceRepository serviceRepository;
    private final ServiceItemRepository serviceItemRepository;

    public List<ru.santurov.paceopp.models.Service> findAll() {
        return serviceRepository.findAll();
    }

    public ru.santurov.paceopp.models.Service updateService(Long id, ru.santurov.paceopp.models.Service updatedService) {
        ru.santurov.paceopp.models.Service service = serviceRepository.findById(id).orElseThrow(() ->  new RuntimeException("Service not found with id " + id));

        service.setName(updatedService.getName());
        service.setPrice(updatedService.getPrice());
        addOrUpdateServiceItems(updatedService.getItems());

        return serviceRepository.save(service);
    }

    private void addOrUpdateServiceItems(List<ServiceItem> items) {
        for (ServiceItem item : items) {
            if (item.getId() == null) {
                // Item without an ID, meaning it's new
                serviceItemRepository.save(item);
            } else {
                // Item with an ID, meaning it's an existing item that needs updating
                ServiceItem existingItem = serviceItemRepository.findById(item.getId())
                        .orElseThrow(() -> new RuntimeException("Service item not found with id " + item.getId()));
                existingItem.setItem(item.getItem()); // Update the item
                serviceItemRepository.save(existingItem);
            }
        }
    }
    public void deleteServiceItem(Long serviceId, Long itemId) {
        ru.santurov.paceopp.models.Service service = serviceRepository.findById(serviceId).orElseThrow(() -> new RuntimeException("Service not found with id " + serviceId));
        ServiceItem item = serviceItemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Service item not found with id " + itemId));

        service.getItems().remove(item);
        serviceItemRepository.delete(item);
        serviceRepository.save(service);
    }


}
