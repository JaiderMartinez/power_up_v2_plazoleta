package com.reto.plazoleta.infraestructure.configuration.security.jwt.implementation;

import com.reto.plazoleta.infraestructure.configuration.security.jwt.IUserVerifierToken;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserVerifierToken implements IUserVerifierToken {

    private final WebClient webClient;

    public UserVerifierToken(@Qualifier("webClientUsers") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public boolean isValidTokenUser(String tokenWithPrefixBearer) {
        try {
            this.webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("user/verifier").build())
                    .header(HttpHeaders.AUTHORIZATION, tokenWithPrefixBearer)
                    .exchangeToMono( clientResponse -> {
                        if(clientResponse.statusCode().equals(HttpStatus.NO_CONTENT)) {
                            return Mono.empty();
                        } else {
                            return clientResponse.createException().flatMap(Mono::error);
                        }
                    })
                    .block();
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
