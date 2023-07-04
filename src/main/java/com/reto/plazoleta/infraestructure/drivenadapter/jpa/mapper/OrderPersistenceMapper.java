package com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper;

import com.reto.plazoleta.domain.model.CategoryModel;
import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import com.reto.plazoleta.domain.model.orders.OrderDishModel;
import com.reto.plazoleta.domain.model.orders.OrderModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.model.dishes.DishModel;
import com.reto.plazoleta.domain.model.dishes.FlanDessertDish;
import com.reto.plazoleta.domain.model.dishes.IceCreamDessertDish;
import com.reto.plazoleta.domain.model.dishes.MeatDish;
import com.reto.plazoleta.domain.model.dishes.SoupDish;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.CategoryEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.DishEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.EmployeeRestaurantEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.OrderDishEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.OrderEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.RestaurantEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.TypeDish;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.stream.Collectors;

@UtilityClass
public class OrderPersistenceMapper {

    public static OrderModel convertOrderEntityToOrderModel(OrderEntity orderEntity) {
        if (orderEntity.getOrdersDishesEntity() == null) {
            orderEntity.setOrdersDishesEntity(Collections.emptyList());
        }
        return new OrderModel(
                orderEntity.getIdOrder(),
                orderEntity.getIdUserCustomer(),
                orderEntity.getDate(),
                orderEntity.getStatus(),
                convertEntityToEmployeeRestaurantModel(orderEntity.getEmployeeRestaurantEntity()),
                convertEntityToRestaurantModel(orderEntity.getRestaurantEntity()),
                orderEntity.getOrdersDishesEntity()
                        .stream()
                        .map(OrderPersistenceMapper::convertOrderDishEntityToOrderDishModel)
                        .collect(Collectors.toList())
        );
    }

    private EmployeeRestaurantModel convertEntityToEmployeeRestaurantModel(EmployeeRestaurantEntity employeeRestaurantEntity) {
        if (employeeRestaurantEntity != null) {
            return new EmployeeRestaurantModel(
                    employeeRestaurantEntity.getIdRestaurantEmployee(),
                    employeeRestaurantEntity.getIdUserEmployee(),
                    employeeRestaurantEntity.getIdRestaurant()
            );
        }
        return null;
    }

    private RestaurantModel convertEntityToRestaurantModel(RestaurantEntity restaurantEntity) {
        return new RestaurantModel(
                restaurantEntity.getIdRestaurant(),
                restaurantEntity.getName(),
                restaurantEntity.getAddress(),
                restaurantEntity.getPhone(),
                restaurantEntity.getUrlLogo(),
                restaurantEntity.getNit(),
                restaurantEntity.getIdOwner()
        );
    }

    private OrderDishModel convertOrderDishEntityToOrderDishModel(OrderDishEntity orderDishEntity) {
        return new OrderDishModel(
                orderDishEntity.getIdOrderDish(),
                mapOrderEntityToOrderModelWithoutListOrdersDishes(orderDishEntity.getOrderEntity()),
                convertDishEntityToDishModel(orderDishEntity),
                orderDishEntity.getAmount()
        );
    }

    private DishModel convertDishEntityToDishModel(OrderDishEntity orderDishEntity) {
        DishEntity dish = orderDishEntity.getDishEntity();
        CategoryEntity typeOfDish = dish.getCategoryEntity();
        String nameFromDishType = typeOfDish.getName().toString();
        if (nameFromDishType.equalsIgnoreCase(TypeDish.CARNE.toString())) {
            return convertToMeatDishModel( dish, orderDishEntity.getGramsDish());
        } else if (nameFromDishType.equalsIgnoreCase(TypeDish.SOPAS.toString())) {
            return convertToSoupDishModel( dish, orderDishEntity.getSideDish());
        } else if (nameFromDishType.equalsIgnoreCase(TypeDish.POSTRE_FLAN.toString())) {
            return convertToFlanDessertDishModel( dish, orderDishEntity.getSideDish());
        } else if (nameFromDishType.equalsIgnoreCase(TypeDish.POSTRE_HELADO.toString())) {
            return convertToIceCreamDessertDishModel( dish, orderDishEntity.getFlavor());
        } else {
            return null;
        }
    }

