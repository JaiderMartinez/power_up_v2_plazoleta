package com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper;

import com.reto.plazoleta.domain.model.CategoryModel;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ICategoryEntityMapper {

    CategoryModel toCategoryEntity(CategoryEntity categoryEntity);
}
