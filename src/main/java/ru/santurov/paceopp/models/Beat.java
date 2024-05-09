package ru.santurov.paceopp.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "beats")
public class Beat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "bpm")
    private int bpm;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(name = "audio_path")
    private String audioPath;
}
