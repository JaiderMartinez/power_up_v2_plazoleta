package com.reto.plazoleta.application.mapper.requestmapper;

import com.reto.plazoleta.application.dto.request.DishFromOrderRequestDto;
import com.reto.plazoleta.application.dto.request.OrderRequestDto;
import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.domain.model.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ICustomerRequestMapper {

    @Mapping(target = "restaurantModel.idRestaurant", source = "idRestaurant")
    OrderModel orderRequestDtoToOrderModel(OrderRequestDto orderRequestDto);

    @Mapping(target = "dishModel.idDish", source = "idDish")
    @Mapping(target = "dishModel.name", source = "name")
    OrderDishModel dishFromOrderRequestDtoToOrderDishModel(DishFromOrderRequestDto dishFromOrderRequestDto);
}
