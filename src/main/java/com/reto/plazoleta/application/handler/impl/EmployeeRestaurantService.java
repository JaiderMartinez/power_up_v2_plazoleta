package com.reto.plazoleta.application.handler.impl;

import com.reto.plazoleta.application.dto.response.OrdersPaginatedResponseDto;
import com.reto.plazoleta.application.handler.IEmployeeRestaurantService;
import com.reto.plazoleta.application.mapper.responsemapper.ICustomerResponseMapper;
import com.reto.plazoleta.domain.api.IEmployeeRestaurantServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeRestaurantService implements IEmployeeRestaurantService {

    private final IEmployeeRestaurantServicePort employeeRestaurantServicePort;
    private final ICustomerResponseMapper customerResponseMapper;

    @Override
    public Page<OrdersPaginatedResponseDto> getAllOrdersFilterByStatus(Integer sizeItems, Integer pageNumber, String status, String tokenWithPrefixBearer) {
        return this.employeeRestaurantServicePort
                .getAllOrdersFilterByStatusAndSizeItemsByPage(sizeItems, pageNumber, status, tokenWithPrefixBearer)
                .map(customerResponseMapper::orderModelToOrdersPaginatedResponseDto);
    }
}
