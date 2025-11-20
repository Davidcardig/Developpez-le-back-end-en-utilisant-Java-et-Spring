package com.chatop.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir les images depuis le dossier static/images avec chemin absolu
        String imagesPath = "file:///C:/Users/david/Desktop/OCR/Projet 3 bis/Developpez-le-back-end-en-utilisant-Java-et-Spring/Chatop/src/main/resources/static/images/";

        registry.addResourceHandler("/images/**")
                .addResourceLocations(imagesPath, "classpath:/static/images/");
    }
}

