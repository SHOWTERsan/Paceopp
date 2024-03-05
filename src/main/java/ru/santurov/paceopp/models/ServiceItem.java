package ru.santurov.paceopp.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "service_items")
public class ServiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "item")
    private String item;

    @ManyToOne
    @JoinColumn(name = "service_id",insertable=false, updatable=false)
    private Service service;
}
