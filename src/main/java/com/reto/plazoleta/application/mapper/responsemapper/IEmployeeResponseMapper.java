package com.reto.plazoleta.application.mapper.responsemapper;

import com.reto.plazoleta.application.dto.response.AssignedOrdersResponseDto;
import com.reto.plazoleta.application.dto.response.OrderDeliveredResponseDto;
import com.reto.plazoleta.application.dto.response.OrderNotificationResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantEmployeeResponseDto;
import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import com.reto.plazoleta.domain.model.OrderModel;
import org.mapstruct.Mapper;

@Mapper
public interface IEmployeeResponseMapper {

    RestaurantEmployeeResponseDto toRestaurantEmployeeResponseDto(EmployeeRestaurantModel employeeRestaurantModel);

    AssignedOrdersResponseDto orderModelToAssignedOrdersResponseDto(OrderModel orderModel);

    OrderNotificationResponseDto orderModelToOrderNotificationResponseDto(OrderModel orderModel);
  
    OrderDeliveredResponseDto orderModelToOrderDeliveredResponseDto(OrderModel orderModel);
}
