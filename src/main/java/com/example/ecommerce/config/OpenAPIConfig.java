package com.example.ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI ecommerceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-commerce Backend API")
                        .description("API documentation for your E-commerce platform including Auth, Products, Cart, and Orders.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Subhash")
                                .email("admin@ecommerce.com"))
                        .license(new License()
                                .name("Apache 2.0")));
    }
}
