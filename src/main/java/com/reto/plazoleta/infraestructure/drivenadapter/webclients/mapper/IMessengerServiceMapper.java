package com.reto.plazoleta.infraestructure.drivenadapter.webclients.mapper;

import com.reto.plazoleta.domain.model.MessageSmsModel;
import com.reto.plazoleta.infraestructure.drivenadapter.webclients.dto.request.MessageSmsRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IMessengerServiceMapper {

    @Mapping(target = "orderPin", source = "pin")
    MessageSmsRequestDto messageSmsToMessageSmsRequestDto(MessageSmsModel messageSms);
}
