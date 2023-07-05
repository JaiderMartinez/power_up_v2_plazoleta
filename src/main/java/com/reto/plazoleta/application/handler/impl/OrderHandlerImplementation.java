package com.reto.plazoleta.application.handler.impl;

import com.reto.plazoleta.application.dto.request.OrderDishTypeRequestDto;
import com.reto.plazoleta.application.dto.request.SingleDishOrderRequestDto;
import com.reto.plazoleta.application.dto.response.OrderDishTypeDtoResponse;
import com.reto.plazoleta.application.dto.response.SingleDishOrderResponseDto;
import com.reto.plazoleta.application.dto.response.pending_orders.PendingOrderResponseDto;
import com.reto.plazoleta.application.dto.response.takenorder.OrderTakenResponseDto;
import com.reto.plazoleta.application.handler.IOrderHandler;
import com.reto.plazoleta.application.mapper.OrderMapper;
import com.reto.plazoleta.domain.api.ICustomerServicePort;
import com.reto.plazoleta.domain.api.IEmployeeServicePort;
import com.reto.plazoleta.domain.model.orders.OrderModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderHandlerImplementation implements IOrderHandler {

    private final IEmployeeServicePort employeeServicePort;
    private final ICustomerServicePort customerServicePort;
    private OrderMapper orderMapper;

    @Transactional
    @Override
    public OrderTakenResponseDto getOrderByPriority() {
        return orderMapper.orderModelToOrderTakenResponseDto(
                this.employeeServicePort.takeOrderByPriorityInStatusEarring()
        );
    }

    @Override
    public List<PendingOrderResponseDto> pendingOrdersWithLowPriority() {
        return this.employeeServicePort.pendingOrdersWithLowPriority().stream()
                .map(orderModel -> orderMapper.orderModelToPendingOrderResponseDto(orderModel))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public SingleDishOrderResponseDto addSingleDishOrder(SingleDishOrderRequestDto singleDishOrderRequestDto, Long idRestaurant) {
        final OrderModel orderModelRequest = orderMapper.singleDishOrderRequestDtoToOrderModel(singleDishOrderRequestDto, idRestaurant);
        final OrderModel orderModelResponse = this.customerServicePort.addSingleDishOrder(orderModelRequest);
        return orderMapper.orderModelToSingleDishOrderResponseDto(orderModelResponse);
    }

    @Transactional
    @Override
    public List<OrderDishTypeDtoResponse> addDishesToOrderWithMultipleDishesType(List<OrderDishTypeRequestDto> ordersDishesTypeRequest, Long idRestaurantFromOrder) {
        final OrderModel orderModelWithMultipleDishes = orderMapper.ordersDishesTypeRequestToOrderModel(ordersDishesTypeRequest, idRestaurantFromOrder);
        final OrderModel registeredOrderWithDishType = this.customerServicePort.addDishesToOrderWithMultipleDishesType(orderModelWithMultipleDishes);
        return orderMapper.mapOrderModelToOrderDishTypeDtoResponse(registeredOrderWithDishType);
    }
}
