package ru.santurov.paceopp.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class ImageCompressor {
    public static byte[] compressImage(byte[] imageData, float compressionQuality) throws IOException {
        // Convert byte array data to BufferedImage
        ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
        BufferedImage bufferedImage = ImageIO.read(bis);

        // Get an ImageWriter for JPG format
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No writers found");
        }
        ImageWriter writer = writers.next();

        // Set the compression quality
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(compressionQuality); // Compression quality from 0.0 to 1.0
        }

        // Create output stream to store compressed image
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
        writer.setOutput(ios);

        // Write the image with the specified compression
        writer.write(null, new IIOImage(bufferedImage, null, null), param);

        // Close streams
        ios.close();
        bos.close();
        writer.dispose();

        // Get the compressed image data
        return bos.toByteArray();
    }
}