    private CategoryModel convertEntityToCategoryModel(CategoryEntity categoryEntity) {
        return new CategoryModel(
                categoryEntity.getIdCategory(),
                categoryEntity.getName().toString(),
                categoryEntity.getDescription()
        );
    }

    private MeatDish convertToMeatDishModel(DishEntity dishEntity, Integer meatGrams) {
        return new MeatDish(
                dishEntity.getIdDish(),
                dishEntity.getName(),
                dishEntity.getDescription(),
                dishEntity.getPrice(),
                dishEntity.getUrlImageDish(),
                dishEntity.getState(),
                convertEntityToRestaurantModel(dishEntity.getRestaurantEntity()),
                convertEntityToCategoryModel(dishEntity.getCategoryEntity()),
                meatGrams
        );
    }

    private SoupDish convertToSoupDishModel(DishEntity dishEntity, String sideSoup) {
        return new SoupDish(
                dishEntity.getIdDish(),
                dishEntity.getName(),
                dishEntity.getDescription(),
                dishEntity.getPrice(),
                dishEntity.getUrlImageDish(),
                dishEntity.getState(),
                convertEntityToRestaurantModel(dishEntity.getRestaurantEntity()),
                convertEntityToCategoryModel(dishEntity.getCategoryEntity()),
                sideSoup
        );
    }

    private FlanDessertDish convertToFlanDessertDishModel(DishEntity dishEntity, String flanTopping) {
        return new FlanDessertDish(
                dishEntity.getIdDish(),
                dishEntity.getName(),
                dishEntity.getDescription(),
                dishEntity.getPrice(),
                dishEntity.getUrlImageDish(),
                dishEntity.getState(),
                convertEntityToRestaurantModel(dishEntity.getRestaurantEntity()),
                convertEntityToCategoryModel(dishEntity.getCategoryEntity()),
                flanTopping
        );
    }

    private IceCreamDessertDish convertToIceCreamDessertDishModel(DishEntity dishEntity, String iceCreamFlavor) {
        return new IceCreamDessertDish(
                dishEntity.getIdDish(),
                dishEntity.getName(),
                dishEntity.getDescription(),
                dishEntity.getPrice(),
                dishEntity.getUrlImageDish(),
                dishEntity.getState(),
                convertEntityToRestaurantModel(dishEntity.getRestaurantEntity()),
                convertEntityToCategoryModel(dishEntity.getCategoryEntity()),
                iceCreamFlavor
        );
    }

    private OrderModel mapOrderEntityToOrderModelWithoutListOrdersDishes(OrderEntity orderEntity) {
        if (orderEntity != null) {
            return new OrderModel(
                    orderEntity.getIdOrder(),
                    orderEntity.getIdUserCustomer(),
                    orderEntity.getDate(),
                    orderEntity.getStatus(),
                    convertEntityToEmployeeRestaurantModel(orderEntity.getEmployeeRestaurantEntity()),
                    convertEntityToRestaurantModel(orderEntity.getRestaurantEntity())
            );
        }
        return null;
    }

    public static OrderEntity convertOrderModelToOrderEntity(OrderModel orderModelRequest) {
        return OrderEntity.builder()
                .idOrder(orderModelRequest.getIdOrder())
                .idUserCustomer(orderModelRequest.getIdUserCustomer())
                .date(orderModelRequest.getDate())
                .status(orderModelRequest.getStatus())
                .employeeRestaurantEntity(mapEmployeeRestaurantModelToEmployeeRestaurantEntity(orderModelRequest.getEmployeeRestaurantModel()))
                .restaurantEntity(mapRestaurantModelToRestaurantEntity(orderModelRequest.getRestaurantModel()))
                .ordersDishesEntity(orderModelRequest.getOrdersDishesModel().stream()
                        .map(orderDishModel -> mapOrderDishModelToOrderDishEntity(orderDishModel))
                        .collect(Collectors.toList())
                )
                .build();
    }

