package com.reto.plazoleta.domain.spi.persistence;

import com.reto.plazoleta.domain.model.RestaurantModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRestaurantPersistencePort {

    RestaurantModel saveRestaurant(RestaurantModel restaurantModel);

    Page<RestaurantModel> findAllByOrderByNameAsc(Pageable pageable);

    RestaurantModel findByIdRestaurant(Long idRestaurant);
}
