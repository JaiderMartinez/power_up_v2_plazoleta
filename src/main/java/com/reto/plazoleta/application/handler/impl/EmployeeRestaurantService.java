package com.reto.plazoleta.application.handler.impl;

import com.reto.plazoleta.application.dto.response.AssignedOrdersResponseDto;
import com.reto.plazoleta.application.dto.response.OrdersPaginatedResponseDto;
import com.reto.plazoleta.application.handler.IEmployeeRestaurantService;
import com.reto.plazoleta.application.mapper.responsemapper.ICustomerResponseMapper;
import com.reto.plazoleta.application.mapper.responsemapper.IEmployeeResponseMapper;
import com.reto.plazoleta.domain.api.IEmployeeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeRestaurantService implements IEmployeeRestaurantService {

    private final IEmployeeServicePort employeeRestaurantServicePort;
    private final ICustomerResponseMapper customerResponseMapper;
    private final IEmployeeResponseMapper employeeResponseMapper;

    @Override
    public Page<OrdersPaginatedResponseDto> getAllOrdersFilterByStatus(Integer sizeItems, Integer pageNumber, String status, String tokenWithPrefixBearer) {
        return this.employeeRestaurantServicePort
                .getAllOrdersFilterByStatusAndSizeItemsByPage(sizeItems, pageNumber, status, tokenWithPrefixBearer)
                .map(customerResponseMapper::orderModelToOrdersPaginatedResponseDto);
    }

    @Override
    public List<AssignedOrdersResponseDto> assignOrderAndChangeStatusToInPreparation(List<Long> idOrders, String tokenWithPrefixBearer) {
        return this.employeeRestaurantServicePort.assignEmployeeToOrderAndChangeStatusToInPreparation(idOrders, tokenWithPrefixBearer).stream()
                .map(employeeResponseMapper::orderModelToAssignedOrdersResponseDto).collect(Collectors.toList());
    }
}
