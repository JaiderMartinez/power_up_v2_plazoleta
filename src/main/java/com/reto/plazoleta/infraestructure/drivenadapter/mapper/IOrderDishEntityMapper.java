package com.reto.plazoleta.infraestructure.drivenadapter.mapper;

import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.OrderDishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IOrderDishEntityMapper {

    @Mapping(target = "orderEntity", source = "orderModel")
    @Mapping(target = "dishEntity", source = "dishModel")
    @Mapping(target = "orderEntity.employeeRestaurantEntity", source = "orderModel.employeeRestaurantModel")
    @Mapping(target = "orderEntity.restaurantEntity", source = "orderModel.restaurantModel")
    @Mapping(target = "dishEntity.restaurantEntity", source = "dishModel.restaurantModel")
    @Mapping(target = "dishEntity.categoryEntity", source = "dishModel.categoryModel")
    OrderDishEntity toOrderDishEntity(OrderDishModel orderDishModel);
}
