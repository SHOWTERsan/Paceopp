package ru.santurov.paceopp.DTO;

import lombok.Data;
import ru.santurov.paceopp.models.Image;

@Data
public class AdminKitDTO {
    private long id;
    private String title;
    private double cost;
    private String description;
    private Image image;
    private boolean hasArchive;
}