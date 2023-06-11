package com.reto.plazoleta.application.mapper.responsemapper;

import com.reto.plazoleta.application.dto.response.CreateDishResponseDto;
import com.reto.plazoleta.application.dto.response.DishStatusResponseDto;
import com.reto.plazoleta.application.dto.response.UpdateDishResponseDto;
import com.reto.plazoleta.domain.model.DishModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IDishResponseMapper {

    CreateDishResponseDto toDishResponse(DishModel dishModel);

    UpdateDishResponseDto toDishUpdateResponse(DishModel dishModel);

    @Mapping(target = "active", source = "dishModel.stateDish")
    DishStatusResponseDto toDishStatusResponseDto(DishModel dishModel);
}
