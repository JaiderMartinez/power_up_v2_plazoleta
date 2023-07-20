package com.reto.plazoleta.application.handler;

import com.reto.plazoleta.application.dto.request.OrderDishTypeRequestDto;
import com.reto.plazoleta.application.dto.request.SingleDishOrderRequestDto;
import com.reto.plazoleta.application.dto.response.OrderDishTypeDtoResponse;
import com.reto.plazoleta.application.dto.response.SingleDishOrderResponseDto;
import com.reto.plazoleta.application.dto.response.PendingDishResponseDto;
import com.reto.plazoleta.application.dto.response.takenorder.OrderTakenResponseDto;

import java.util.List;

public interface IOrderHandler {

    OrderTakenResponseDto getOrderByPriority();

    List<PendingDishResponseDto> pendingOrdersWithLowPriority();

    SingleDishOrderResponseDto addSingleDishOrder(SingleDishOrderRequestDto singleDishOrderRequestDto, Long idRestaurant);

    List<OrderDishTypeDtoResponse> addOrderWithMultipleDishesType(List<OrderDishTypeRequestDto> ordersDishesTypeRequest, Long idRestaurantFromOrder);
}
