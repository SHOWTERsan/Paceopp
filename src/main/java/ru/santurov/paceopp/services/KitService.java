package ru.santurov.paceopp.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.awt.image.BufferedImage;
import java.io.*;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.santurov.paceopp.DTO.AdminKitDTO;
import ru.santurov.paceopp.models.Image;
import ru.santurov.paceopp.models.Kit;
import ru.santurov.paceopp.repositories.ImageRepository;
import ru.santurov.paceopp.repositories.KitRepository;

import javax.imageio.ImageIO;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KitService {
    private final KitRepository kitRepository;
    private final ImageRepository imageRepository;

    public List<Kit> findAll() {
        return kitRepository.findAll();
    }

    public List<AdminKitDTO> findAllAdminKitsDTO() {
        List<Kit> kits = kitRepository.findAll();
        return kits.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    private AdminKitDTO convertToDTO(Kit kit) {
        AdminKitDTO dto = new AdminKitDTO();
        dto.setId(kit.getId());
        dto.setTitle(kit.getTitle());
        dto.setCost(kit.getCost());
        dto.setDescription(kit.getDescription());
        dto.setImage(kit.getImage());
        dto.setHasArchive(kit.getKitArchive() != null); // Set hasArchive based on the presence of data
        return dto;
    }
    public Kit findById(long kitId) {
        return kitRepository.findById(kitId)
                .orElseThrow(() -> new RuntimeException("Kit not found with id: " + kitId));
    }
    public Kit updateKit(long id, String title, double cost, MultipartFile imageFile, MultipartFile archiveFile, String description) throws IOException {
        Kit kit = kitRepository.findById(id).orElseThrow(() -> new RuntimeException("Kit not found with id " + id));

        kit.setTitle(title);
        kit.setCost(cost);
        kit.setDescription(description);
        if (kit.getImage() != null) {
            imageRepository.deleteById(kit.getImage().getId());
            kit.setImage(null);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            Image image = new Image();
            image.setName(imageFile.getOriginalFilename());
            image.setData(compressImage(imageFile.getBytes(), 0.5f));
            image = imageRepository.save(image); // Save and get updated image with ID
            kit.setImage(image);
        }

        // Assign archive file data directly to the kit's data field
        if (archiveFile != null && !archiveFile.isEmpty()) {
            kit.getKitArchive().setData(archiveFile.getBytes());
        }

        return kitRepository.save(kit);
    }


    public static byte[] compressImage(byte[] imageData, float compressionQuality) throws IOException {
        // Convert bytea data to BufferedImage
        ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
        BufferedImage bufferedImage = ImageIO.read(bis);

        // Create output stream to store compressed image
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Write compressed image to output stream
        ImageIO.write(bufferedImage, "jpg", bos);

        // Get the compressed image data
        byte[] compressedImageData = bos.toByteArray();

        // Close streams
        bis.close();
        bos.close();

        return compressedImageData;
    }


    public Resource getKitArchive(long kitId) {
        // Retrieve the kit entity from the database
        Kit kit = kitRepository.findById(kitId)
                .orElseThrow(() -> new RuntimeException("Kit not found with id: " + kitId));

        // Create a ByteArrayResource from the kit archive data
        ByteArrayResource resource = new ByteArrayResource(kit.getKitArchive().getData());
        return resource;
    }

    public Kit createKit(String title, double cost, MultipartFile imageFile, MultipartFile archiveFile, String description) throws IOException {
        Kit kit = new Kit();
        kit.setTitle(title);
        kit.setCost(cost);
        kit.setDescription(description);

        if (imageFile != null && !imageFile.isEmpty()) {
            Image image = new Image();
            image.setName(imageFile.getOriginalFilename());
            image.setData(imageFile.getBytes());
            image = imageRepository.save(image); // Save and get updated image with ID
            kit.setImage(image);
        }

        // Process and save the archive file
        if (archiveFile != null && !archiveFile.isEmpty()) {
            kit.getKitArchive().setData(archiveFile.getBytes());
        }

        return kitRepository.save(kit);
    }

    @Transactional
    public void deleteKit(long id) {
        kitRepository.deleteById(id);
    }
}

