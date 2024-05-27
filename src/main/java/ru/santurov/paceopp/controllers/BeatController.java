package ru.santurov.paceopp.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.santurov.paceopp.DTO.BeatDTO;
import ru.santurov.paceopp.models.Beat;
import ru.santurov.paceopp.services.BeatService;

import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/beats")
@RequiredArgsConstructor
public class BeatController {

    private final BeatService beatService;
    private final ModelMapper modelMapper;

    @GetMapping("")
    public String getBeats(Model model) {
        List<BeatDTO> beats = beatService.findAll().stream().map(this::toBeatDTO).toList();
            model.addAttribute("beats", beats);
        return "beats";
    }
    private BeatDTO toBeatDTO(Beat futureBeat) {
        BeatDTO beatDTO = modelMapper.map(futureBeat, BeatDTO.class);
        if (futureBeat.getImage() != null && futureBeat.getImage().getData() != null) {
            String base64Encoded = Base64.getEncoder().encodeToString(futureBeat.getImage().getData());
            beatDTO.setImage(base64Encoded);
        } else {
            beatDTO.setImage(null);
        }
        return beatDTO;
    }

}
