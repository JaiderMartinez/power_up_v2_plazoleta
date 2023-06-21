package com.reto.plazoleta.application.handler;

import com.reto.plazoleta.application.dto.response.AssignedOrdersResponseDto;
import com.reto.plazoleta.application.dto.response.OrderDeliveredResponseDto;
import com.reto.plazoleta.application.dto.response.OrdersPaginatedResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IEmployeeRestaurantService {

    Page<OrdersPaginatedResponseDto> getAllOrdersFilterByStatus(Integer sizeItems, Integer pageNumber, String status, String tokenWithPrefixBearer);

    List<AssignedOrdersResponseDto> assignOrderAndChangeStatusToInPreparation(List<Long> idOrders, String tokenWithPrefixBearer);

    OrderDeliveredResponseDto changeOrderStatusToDelivered(Long orderPin, String tokenWithPrefixBearer);
}
