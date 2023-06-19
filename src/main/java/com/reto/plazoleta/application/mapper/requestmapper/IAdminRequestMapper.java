package com.reto.plazoleta.application.mapper.requestmapper;

import com.reto.plazoleta.application.dto.request.RequestToCreateRestaurantDto;
import com.reto.plazoleta.domain.model.RestaurantModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IAdminRequestMapper {

    @Mapping(target = "idRestaurant", ignore = true)
    RestaurantModel requestToCreateRestaurantDtoToRestaurantModel(RequestToCreateRestaurantDto requestToCreateRestaurantDto);
}
