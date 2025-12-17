package com.chatop.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    private final ResourceLoader resourceLoader;

    @Value("${app.images.directory:src/main/resources/static/images}")
    private String imagesDir;

    public ImageService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Resource loadImage(String filename) {
        // Construit le chemin complet
        Path imagePath = Paths.get(imagesDir).resolve(filename);
        File imageFile = imagePath.toFile();

        // Si le chemin est relatif et que le fichier n'existe pas, essaye avec le répertoire de travail
        if (!imageFile.isAbsolute()) {
            imagePath = Paths.get(System.getProperty("user.dir")).resolve(imagesDir).resolve(filename);
            imageFile = imagePath.toFile();
        }

        if (imageFile.exists() && imageFile.canRead()) {
            return resourceLoader.getResource("file:" + imagePath.toAbsolutePath());
        } else {
            throw new IllegalArgumentException("Image not found: " + filename + " - Searched at: " + imagePath.toAbsolutePath());
        }
    }

    public String getContentType(String filename) {
        String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        return switch (fileExtension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
    }
}

