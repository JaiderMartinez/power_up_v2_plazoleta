package com.reto.plazoleta.application.mapper;

import com.reto.plazoleta.application.dto.request.OrderDishTypeRequestDto;
import com.reto.plazoleta.application.dto.request.SingleDishOrderRequestDto;
import com.reto.plazoleta.application.dto.response.OrderDishTypeDtoResponse;
import com.reto.plazoleta.application.dto.response.SingleDishOrderResponseDto;
import com.reto.plazoleta.application.dto.response.pending_orders.PendingDishResponseDto;
import com.reto.plazoleta.application.dto.response.pending_orders.PendingOrderResponseDto;
import com.reto.plazoleta.application.dto.response.takenorder.DishTypeOrderedResponseDto;
import com.reto.plazoleta.application.dto.response.takenorder.OrderTakenResponseDto;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.model.orders.OrderDishModel;
import com.reto.plazoleta.domain.model.orders.OrderModel;
import com.reto.plazoleta.domain.model.dishes.DishModel;
import com.reto.plazoleta.domain.model.dishes.FlanDessertDish;
import com.reto.plazoleta.domain.model.dishes.IceCreamDessertDish;
import com.reto.plazoleta.domain.model.dishes.MeatDish;
import com.reto.plazoleta.domain.model.dishes.SoupDish;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class OrderMapper {

    private static final String TYPE_DISH_MEAT = "Carne";
    private static final String TYPE_DISH_SOUP = "Sopas";
    private static final String TYPE_DISH_DESSERT = "Postre";
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
        return DishTypeOrderedResponseDto.builder()
                .typeDish(TYPE_DISH_MEAT)
                .grams(meatDish.getGrams())
                .build();
    }

    private DishTypeOrderedResponseDto buildSoupDishTypeOrderedResponse(SoupDish soupDish) {
        return DishTypeOrderedResponseDto.builder()
                .typeDish(TYPE_DISH_SOUP)
                .sideDish(soupDish.getSideDish())
                .build();
    }

    private DishTypeOrderedResponseDto buildFlanDessertTypeOrderedResponse(FlanDessertDish flanDessertDish) {
        return DishTypeOrderedResponseDto.builder()
                .typeDish(TYPE_DISH_DESSERT)
                .typeDessert(TYPE_DISH_FLAN_DESSERT)
                .sideDish(flanDessertDish.getTopping())
                .build();
    }

    private DishTypeOrderedResponseDto buildIceCreamDessertTypeOrderedResponse(IceCreamDessertDish iceCreamDessertDish) {
        return DishTypeOrderedResponseDto.builder()
                .typeDish(TYPE_DISH_DESSERT)
                .typeDessert(TYPE_DISH_ICE_CREAM_DESSERT)
                .flavor(iceCreamDessertDish.getFlavor())
                .build();
    }

    public static PendingOrderResponseDto orderModelToPendingOrderResponseDto(OrderModel order) {
        return PendingOrderResponseDto.builder()
                .idOrder(order.getIdOrder())
                .idUserCustomer(order.getIdUserCustomer())
                .date(order.getDate())
                .status(order.getStatus().toString())
                .dishes(order.getOrdersDishesModel().stream()
                        .map(OrderMapper::orderDishModelToPendingDishResponseDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private PendingDishResponseDto orderDishModelToPendingDishResponseDto(OrderDishModel orderDish) {
        PendingDishResponseDto pendingDishResponseDto = dishModelToPendingDishResponseDto(orderDish.getDishModel());
        return PendingDishResponseDto.builder()
                .idDish(orderDish.getDishModel().getIdDish())
                .typeDish(pendingDishResponseDto != null ? pendingDishResponseDto.getTypeDish() : null)
                .typeDessert(pendingDishResponseDto != null ? pendingDishResponseDto.getTypeDessert() : null)
                .sideDish(pendingDishResponseDto != null ? pendingDishResponseDto.getSideDish() : null)
                .flavor(pendingDishResponseDto != null ? pendingDishResponseDto.getFlavor() : null)
                .grams(pendingDishResponseDto != null ? pendingDishResponseDto.getGrams() : null)
                .build();
    }

    private PendingDishResponseDto dishModelToPendingDishResponseDto(DishModel dish) {
        if (dish instanceof MeatDish) {
            return buildMeatDishToPendingDishResponseDto((MeatDish) dish);
        } else if (dish instanceof SoupDish) {
            return buildSoupDishToPendingDishResponseDto((SoupDish) dish);
        } else if (dish instanceof FlanDessertDish) {
            return buildFlanDessertToPendingDishResponseDto((FlanDessertDish) dish);
        } else if (dish instanceof IceCreamDessertDish) {
            return buildIceCreamDessertToPendingDishResponseDto((IceCreamDessertDish) dish);
        }
        return null;
    }

    private PendingDishResponseDto buildMeatDishToPendingDishResponseDto(MeatDish meatDish) {
        return PendingDishResponseDto.builder()
                .typeDish(TYPE_DISH_MEAT)
                .grams(meatDish.getGrams())
                .build();
    }

    private PendingDishResponseDto buildSoupDishToPendingDishResponseDto(SoupDish soupDish) {
        return PendingDishResponseDto.builder()
                .typeDish(TYPE_DISH_SOUP)
                .sideDish(soupDish.getSideDish())
                .build();
    }

    private PendingDishResponseDto buildFlanDessertToPendingDishResponseDto(FlanDessertDish flanDessertDish) {
        DishTypeOrderedResponseDto dishTypeOrderedResponse = new DishTypeOrderedResponseDto();
        dishTypeOrderedResponse.setTypeDessert(TYPE_DISH_FLAN_DESSERT);
        dishTypeOrderedResponse.setSideDish(flanDessertDish.getTopping());
        return PendingDishResponseDto.builder()
                .typeDish(TYPE_DISH_DESSERT)
                .typeDessert(TYPE_DISH_FLAN_DESSERT)
                .sideDish(flanDessertDish.getTopping())
                .build();
    }

    private PendingDishResponseDto buildIceCreamDessertToPendingDishResponseDto(IceCreamDessertDish iceCreamDessertDish) {
        return PendingDishResponseDto.builder()
                .typeDish(TYPE_DISH_DESSERT)
                .typeDessert(TYPE_DISH_FLAN_DESSERT)
                .flavor(iceCreamDessertDish.getFlavor())
                .build();
    }

    public static OrderModel singleDishOrderRequestDtoToOrderModel(SingleDishOrderRequestDto singleDishOrderRequestDto, Long idRestaurant) {
        return new OrderModel(
                null,
                null,
                LocalDate.now(),
                null,
                null,
                longAsIdRestaurantToRestaurantModel(idRestaurant),
                Collections.singletonList(dishFromOrderRequestDtoToOrderDishModel(singleDishOrderRequestDto))
        );
    }

    private RestaurantModel longAsIdRestaurantToRestaurantModel(Long idRestaurant) {
        return new RestaurantModel(
                idRestaurant,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private OrderDishModel dishFromOrderRequestDtoToOrderDishModel(SingleDishOrderRequestDto singleDishOrderRequestDto) {
        return new OrderDishModel(
                null,
                null,
                singleDishOrderRequestDtoToDishModel(singleDishOrderRequestDto),
                1
        );
    }

    private DishModel singleDishOrderRequestDtoToDishModel(SingleDishOrderRequestDto singleDishOrderRequestDto) {
        String typeDish = singleDishOrderRequestDto.getTypeDish();
        if (typeDish.equalsIgnoreCase(TYPE_DISH_MEAT)) {
            return buildSingleDishOrderRequestDtoToMeatDish(singleDishOrderRequestDto);
        } else if (typeDish.equalsIgnoreCase(TYPE_DISH_SOUP)) {
            return buildSingleDishOrderRequestDtoToSoupDish(singleDishOrderRequestDto);
        } else if (typeDish.equalsIgnoreCase(TYPE_DISH_DESSERT)) {
            String typeDessert = singleDishOrderRequestDto.getTypeDessert();
            if (typeDessert.equalsIgnoreCase(TYPE_DISH_FLAN_DESSERT)) {
                return buildSingleDishOrderRequestDtoToFlanDessertDish(singleDishOrderRequestDto);
            } else if (typeDessert.equalsIgnoreCase(TYPE_DISH_ICE_CREAM_DESSERT)) {
                return buildSingleDishOrderRequestDtoToIceCreamDessertDish(singleDishOrderRequestDto);
            }
        }
        return null;
    }

    private MeatDish buildSingleDishOrderRequestDtoToMeatDish(SingleDishOrderRequestDto singleMeatDishRequest) {
        return new MeatDish(singleMeatDishRequest.getIdDish(), singleMeatDishRequest.getGrams());
    }

    private SoupDish buildSingleDishOrderRequestDtoToSoupDish(SingleDishOrderRequestDto singleSoupDishRequest) {
        return new SoupDish(singleSoupDishRequest.getIdDish(), singleSoupDishRequest.getSideDish());
    }

    private FlanDessertDish buildSingleDishOrderRequestDtoToFlanDessertDish(SingleDishOrderRequestDto singleFlanDessertDishRequest) {
        return new FlanDessertDish(singleFlanDessertDishRequest.getIdDish(), singleFlanDessertDishRequest.getSideDish());
    }

    private IceCreamDessertDish buildSingleDishOrderRequestDtoToIceCreamDessertDish(SingleDishOrderRequestDto singleIceCreamDessertDishRequest) {
        return new IceCreamDessertDish(singleIceCreamDessertDishRequest.getIdDish(), singleIceCreamDessertDishRequest.getFlavor());
    }

    public static SingleDishOrderResponseDto orderModelToSingleDishOrderResponseDto(OrderModel orderModel) {
        return SingleDishOrderResponseDto.builder()
                .idOrder(orderModel.getIdOrder())
                .build();
    }

    public static OrderModel ordersDishesTypeRequestToOrderModel(List<OrderDishTypeRequestDto> ordersDishesTypeRequest, Long idRestaurant) {
        return new OrderModel(
                null,
                null,
                LocalDate.now(),
                null,
                null,
                longAsIdRestaurantToRestaurantModel(idRestaurant),
                ordersDishesTypeRequest.stream()
                        .map(OrderMapper::orderDishTypeRequestDtoToOrderDishModel)
                        .collect(Collectors.toList())
        );
    }

    private OrderDishModel orderDishTypeRequestDtoToOrderDishModel(OrderDishTypeRequestDto orderDishTypeRequestDto) {
        return new OrderDishModel(
                null,
                null,
                orderDishTypeRequestDtoToDishModel(orderDishTypeRequestDto),
                1
        );
    }

    private DishModel orderDishTypeRequestDtoToDishModel(OrderDishTypeRequestDto orderDishTypeRequestDto) {
        String typeDish = orderDishTypeRequestDto.getTypeDish();
        if (typeDish.equalsIgnoreCase(TYPE_DISH_MEAT)) {
            return mapOrderDishTypeRequestDtoToMeatDish(orderDishTypeRequestDto);
        } else if (typeDish.equalsIgnoreCase(TYPE_DISH_SOUP)) {
            return mapOrderDishTypeRequestDtoToSoupDish(orderDishTypeRequestDto);
        } else if (typeDish.equalsIgnoreCase(TYPE_DISH_DESSERT)) {
            String typeDessert = orderDishTypeRequestDto.getTypeDessert();
            if (typeDessert.equalsIgnoreCase(TYPE_DISH_FLAN_DESSERT)) {
                return mapOrderDishTypeRequestDtoToFlanDessertDish(orderDishTypeRequestDto);
            } else if (typeDessert.equalsIgnoreCase(TYPE_DISH_ICE_CREAM_DESSERT)) {
                return mapOrderDishTypeRequestDtoToIceCreamDessertDish(orderDishTypeRequestDto);
            }
        }
        return null;
    }

    private MeatDish mapOrderDishTypeRequestDtoToMeatDish(OrderDishTypeRequestDto meatTypeDish) {
        return new MeatDish(meatTypeDish.getIdDish(), meatTypeDish.getGrams());
    }

    private SoupDish mapOrderDishTypeRequestDtoToSoupDish(OrderDishTypeRequestDto soupTypeDish) {
        return new SoupDish(soupTypeDish.getIdDish(), soupTypeDish.getSideDish());
    }

    private FlanDessertDish mapOrderDishTypeRequestDtoToFlanDessertDish(OrderDishTypeRequestDto flanDessertTypeDish) {
        return new FlanDessertDish(flanDessertTypeDish.getIdDish(), flanDessertTypeDish.getSideDish());
    }

    private IceCreamDessertDish mapOrderDishTypeRequestDtoToIceCreamDessertDish(OrderDishTypeRequestDto iceCreamDessertTypeDish) {
        return new IceCreamDessertDish(iceCreamDessertTypeDish.getIdDish(), iceCreamDessertTypeDish.getFlavor());
    }

    public static List<OrderDishTypeDtoResponse> mapOrderModelToOrderDishTypeDtoResponse(OrderModel orderSaved) {
        return orderSaved.getOrdersDishesModel().stream()
                .map(OrderMapper::mapOrderDishModelToResponse)
                .collect(Collectors.toList());
    }

    private OrderDishTypeDtoResponse mapOrderDishModelToResponse(OrderDishModel orderDishModel) {
        return OrderDishTypeDtoResponse.builder()
                .idOrder(orderDishModel.getOrderModel().getIdOrder())
                .idOrderDish(orderDishModel.getIdOrderDish())
                .idDish(orderDishModel.getDishModel().getIdDish())
                .build();
    }
}
