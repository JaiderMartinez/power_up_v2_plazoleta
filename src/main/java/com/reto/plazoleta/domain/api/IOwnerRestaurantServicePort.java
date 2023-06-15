package com.reto.plazoleta.domain.api;

import com.reto.plazoleta.domain.model.DishModel;

public interface IOwnerRestaurantServicePort {

    DishModel saveDish(DishModel dishModel);

    DishModel updateDish(DishModel dishModel);

    DishModel enableOrDisableDishByFieldStatusAndIdRestaurantAndIdDish(Long idDish, Long idRestaurant, boolean active, String tokenWithBearerPrefix);
}
