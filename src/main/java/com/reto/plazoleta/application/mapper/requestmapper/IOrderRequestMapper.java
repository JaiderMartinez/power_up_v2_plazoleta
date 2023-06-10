package com.reto.plazoleta.application.mapper.requestmapper;

import com.reto.plazoleta.application.dto.request.OrderRequestDto;
import com.reto.plazoleta.application.dto.request.DishFromOrderAndAmountRequestDto;
import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.domain.model.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IOrderRequestMapper {

    @Mapping(target = "restaurantModel.idRestaurant", source = "idRestaurant")
    OrderModel toOrderModel(OrderRequestDto createOrderRequestDto);

    @Mapping(target = "dishModel.idDish", source = "idDish")
    @Mapping(target = "dishModel.name", source = "name")
    OrderDishModel toOrderDishModel(DishFromOrderAndAmountRequestDto dishFromOrderAndAmountRequestDto);
}
