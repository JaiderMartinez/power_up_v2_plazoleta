package com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper;

import com.reto.plazoleta.domain.model.DishModel;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.DishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IDishEntityMapper {

    @Mapping(target = "restaurantEntity", source = "dishModel.restaurantModel")
    @Mapping(target = "categoryEntity", source = "dishModel.categoryModel")
    DishEntity toDishEntity(DishModel dishModel);

    @Mapping(target = "restaurantModel", source = "dishEntity.restaurantEntity")
    @Mapping(target = "categoryModel", source = "dishEntity.categoryEntity")
    DishModel toDishModel(DishEntity dishEntity);
}
