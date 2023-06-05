package com.reto.plazoleta.domain.api;

import com.reto.plazoleta.domain.model.RestaurantModel;
import org.springframework.data.domain.Page;

public interface IRestaurantServicePort {

    RestaurantModel saveRestaurant(RestaurantModel restaurantModel, String tokenWithBearerPrefix);

    Page<RestaurantModel> findAllByOrderByNameAsc(int numberPage, int sizeItems);
}
