package com.reto.plazoleta.application.mapper.responsemapper;

import com.reto.plazoleta.application.dto.response.CategoryFromDishesPaginatedResponseDto;
import com.reto.plazoleta.application.dto.response.DishResponseDto;
import com.reto.plazoleta.application.dto.response.OrderCanceledResponseDto;
import com.reto.plazoleta.application.dto.response.OrderCreatedResponseDto;
import com.reto.plazoleta.application.dto.response.OrdersDishesPaginatedResponseDto;
import com.reto.plazoleta.application.dto.response.OrdersPaginatedResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantResponsePageableDto;
import com.reto.plazoleta.domain.model.dishes.DishModel;
import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;

@Mapper
public interface ICustomerResponseMapper {

    RestaurantResponsePageableDto toRestaurantResponse(RestaurantModel restaurantModel);

    OrderCreatedResponseDto toCreateOrderResponseDto(OrderModel orderModel);

    @Mapping(target = "ordersDishes", source = "ordersDishesModel")
    OrdersPaginatedResponseDto orderModelToOrdersPaginatedResponseDto(OrderModel orderModel);

    @Mapping(target = "dish.idDish", source = "dishModel.idDish")
    @Mapping(target = "dish.name", source = "dishModel.name")
    @Mapping(target = "dish.dishImageUrl", source = "dishModel.urlImageDish")
    @Mapping(target = "dish.description", source = "dishModel.description")
    OrdersDishesPaginatedResponseDto orderDishModelToOrdersDishesPaginatedResponseDto(OrderDishModel orderDishModel);

    @Mapping(target = "idCustomer", source = "idUserCustomer")
    OrderCanceledResponseDto orderModelToOrderCanceledResponseDto(OrderModel orderModel);

    @Mapping(target = "idCategory", source = "entry.key")
    @Mapping(target = "categoryName", expression = "java(entry.getValue().get(0).getCategoryModel().getName())")
    @Mapping(target = "dishes", source = "entry.value", qualifiedByName = "mapDishModelListToDishResponseDtoList")
    CategoryFromDishesPaginatedResponseDto mapEntryToCategoryFromDishesPaginatedResponseDto(Map.Entry<Long, List<DishModel>> entry);

    @Named( value = "mapDishModelListToDishResponseDtoList")
    List<DishResponseDto> mapDishModelListToDishResponseDtoList(List<DishModel> dishesModels);


    @Mapping(target = "urlImage", source = "urlImageDish")
    DishResponseDto dishModelToDishResponseDto(DishModel dishModel);
}
