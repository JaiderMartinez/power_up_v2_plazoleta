package com.reto.plazoleta.domain.spi.persistence;

import com.reto.plazoleta.domain.model.CategoryModel;

public interface ICategoryPersistencePort {

    CategoryModel findById(Long idCategory);
}