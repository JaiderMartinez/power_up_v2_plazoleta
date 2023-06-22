package com.reto.plazoleta.infraestructure.drivenadapter.token;

import com.reto.plazoleta.domain.spi.token.ITokenServiceProviderPort;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenAdapter implements ITokenServiceProviderPort {

    private final JwtProvider jwtProvider;

    @Override
    public String getEmailFromToken(String tokenWithPrefixBearer) {
        String tokenWithoutPrefix = tokenWithPrefixBearer.replace("Bearer ", "").trim();
        return jwtProvider.getAuthentication(tokenWithoutPrefix).getName();
    }
}
