package com.reto.plazoleta.application.mapper.responsemapper;

import com.reto.plazoleta.application.dto.response.DishCreatedResponseDto;
import com.reto.plazoleta.application.dto.response.DishStatusResponseDto;
import com.reto.plazoleta.application.dto.response.UpdateDishResponseDto;
import com.reto.plazoleta.domain.model.dishes.DishModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IOwnerResponseMapper {

    DishCreatedResponseDto dishModelToDishResponse(DishModel dishModel);

    UpdateDishResponseDto dishModelToDishUpdateResponse(DishModel dishModel);

    @Mapping(target = "active", source = "dishModel.state")
    DishStatusResponseDto dishModelToDishStatusResponseDto(DishModel dishModel);
}
