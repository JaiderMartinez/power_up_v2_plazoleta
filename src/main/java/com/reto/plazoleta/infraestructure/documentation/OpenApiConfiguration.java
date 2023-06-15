package com.reto.plazoleta.infraestructure.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Plazoleta")
                        .version("1.0")
                        .description("Administrador de restaurantes, platos y pedidos")
                        .termsOfService("http://swagger.io/terms/")
                );
    }
}
