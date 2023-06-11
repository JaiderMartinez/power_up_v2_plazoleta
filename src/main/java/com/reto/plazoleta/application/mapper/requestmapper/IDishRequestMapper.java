package com.reto.plazoleta.application.mapper.requestmapper;

import com.reto.plazoleta.application.dto.request.CreateDishRequestDto;
import com.reto.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.reto.plazoleta.application.dto.request.DishUpdateStatusRequestDto;
import com.reto.plazoleta.domain.model.DishModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IDishRequestMapper {

    @Mapping(target = "restaurantModel.idRestaurant", source = "createDishRequestDto.idRestaurant")
    @Mapping(target = "categoryModel.idCategory", source = "createDishRequestDto.idCategory")
    @Mapping(target = "stateDish", ignore = true)
    @Mapping(target = "idDish", ignore = true)
    DishModel toDishModel(CreateDishRequestDto createDishRequestDto);

    @Mapping(target = "restaurantModel.idRestaurant", source = "updateDishRequestDto.idRestaurant")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "imageDish", ignore = true)
    @Mapping(target = "stateDish", ignore = true)
    @Mapping(target = "categoryModel", ignore = true)
    @Mapping(target = "price", source = "updateDishRequestDto.price")
    @Mapping(target = "descriptionDish", source = "updateDishRequestDto.descriptionDish")
    DishModel toDishModel(UpdateDishRequestDto updateDishRequestDto);

    @Mapping(target = "restaurantModel.idRestaurant", source = "updateDishStatusRequestDto.idRestaurant")
    @Mapping(target = "stateDish", source = "updateDishStatusRequestDto.active")
    DishModel toDishModelWithValueInFieldsIdRestaurantAndIsActive(DishUpdateStatusRequestDto updateDishStatusRequestDto);
}
