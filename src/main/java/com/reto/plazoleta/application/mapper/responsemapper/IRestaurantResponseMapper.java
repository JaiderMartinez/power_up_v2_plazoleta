package com.reto.plazoleta.application.mapper.responsemapper;

import com.reto.plazoleta.application.dto.response.RestaurantCreatedResponseDto;
import com.reto.plazoleta.domain.model.RestaurantModel;
import org.mapstruct.Mapper;

@Mapper
public interface IRestaurantResponseMapper {

    RestaurantCreatedResponseDto toRestaurantCreatedResponseDto(RestaurantModel restaurantModel);
}
