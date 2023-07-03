package com.reto.plazoleta.application.handler.impl;

import com.reto.plazoleta.application.dto.response.takenorder.OrderTakenResponseDto;
import com.reto.plazoleta.application.handler.IOrderHandler;
import com.reto.plazoleta.application.mapper.OrderMapper;
import com.reto.plazoleta.domain.api.IEmployeeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class OrderHandlerImplementation implements IOrderHandler {

    private final IEmployeeServicePort employeeServicePort;
    private OrderMapper orderMapper;

    @Transactional
    @Override
    public OrderTakenResponseDto getOrderByPriority() {
        return orderMapper.orderModelToOrderTakenResponseDto(
                this.employeeServicePort.takeOrderByPriorityInStatusEarring()
        );
    }
}
