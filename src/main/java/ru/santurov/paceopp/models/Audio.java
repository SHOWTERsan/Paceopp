package ru.santurov.paceopp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "audio_files")
@Data
public class Audio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beat_id", nullable = false)
    @JsonIgnore
    private Beat beat;

    @Column(name = "file_format")
    private String fileFormat;

    @Column(name = "data",columnDefinition = "bytea")
    private byte[] data;
}