    private EmployeeRestaurantEntity mapEmployeeRestaurantModelToEmployeeRestaurantEntity(EmployeeRestaurantModel employeeRestaurantModelRequest) {
        if (employeeRestaurantModelRequest != null) {
            return EmployeeRestaurantEntity.builder()
                    .idRestaurantEmployee(employeeRestaurantModelRequest.getIdRestaurantEmployee())
                    .idUserEmployee(employeeRestaurantModelRequest.getIdUserEmployee())
                    .idRestaurant(employeeRestaurantModelRequest.getIdRestaurant())
                    .build();
        }
        return null;
    }

    private RestaurantEntity mapRestaurantModelToRestaurantEntity(RestaurantModel restaurantModelRequest) {
        if (restaurantModelRequest != null) {
            return RestaurantEntity.builder()
                    .idRestaurant(restaurantModelRequest.getIdRestaurant())
                    .name(restaurantModelRequest.getName())
                    .phone(restaurantModelRequest.getPhone())
                    .address(restaurantModelRequest.getAddress())
                    .urlLogo(restaurantModelRequest.getUrlLogo())
                    .nit(restaurantModelRequest.getNit())
                    .idOwner(restaurantModelRequest.getIdOwner())
                    .build();
        }
        return null;
    }

    private OrderDishEntity mapOrderDishModelToOrderDishEntity(OrderDishModel orderDishModel) {
        return OrderDishEntity.builder()
                .idOrderDish(orderDishModel.getIdOrderDish())
                .orderEntity(mapOrderModelWithoutListOrdersDishesToOrderEntity(orderDishModel.getOrderModel()))
                .dishEntity(mapDishModelToDishEntity(orderDishModel.getDishModel()))
                .amount(orderDishModel.getAmount())
                .gramsDish(dishModelToIntegerAsGramsOfMeat(orderDishModel.getDishModel()))
                .sideDish(dishModelToStringAsSoupOrFlanSideDish(orderDishModel.getDishModel()))
                .flavor(dishModelToStringAsIceCreamFlavor(orderDishModel.getDishModel()))
                .build();
    }

    private OrderEntity mapOrderModelWithoutListOrdersDishesToOrderEntity(OrderModel orderModel) {
        return OrderEntity.builder()
                .idOrder(orderModel.getIdOrder())
                .idUserCustomer(orderModel.getIdUserCustomer())
                .date(orderModel.getDate())
                .status(orderModel.getStatus())
                .employeeRestaurantEntity(mapEmployeeRestaurantModelToEmployeeRestaurantEntity(orderModel.getEmployeeRestaurantModel()))
                .restaurantEntity(mapRestaurantModelToRestaurantEntity(orderModel.getRestaurantModel()))
                .ordersDishesEntity(Collections.emptyList())
                .build();
    }

    private DishEntity mapDishModelToDishEntity(DishModel dishModel) {
        return DishEntity.builder()
                .idDish(dishModel.getIdDish())
                .name(dishModel.getName())
                .description(dishModel.getDescription())
                .price(dishModel.getPrice())
                .urlImageDish(dishModel.getUrlImageDish())
                .state(dishModel.getState())
                .restaurantEntity(mapRestaurantModelToRestaurantEntity(dishModel.getRestaurantModel()))
                .categoryEntity(mapCategoryModelToCategoryEntity(dishModel.getCategoryModel()))
                .build();
    }

    private CategoryEntity mapCategoryModelToCategoryEntity(CategoryModel categoryModel) {
        return CategoryEntity.builder()
                .idCategory(categoryModel.getIdCategory())
                .description(categoryModel.getDescription())
                .name(TypeDish.valueOf(categoryModel.getName()))
                .build();
    }

    private Integer dishModelToIntegerAsGramsOfMeat(DishModel dish) {
        if (dish instanceof MeatDish) {
            return ((MeatDish) dish).getGrams();
        }
        return null;
    }

    private String dishModelToStringAsSoupOrFlanSideDish(DishModel dish) {
        if (dish instanceof SoupDish) {
            return ((SoupDish) dish).getSideDish();
        } else if (dish instanceof FlanDessertDish) {
            return ((FlanDessertDish) dish).getTopping();
        }
        return null;
    }

    private String dishModelToStringAsIceCreamFlavor(DishModel dish) {
        if (dish instanceof IceCreamDessertDish) {
            return ((IceCreamDessertDish) dish).getFlavor();
        }
        return null;
    }
}
