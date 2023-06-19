package com.reto.plazoleta.domain.api;

import com.reto.plazoleta.domain.model.RestaurantModel;

public interface IAdminServicePort {

    RestaurantModel saveRestaurant(RestaurantModel restaurantModel, String tokenWithBearerPrefix);
}
