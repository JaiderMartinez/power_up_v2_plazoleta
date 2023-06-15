package com.reto.plazoleta.application.mapper.responsemapper;

import com.reto.plazoleta.application.dto.response.OrderCreatedResponseDto;
import com.reto.plazoleta.application.dto.response.OrdersDishesPaginatedResponseDto;
import com.reto.plazoleta.application.dto.response.OrdersPaginatedResponseDto;
import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.domain.model.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ICustomerResponseMapper {

    OrderCreatedResponseDto toCreateOrderResponseDto(OrderModel orderModel);

    @Mapping(target = "ordersDishes", source = "ordersDishesModel")
    OrdersPaginatedResponseDto orderModelToOrdersPaginatedResponseDto(OrderModel orderModel);

    @Mapping(target = "dish.idDish", source = "dishModel.idDish")
    @Mapping(target = "dish.name", source = "dishModel.name")
    @Mapping(target = "dish.dishImageUrl", source = "dishModel.urlImageDish")
    @Mapping(target = "dish.description", source = "dishModel.description")
    OrdersDishesPaginatedResponseDto orderDishModelToOrdersDishesPaginatedResponseDto(OrderDishModel orderDishModel);
}
