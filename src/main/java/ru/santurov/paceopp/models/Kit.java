package ru.santurov.paceopp.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "kit")
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
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    private KitArchive kitArchive;

}
//todo удаление при обновлении картинки
//эффективность скорее всего связано с тем что чтоб сделать дто нужно достать ориг ФАЙЛ