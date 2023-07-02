package com.reto.plazoleta.application.mapper;

import com.reto.plazoleta.application.dto.response.takenorder.DishTypeOrderedResponseDto;
import com.reto.plazoleta.application.dto.response.takenorder.OrderTakenResponseDto;
import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.domain.model.dishes.DishModel;
import com.reto.plazoleta.domain.model.dishes.FlanDessertDish;
import com.reto.plazoleta.domain.model.dishes.IceCreamDessertDish;
import com.reto.plazoleta.domain.model.dishes.MeatDish;
import com.reto.plazoleta.domain.model.dishes.SoupDish;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

@UtilityClass
public class OrderMapper {

    private static final String TYPE_DISH_MEAT = "Carne";
    private static final String TYPE_DISH_SOUP = "Sopa";
    private static final String TYPE_DISH_ICE_CREAM_DESSERT = "Helado";
    private static final String TYPE_DISH_FLAN_DESSERT = "Flan";

    public static OrderTakenResponseDto orderModelToOrderTakenResponseDto(OrderModel orderModel) {
        return OrderTakenResponseDto.builder()
                .idOrder(orderModel.getIdOrder())
                .idUserCustomer(orderModel.getIdUserCustomer())
                .date(orderModel.getDate())
                .status(orderModel.getStatus().toString())
                .idChef(orderModel.getEmployeeRestaurantModel().getIdUserEmployee())
                .dishes(orderModel.getOrdersDishesModel().stream()
                        .map(OrderMapper::orderDishToDishTypeOrdered)
                        .collect(Collectors.toList()))
                .build();
    }

    private DishTypeOrderedResponseDto orderDishToDishTypeOrdered(OrderDishModel orderDishModel) {
        DishTypeOrderedResponseDto dishTypeOrderedResponseDto = dataModelToDishTypeOrderedResponseDto(orderDishModel.getDishModel());
        return DishTypeOrderedResponseDto.builder()
                .idDish(orderDishModel.getDishModel().getIdDish())
                .typeDish(dishTypeOrderedResponseDto != null ? dishTypeOrderedResponseDto.getTypeDish() : null)
                .typeDessert(dishTypeOrderedResponseDto != null ? dishTypeOrderedResponseDto.getTypeDessert() : null)
                .sideDish(dishTypeOrderedResponseDto != null ? dishTypeOrderedResponseDto.getSideDish() : null)
                .flavor(dishTypeOrderedResponseDto != null ? dishTypeOrderedResponseDto.getFlavor() : null)
                .grams(dishTypeOrderedResponseDto != null ? dishTypeOrderedResponseDto.getGrams() : null)
                .build();
    }

    private DishTypeOrderedResponseDto dataModelToDishTypeOrderedResponseDto(DishModel dish) {
        if (dish instanceof MeatDish) {
            return buildMeatDishTypeOrderedResponse((MeatDish) dish);
        } else if (dish instanceof SoupDish) {
            return buildSoupDishTypeOrderedResponse((SoupDish) dish);
        } else if (dish instanceof FlanDessertDish) {
            return buildFlanDessertTypeOrderedResponse((FlanDessertDish) dish);
        } else if (dish instanceof IceCreamDessertDish) {
            return buildIceCreamDessertTypeOrderedResponse((IceCreamDessertDish) dish);
        }
        return null;
    }

    private DishTypeOrderedResponseDto buildMeatDishTypeOrderedResponse(MeatDish meatDish) {
        DishTypeOrderedResponseDto dishTypeOrderedResponse = new DishTypeOrderedResponseDto();
        dishTypeOrderedResponse.setTypeDish(TYPE_DISH_MEAT);
        dishTypeOrderedResponse.setGrams(meatDish.getGrams());
        return dishTypeOrderedResponse;
    }

    private DishTypeOrderedResponseDto buildSoupDishTypeOrderedResponse(SoupDish soupDish) {
        DishTypeOrderedResponseDto dishTypeOrderedResponse = new DishTypeOrderedResponseDto();
        dishTypeOrderedResponse.setTypeDish(TYPE_DISH_SOUP);
        dishTypeOrderedResponse.setSideDish(soupDish.getSideDish());
        return dishTypeOrderedResponse;
    }

    private DishTypeOrderedResponseDto buildFlanDessertTypeOrderedResponse(FlanDessertDish flanDessertDish) {
        DishTypeOrderedResponseDto dishTypeOrderedResponse = new DishTypeOrderedResponseDto();
        dishTypeOrderedResponse.setTypeDessert(TYPE_DISH_FLAN_DESSERT);
        dishTypeOrderedResponse.setSideDish(flanDessertDish.getTopping());
        return dishTypeOrderedResponse;
    }

    private DishTypeOrderedResponseDto buildIceCreamDessertTypeOrderedResponse(IceCreamDessertDish iceCreamDessertDish) {
        DishTypeOrderedResponseDto dishTypeOrderedResponse = new DishTypeOrderedResponseDto();
        dishTypeOrderedResponse.setTypeDessert(TYPE_DISH_ICE_CREAM_DESSERT);
        dishTypeOrderedResponse.setFlavor(iceCreamDessertDish.getFlavor());
        return dishTypeOrderedResponse;
    }
}
