package com.reto.plazoleta.infraestructure.drivenadapter.mapper;

import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IOrderEntityMapper {

    @Mapping(target = "employeeRestaurantModel", source = "employeeRestaurantEntity")
    @Mapping(target = "restaurantModel", source = "restaurantEntity")
    OrderModel toOrderModel(OrderEntity orderEntity);

    @Mapping(target = "employeeRestaurantEntity", source = "employeeRestaurantModel")
    @Mapping(target = "restaurantEntity", source = "restaurantModel")
    OrderEntity toOrderEntity(OrderModel orderModel);
}
