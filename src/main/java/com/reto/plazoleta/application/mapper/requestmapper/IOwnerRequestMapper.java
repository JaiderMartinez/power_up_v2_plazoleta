package com.reto.plazoleta.application.mapper.requestmapper;

import com.reto.plazoleta.application.dto.request.DishCreateRequestDto;
import com.reto.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.reto.plazoleta.domain.model.dishes.DishModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IOwnerRequestMapper {

    @Mapping(target = "restaurantModel.idRestaurant", source = "createDishRequestDto.idRestaurant")
    @Mapping(target = "categoryModel.idCategory", source = "createDishRequestDto.idCategory")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "idDish", ignore = true)
    DishModel updateDishRequestDtoToDishModel(DishCreateRequestDto createDishRequestDto);

    @Mapping(target = "restaurantModel.idRestaurant", source = "updateDishRequestDto.idRestaurant")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "urlImageDish", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "categoryModel", ignore = true)
    @Mapping(target = "price", source = "updateDishRequestDto.price")
    @Mapping(target = "description", source = "updateDishRequestDto.description")
    DishModel updateDishRequestDtoToDishModel(UpdateDishRequestDto updateDishRequestDto);
}
