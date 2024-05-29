package ru.santurov.paceopp.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.santurov.paceopp.DTO.AdminKitDTO;
import ru.santurov.paceopp.models.Kit;
import ru.santurov.paceopp.services.KitService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kits")
public class AdminKitsController {
    private final KitService kitService;

    @GetMapping
    public ResponseEntity<List<AdminKitDTO>> getAllKits() {
        return ResponseEntity.ok(kitService.findAllAdminKitsDTO());
    }
    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminKitDTO> updateKit(@PathVariable long id,
                                         @RequestParam("title") String title,
                                         @RequestParam("cost") double cost,
                                         @RequestParam(value = "image", required = false) MultipartFile image,
                                         @RequestParam(value = "archive", required = false) MultipartFile archive,
                                         @RequestParam(value = "description", required = false) String description) {
        try {
            AdminKitDTO updatedKit = kitService.updateKit(id, title, cost, image, archive, description);
            return ResponseEntity.ok(updatedKit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/{kitId}/archive")
    public ResponseEntity<Resource> downloadKitArchive(@PathVariable long kitId) {
        Kit kit = kitService.findById(kitId);

        Resource archiveResource = kitService.getKitArchive(kitId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(kit.getTitle() + "_archive.zip").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(archiveResource);
    }
    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminKitDTO> createKit(@RequestParam("title") String title,
                                         @RequestParam("cost") double cost,
                                         @RequestParam(value = "image", required = false) MultipartFile image,
                                         @RequestParam(value = "archive", required = false) MultipartFile archive,
                                         @RequestParam(value = "description", required = false) String description) {
        try {
            AdminKitDTO createdKit = kitService.createKit(title, cost, image, archive, description);
            return ResponseEntity.ok(createdKit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKit(@PathVariable int id) {
        try {
            kitService.deleteKit(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
