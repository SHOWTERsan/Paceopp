package ru.santurov.paceopp.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "drumkit")
public class Kit {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "cost")
    private Double cost;
    @Column(name = "description")
    private String description;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;
    @Column(name = "data",columnDefinition = "bytea")
    private byte[] data;
}
