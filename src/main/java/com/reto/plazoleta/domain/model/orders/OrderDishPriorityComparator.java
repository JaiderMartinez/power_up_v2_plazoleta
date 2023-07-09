package com.reto.plazoleta.domain.model.orders;

import com.reto.plazoleta.domain.model.dishes.DessertDish;
import com.reto.plazoleta.domain.model.dishes.DishModel;
import com.reto.plazoleta.domain.model.dishes.FlanDessertDish;
import com.reto.plazoleta.domain.model.dishes.IceCreamDessertDish;
import com.reto.plazoleta.domain.model.dishes.MeatDish;
import com.reto.plazoleta.domain.model.dishes.SoupDish;

import java.util.Comparator;

public class OrderDishPriorityComparator implements Comparator<OrderDishModel> {

    private static final String RICE_GARNISH_SOUP_DISH = "ARROZ";
    private static final String YUCCA_GARNISH_SOUP_DISH = "YUCA";
    private static final String POTATO_GARNISH_SOUP_DISH = "PAPA";
    private static final int YUCCA_PRIORITY = 30;
    private static final int POTATO_PRIORITY = 25;
    private static final int RICE_PRIORITY = 20;
    private static final int FLAN_DESSERT_PRIORITY = 15;
    private static final int ICE_CREAM_DESSERT_PRIORITY = 10;

    @Override
    public int compare(OrderDishModel orderDishCurrent, OrderDishModel orderDishNext) {
        int orderDishPriority = calculateOrderDishPriorityTotal(orderDishCurrent);
        int nextOrderDishPriority = calculateOrderDishPriorityTotal(orderDishNext);
        return Integer.compare(orderDishPriority, nextOrderDishPriority);
    }

    private int calculateOrderDishPriorityTotal(OrderDishModel orderDish) {
        return calculatePriorityOfDish(orderDish.getDishModel()) * orderDish.getAmount();
    }

    private int calculatePriorityOfDish(DishModel dish) {
        if (dish instanceof MeatDish) {
            return ((MeatDish) dish).getGrams();
        } else if (dish instanceof SoupDish) {
            return calculatePriorityOfSoupDish((SoupDish) dish);
        } else if (dish instanceof DessertDish) {
            return calculatePriorityOfDessertDish((DessertDish) dish);
        }
        return 0;
    }

    private int calculatePriorityOfSoupDish(SoupDish soupDish) {
        String sideDish = soupDish.getSideDish().toUpperCase();
        switch (sideDish) {
            case YUCCA_GARNISH_SOUP_DISH:
                return YUCCA_PRIORITY;
            case POTATO_GARNISH_SOUP_DISH:
                return POTATO_PRIORITY;
            case RICE_GARNISH_SOUP_DISH:
                return RICE_PRIORITY;
            default:
                return 0;
        }
    }

    private int calculatePriorityOfDessertDish(DessertDish dessertDish) {
        if (dessertDish instanceof FlanDessertDish) {
            return FLAN_DESSERT_PRIORITY;
        } else if (dessertDish instanceof IceCreamDessertDish) {
            return ICE_CREAM_DESSERT_PRIORITY;
        }
        return 0;
    }
}
