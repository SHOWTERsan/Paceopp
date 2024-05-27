package ru.santurov.paceopp.DTO;

import lombok.Data;
import ru.santurov.paceopp.models.Image;

@Data
public class AdminBeatDTO {
    private int id;
    private String name;
    private int bpm;
    private Image image;
    private boolean hasAudios;
}
