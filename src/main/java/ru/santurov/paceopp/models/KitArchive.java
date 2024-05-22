package ru.santurov.paceopp.models;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "kit_archive")
public class KitArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "data",columnDefinition = "bytea")
    private byte[] data;
}
