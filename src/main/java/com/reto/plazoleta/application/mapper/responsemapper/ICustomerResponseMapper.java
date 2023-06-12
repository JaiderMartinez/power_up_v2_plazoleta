package com.reto.plazoleta.application.mapper.responsemapper;

import com.reto.plazoleta.application.dto.response.OrderCreatedResponseDto;
import com.reto.plazoleta.domain.model.OrderModel;
import org.mapstruct.Mapper;

@Mapper
public interface ICustomerResponseMapper {

    OrderCreatedResponseDto toCreateOrderResponseDto(OrderModel orderModel);
}
