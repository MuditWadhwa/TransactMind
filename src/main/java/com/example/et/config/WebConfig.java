package com.example.et.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer; // 🚀 Correct Framework 7 import

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                // 1. Tell Spring to read the custom version property from the header payload
                .useRequestHeader("X-API-Version")

                // 2. 🚀 THE FIX: Make the version header OPTIONAL so unversioned targets don't crash
                .setVersionRequired(false)

                // 3. Fallback to a baseline version configuration if nothing is annotated on the method
                .setDefaultVersion("1");
    }

}
