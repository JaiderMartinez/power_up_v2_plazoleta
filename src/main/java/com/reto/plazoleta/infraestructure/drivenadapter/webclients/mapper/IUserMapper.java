package com.reto.plazoleta.infraestructure.drivenadapter.webclients.mapper;

import com.reto.plazoleta.domain.model.User;
import com.reto.plazoleta.infraestructure.drivenadapter.webclients.dto.request.UserDto;
import org.mapstruct.Mapper;

@Mapper
public interface IUserMapper {

    User userDtoToUser(UserDto userDto);
}
