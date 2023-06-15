package com.reto.plazoleta.domain.spi;

import com.reto.plazoleta.domain.model.DishModel;

public interface IDishPersistencePort {

    DishModel saveDish(DishModel dishModel);

    DishModel findById(Long idDish);
}
