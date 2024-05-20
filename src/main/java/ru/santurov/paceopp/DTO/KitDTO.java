package ru.santurov.paceopp.DTO;

import lombok.Data;

@Data
public class KitDTO {
    private long id;
    private String title;
    private double cost;
    private String description;
    private String image;
    private boolean hasArchive;
}
