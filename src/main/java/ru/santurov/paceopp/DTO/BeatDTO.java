package ru.santurov.paceopp.DTO;

import lombok.Data;

@Data
public class BeatDTO {
    private int id;

    private String name;

    private int bpm;

    private String imagePath;
}
