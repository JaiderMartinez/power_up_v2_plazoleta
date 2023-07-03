package com.reto.plazoleta.domain.spi.token;

public interface ITokenServiceProviderPort {

    String getEmailFromToken(String tokenWithPrefixBearer);

    String getTokenWithPrefixBearerFromUserAuthenticated();
}
