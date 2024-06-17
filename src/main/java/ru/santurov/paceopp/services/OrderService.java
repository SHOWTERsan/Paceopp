package ru.santurov.paceopp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.Beat;
import ru.santurov.paceopp.models.Order;
import ru.santurov.paceopp.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private ServiceManagementService service;

    public List<Order> findByUsername(String username) {
        return orderRepository.findAllByUsername(username);
    }

    public void saveOrder(String username, Long serviceId, Beat beat) {
        Order order = new Order();
        order.setUsername(username);
        order.setService(service.findById(serviceId).get());
        order.setBeat(beat);
        order.setOrderDate(LocalDateTime.now());

        orderRepository.save(order);
    }
}
