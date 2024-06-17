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
    private final ServiceManagementService service;
    private final UserService userService;

    public List<Order> findByUsername(String username) {
        return orderRepository.findAllByUsername(username);
    }

    public void saveOrder(String username, Long serviceId, Beat beat) {
        Order order = new Order();
        order.setUsername(username);
        order.setService(service.findById(serviceId).orElseThrow(NullPointerException::new));
        order.setBeat(beat);
        order.setOrderDate(LocalDateTime.now());
        order.setUser(userService.findByUsername(username).orElseThrow(NullPointerException::new));

        orderRepository.save(order);
    }

    public boolean hasUserOrderedAudio(String username, Long audioId) {
        return orderRepository.findAllByUsername(username).stream()
                .flatMap(order -> order.getBeat().getAudioFiles().stream())
                .anyMatch(audio -> audio.getId().equals(audioId));
    }
}
