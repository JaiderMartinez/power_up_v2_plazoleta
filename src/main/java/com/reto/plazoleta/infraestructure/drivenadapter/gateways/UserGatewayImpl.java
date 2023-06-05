package com.reto.plazoleta.infraestructure.drivenadapter.gateways;

import com.reto.plazoleta.infraestructure.configuration.security.exception.UserDoesNotExistException;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.infraestructure.exception.UserInTokenIsInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class UserGatewayImpl implements IUserGateway {

    private final WebClient webClient;

    @Override
    public User getUserById(Long idUser, String token) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("user/verifier")
                        .queryParam("idUser", idUser)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchangeToMono( clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(User.class);
                    } else if(clientResponse.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
                        return Mono.error(new UserInTokenIsInvalidException("Username or role in the token is invalid"));
                    } else if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new UserDoesNotExistException("User not found"));
                    } else {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                })
                .block();
    }

    @Override
    public User getUserByEmailInTheToken(String email, String tokenWithPrefixBearer) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("user/get-user-by-email")
                        .queryParam("email", email)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, tokenWithPrefixBearer)
                .exchangeToMono( clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(User.class);
                    } else {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                })
                .block();
    }
}
