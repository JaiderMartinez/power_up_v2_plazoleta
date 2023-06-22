package com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper;

import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.RestaurantEntity;
import org.mapstruct.Mapper;

@Mapper
public interface IRestaurantEntityMapper {

    RestaurantEntity toRestaurantEntity(RestaurantModel restaurantModel);

    RestaurantModel toRestaurantModel(RestaurantEntity restaurantEntity);
}
