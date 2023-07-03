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
                convertDishEntityToDishModel(orderDishEntity.getDishEntity(), orderDishEntity),
                orderDishEntity.getAmount(),
                orderDishEntity.getGramsDish(),
                orderDishEntity.getSideDish(),
                orderDishEntity.getFlavor()
        );
    }

    private DishModel convertDishEntityToDishModel(DishEntity dishEntity, OrderDishEntity orderDishEntity) {
        String dishType = dishEntity.getCategoryEntity().getName().toString();
        if (dishType.equalsIgnoreCase(TypeDish.CARNE.toString())) {
            return convertToMeatDishModel(dishEntity, orderDishEntity.getGramsDish());
        } else if (dishType.equalsIgnoreCase(TypeDish.SOPAS.toString())) {
            return convertToSoupDishModel(dishEntity, orderDishEntity.getSideDish());
        } else if (dishType.equalsIgnoreCase(TypeDish.POSTRE_FLAN.toString())) {
            return convertToFlanDessertDishModel(dishEntity, orderDishEntity.getSideDish());
        } else if (dishType.equalsIgnoreCase(TypeDish.POSTRE_HELADO.toString())) {
            return convertToIceCreamDessertDishModel(dishEntity, orderDishEntity.getFlavor());
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
        return new OrderModel(
                orderEntity.getIdOrder(),
                orderEntity.getIdUserCustomer(),
                orderEntity.getDate(),
                orderEntity.getStatus(),
                convertEntityToEmployeeRestaurantModel(orderEntity.getEmployeeRestaurantEntity()),
                convertEntityToRestaurantModel(orderEntity.getRestaurantEntity())
        );
    }
}
