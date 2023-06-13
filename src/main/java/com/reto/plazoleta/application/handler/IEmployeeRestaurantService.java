package com.reto.plazoleta.application.handler;

import com.reto.plazoleta.application.dto.response.OrdersPaginatedResponseDto;
import org.springframework.data.domain.Page;

public interface IEmployeeRestaurantService {

    Page<OrdersPaginatedResponseDto> getAllOrdersFilterByStatus(Integer sizeItems, Integer pageNumber, String status, String tokenWithPrefixBearer);
}
