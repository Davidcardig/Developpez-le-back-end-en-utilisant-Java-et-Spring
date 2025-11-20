package com.chatop.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    private final String imagesDir = "Chatop\\src\\main\\resources\\static\\images";

    public Resource loadImage(String filename) throws Exception {
        Path filePath = Paths.get(imagesDir).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IllegalArgumentException("Image not found: " + filename);
        }
    }

    public String getContentType(String filename) {
        String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        switch (fileExtension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }
}

