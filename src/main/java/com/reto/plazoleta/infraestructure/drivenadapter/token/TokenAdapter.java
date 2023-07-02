package com.reto.plazoleta.infraestructure.drivenadapter.token;

import com.reto.plazoleta.domain.spi.token.ITokenServiceProviderPort;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
public class TokenAdapter implements ITokenServiceProviderPort {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private final JwtProvider jwtProvider;

    @Override
    public String getEmailFromToken(String tokenWithPrefixBearer) {
        String tokenWithoutPrefix = tokenWithPrefixBearer.replace("Bearer ", "").trim();
        return jwtProvider.getAuthentication(tokenWithoutPrefix).getName();
    }

    @Override
    public String getTokenWithPrefixBearerFromUserAuthenticated() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getHeader(HEADER_AUTHORIZATION);
    }
}
