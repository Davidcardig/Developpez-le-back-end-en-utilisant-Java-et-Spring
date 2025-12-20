package com.chatop.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.images.directory:src/main/resources/static/images}")
    private String imagesDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path imagePath = Paths.get(imagesDir);
        if (!imagePath.isAbsolute()) {
            imagePath = Paths.get(System.getProperty("user.dir")).resolve(imagesDir);
        }

        String imagesPath = "file:///" + imagePath.toAbsolutePath().toString().replace("\\", "/") + "/";

        registry.addResourceHandler("/images/**")
                .addResourceLocations(imagesPath, "classpath:/static/images/");
    }
}

