package com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper;

import com.reto.plazoleta.domain.model.orders.OrderDishModel;
import com.reto.plazoleta.domain.model.orders.OrderModel;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.OrderDishEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IOrderEntityMapper {

    @Mapping(target = "employeeRestaurantModel", source = "employeeRestaurantEntity")
    @Mapping(target = "restaurantModel", source = "restaurantEntity")
    @Mapping(target = "ordersDishesModel", source = "ordersDishesEntity")
    @Mapping(target = "ordersDishesModel.orderModel", ignore = true)
    OrderModel toOrderModel(OrderEntity orderEntity);

    @Mapping(target = "employeeRestaurantEntity", source = "employeeRestaurantModel")
    @Mapping(target = "restaurantEntity", source = "restaurantModel")
    @Mapping(target = "ordersDishesEntity", source = "ordersDishesModel")
    OrderEntity toOrderEntity(OrderModel orderModel);

    @Mapping(target = "orderModel.idOrder",  expression = "java(orderDishEntity.getOrderEntity().getIdOrder())")
    @Mapping(target = "dishModel", source = "dishEntity")
    @Mapping(target = "dishModel.restaurantModel", source = "dishEntity.restaurantEntity")
    @Mapping(target = "dishModel.categoryModel", source = "dishEntity.categoryEntity")
    OrderDishModel toOrderDishModel(OrderDishEntity orderDishEntity);

    @Mapping(target = "orderEntity.idOrder", expression = "java(orderDishModel.getOrderModel().getIdOrder())")
    @Mapping(target = "dishEntity", source = "dishModel")
    @Mapping(target = "dishEntity.restaurantEntity", source = "dishModel.restaurantModel")
    @Mapping(target = "dishEntity.categoryEntity", source = "dishModel.categoryModel")
    OrderDishEntity toOrderDishEntity(OrderDishModel orderDishModel);
}
