package com.reto.plazoleta.application.handler;

import com.reto.plazoleta.application.dto.request.OrderRequestDto;
import com.reto.plazoleta.application.dto.response.OrderCanceledResponseDto;
import com.reto.plazoleta.application.dto.response.OrderCreatedResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantResponsePageableDto;
import org.springframework.data.domain.Page;

public interface ICustomerService {

    Page<RestaurantResponsePageableDto> getAllRestaurantsByOrderByNameAsc(Integer numberPage, Integer sizeItems);

    OrderCreatedResponseDto saveOrder(OrderRequestDto createOrderRequestDto, String tokenWithPrefixBearer);

    OrderCanceledResponseDto cancelOrder(Long pinOrIdOrder, String tokenWithPrefixBearer);
}
