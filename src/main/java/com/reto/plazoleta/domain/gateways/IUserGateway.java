package com.reto.plazoleta.domain.gateways;

import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;

public interface IUserGateway {

    User getUserById(Long idUser, String token);

    User getUserByEmailInTheToken(String email, String tokenWithPrefixBearer);
}
