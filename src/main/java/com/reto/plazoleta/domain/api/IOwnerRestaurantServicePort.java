package com.reto.plazoleta.domain.api;

import com.reto.plazoleta.domain.model.DishModel;

public interface IOwnerRestaurantServicePort {

    DishModel saveDish(DishModel dishModel);

    DishModel updateDish(DishModel dishModel);

    DishModel enableOrDisableDishByFieldStatusAndIdRestaurantAndIdDish(DishModel dishModelRequest, String tokenWithBearerPrefix);
}
