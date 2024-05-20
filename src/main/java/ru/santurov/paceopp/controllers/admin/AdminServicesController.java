package ru.santurov.paceopp.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.santurov.paceopp.models.Service;
import ru.santurov.paceopp.models.ServiceItem;
import ru.santurov.paceopp.services.ServiceManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class AdminServicesController {
    private final ServiceManagementService serviceManagementService;

    @GetMapping()
    public ResponseEntity<List<Service>> getServices() {
        List<Service> services = serviceManagementService.findAll();
        return ResponseEntity.ok(services);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody Service updatedService) {
        try {
            Service service = serviceManagementService.updateService(id, updatedService);
            return ResponseEntity.ok(service);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{serviceId}/items/{itemId}")
    public ResponseEntity<Void> deleteServiceItem(@PathVariable Long serviceId, @PathVariable Long itemId) {
        try {
            serviceManagementService.deleteServiceItem(serviceId, itemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
