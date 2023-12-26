package ru.santurov.paceopp.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "drumkit")
public class Drumkit {
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
    @Column(name = "image_url")
    private String imageUrl;
}
