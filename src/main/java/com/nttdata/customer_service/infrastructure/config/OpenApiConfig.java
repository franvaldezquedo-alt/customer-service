package com.nttdata.customer_service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customer Service API")
                        .description("API REST para la gestión de clientes del banco")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Fran Valdez Quedo")
                                .email("fvaldezq@emeal.nttdata.com")
                                .url("https://github.com/franvaldezquedo")));
    }

}
