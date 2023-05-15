package com.reto.plazoleta.infraestructure.drivenadapter.mapper;

import com.reto.plazoleta.domain.model.DishModel;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.DishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishEntityMapper {

    @Mapping(target = "restaurantEntity", source = "dishModel.restaurantModel")
    @Mapping(target = "categoryEntity", source = "dishModel.categoryModel")
    DishEntity toDishEntity(DishModel dishModel);

    @Mapping(target = "restaurantModel", source = "dishEntity.restaurantEntity")
    @Mapping(target = "categoryModel", source = "dishEntity.categoryEntity")
    DishModel toDishModel(DishEntity dishEntity);
}
