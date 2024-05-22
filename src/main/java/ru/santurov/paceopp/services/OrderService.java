package ru.santurov.paceopp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.Beat;
import ru.santurov.paceopp.models.Order;
import ru.santurov.paceopp.repositories.OrderRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void saveOrder(String username, int serviceId, Beat beat) {
        Order order = new Order();
        order.setUsername(username);
        order.setServiceId(serviceId);
        order.setBeat(beat);
        order.setOrderDate(LocalDateTime.now());

        orderRepository.save(order);
    }
}
