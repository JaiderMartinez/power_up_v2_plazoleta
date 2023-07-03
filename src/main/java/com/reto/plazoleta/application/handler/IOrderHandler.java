package com.reto.plazoleta.application.handler;

import com.reto.plazoleta.application.dto.response.pending_orders.PendingOrderResponseDto;
import com.reto.plazoleta.application.dto.response.takenorder.OrderTakenResponseDto;

import java.util.List;

public interface IOrderHandler {

    OrderTakenResponseDto getOrderByPriority();

    List<PendingOrderResponseDto> pendingOrdersWithLowPriority();
}
