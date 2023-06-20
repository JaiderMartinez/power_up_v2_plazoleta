package com.reto.plazoleta.infraestructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Value("${base.url.micro.user}")
    private String baseUrl;

    @Value("${base.url.micro.messenger.service}")
    private String baseUrlMessengerService;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean("webClientMessengerService")
    public WebClient webClientMessengerService() {
        return WebClient.builder().baseUrl(this.baseUrlMessengerService).build();
    }
}
