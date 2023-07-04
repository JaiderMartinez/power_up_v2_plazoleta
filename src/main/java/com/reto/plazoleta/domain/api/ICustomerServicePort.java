package com.reto.plazoleta.domain.api;

import com.reto.plazoleta.domain.model.dishes.DishModel;
import com.reto.plazoleta.domain.model.orders.OrderModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import org.springframework.data.domain.Page;

public interface ICustomerServicePort {

    Page<RestaurantModel> findAllByOrderByNameAsc(Integer numberPage, Integer sizeItems);

    Page<DishModel> getAllDishesActivePaginatedFromARestaurantOrderByCategoryAscending(Integer numberPage, Integer sizeItems, Long idRestaurant);

    OrderModel saveOrder(OrderModel orderModelRequest, String tokenWithPrefixBearer);

    OrderModel cancelOrder(Long idOrder, String tokenWithPrefixBearer);

    OrderModel addSingleDishOrder(OrderModel orderRequest);
}
