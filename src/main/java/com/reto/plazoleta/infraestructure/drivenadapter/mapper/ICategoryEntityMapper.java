package com.reto.plazoleta.infraestructure.drivenadapter.mapper;

import com.reto.plazoleta.domain.model.CategoryModel;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ICategoryEntityMapper {

    CategoryModel toCategoryEntity(CategoryEntity categoryEntity);
}
