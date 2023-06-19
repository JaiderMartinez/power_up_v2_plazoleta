package com.reto.plazoleta.domain.api;

import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import org.springframework.data.domain.Page;

public interface ICustomerServicePort {

    Page<RestaurantModel> findAllByOrderByNameAsc(Integer numberPage, Integer sizeItems);

    OrderModel saveOrder(OrderModel orderModelRequest, String tokenWithPrefixBearer);

    OrderModel cancelOrder(Long idOrder, String tokenWithPrefixBearer);
}
