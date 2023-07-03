package com.reto.plazoleta.application.handler;

import com.reto.plazoleta.application.dto.response.takenorder.OrderTakenResponseDto;

public interface IOrderHandler {

    OrderTakenResponseDto getOrderByPriority();
}
