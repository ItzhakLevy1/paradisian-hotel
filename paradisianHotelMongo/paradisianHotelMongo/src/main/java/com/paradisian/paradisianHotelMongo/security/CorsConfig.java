package com.paradisian.paradisianHotelMongo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET","POST","PUT","DELETE")
                        .allowedOrigins("*");
            }
        };
    }
}

/*
This class is used to configure CORS (Cross-Origin Resource Sharing)
This code allows all domains (via allowedOrigins("*")) to access any path ("/**") of your application via HTTP methods: GET, POST, PUT, and DELETE.
You can adjust the allowedOrigins to be more restrictive (for example, to only allow your frontend domain to communicate with the backend).
 */