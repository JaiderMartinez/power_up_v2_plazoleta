package com.reto.plazoleta.infraestructure.drivenadapter.webclients.adapter;

import com.reto.plazoleta.domain.model.User;
import com.reto.plazoleta.infraestructure.drivenadapter.webclients.dto.request.UserDto;
import com.reto.plazoleta.infraestructure.drivenadapter.webclients.exceptions.UserDoesNotExistException;
import com.reto.plazoleta.domain.spi.clients.IUserGateway;
import com.reto.plazoleta.infraestructure.drivenadapter.webclients.mapper.IUserMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserGatewayImpl implements IUserGateway {

    private final WebClient webClient;
    private final IUserMapper userMapper;

    public UserGatewayImpl(@Qualifier("webClientUsers") WebClient webClient, IUserMapper userMapper) {
        this.webClient = webClient;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto getUserById(Long idUser, String token) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("user/verifier")
                        .queryParam("idUser", idUser)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchangeToMono( clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(UserDto.class);
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
        return this.userMapper.userDtoToUser(
                this.webClient.get().uri(uriBuilder -> uriBuilder.path("user/get-user-by-email")
                        .queryParam("email", email)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, tokenWithPrefixBearer)
                .exchangeToMono( clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(UserDto.class);
                    } else {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                })
                .block()
        );
    }
}
