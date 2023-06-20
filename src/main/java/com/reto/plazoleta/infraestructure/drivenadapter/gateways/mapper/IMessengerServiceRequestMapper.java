package com.reto.plazoleta.infraestructure.drivenadapter.gateways.mapper;

import com.reto.plazoleta.domain.model.MessageSms;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.dto.request.MessageSmsRequestDto;
import org.mapstruct.Mapper;

@Mapper
public interface IMessengerServiceRequestMapper {

    MessageSmsRequestDto messageSmsToMessageSmsRequestDto(MessageSms messageSms);
}