package com.reto.plazoleta.domain.spi.clients;

import com.reto.plazoleta.domain.model.User;
import com.reto.plazoleta.infraestructure.drivenadapter.webclients.dto.request.UserDto;

public interface IUserGateway {

    UserDto getUserById(Long idUser, String token);

    User getUserByEmailInTheToken(String email, String tokenWithPrefixBearer);
}
