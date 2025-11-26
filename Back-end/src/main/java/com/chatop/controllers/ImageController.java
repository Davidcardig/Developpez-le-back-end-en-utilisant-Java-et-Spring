package com.chatop.controllers;

import com.chatop.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/images")
@Tag(name = "Images", description = "API de gestion des images des locations")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(
        summary = "Récupérer une image",
        description = "Charge et retourne une image de location par son nom de fichier",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Image trouvée et retournée",
            content = @Content(mediaType = "image/png")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Image non trouvée",
            content = @Content
        )
    })
    @GetMapping("/{filename:.+}") // GET /images/{filename}
    public ResponseEntity<Resource> serveImage(
        @Parameter(description = "Nom du fichier image", required = true)
        @PathVariable String filename) throws Exception {
        Resource resource = imageService.loadImage(filename);
        String contentType = imageService.getContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }
}

