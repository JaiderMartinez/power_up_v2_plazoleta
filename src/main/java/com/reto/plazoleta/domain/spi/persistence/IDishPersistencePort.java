package com.reto.plazoleta.domain.spi.persistence;

import com.reto.plazoleta.domain.model.dishes.DishModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDishPersistencePort {

    DishModel saveDish(DishModel dishModel);

    DishModel findById(Long idDish);

    Page<DishModel> getAllDishesActiveOfARestaurantOrderByCategoryAscending(Pageable pageable, Long idRestaurant);
}
