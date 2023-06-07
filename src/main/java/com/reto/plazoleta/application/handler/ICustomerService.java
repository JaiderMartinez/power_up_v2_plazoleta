package com.reto.plazoleta.application.handler;

import com.reto.plazoleta.application.dto.request.OrderRequestDto;
import com.reto.plazoleta.application.dto.response.OrderCreatedResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantResponsePageableDto;
import org.springframework.data.domain.Page;

public interface ICustomerService {

    Page<RestaurantResponsePageableDto> getAllRestaurantsByOrderByNameAsc(int numberPage, int sizeItems);

    OrderCreatedResponseDto saveOrder(OrderRequestDto createOrderRequestDto, String tokenWithPrefixBearer);
}
