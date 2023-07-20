package com.reto.plazoleta.domain.model.orders;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class OrderProcessor {

    private static final String DISH_TYPE_MEAT = "CARNE";
    private static final String DISH_TYPE_SOUP = "SOPAS";
    private static final String DISH_TYPE_DESSERT = "POSTRE";
    private final Map<String, PriorityQueue<OrderDishModel>> dishesOrdersByQueueType;

    public OrderProcessor() {
        dishesOrdersByQueueType = new LinkedHashMap<>();
        dishesOrdersByQueueType.put(DISH_TYPE_MEAT, new PriorityQueue<>(new OrderDishPriorityComparator().reversed()));
        dishesOrdersByQueueType.put(DISH_TYPE_SOUP, new PriorityQueue<>(new OrderDishPriorityComparator().reversed()));
        dishesOrdersByQueueType.put(DISH_TYPE_DESSERT, new PriorityQueue<>(new OrderDishPriorityComparator().reversed()));
    }

    public void addOrderDish(OrderDishModel orderDishModel) {
        String dishType = orderDishModel.getDishModel().getCategoryModel().getName();
        PriorityQueue<OrderDishModel> ordersDishesOfADishType = this.dishesOrdersByQueueType
                                            .computeIfAbsent(dishType, value ->
                                                    new PriorityQueue<>(new OrderDishPriorityComparator().reversed()));
        ordersDishesOfADishType.add(orderDishModel);
        this.dishesOrdersByQueueType.put(dishType, ordersDishesOfADishType);
    }

    public void addAllOrderDish(List<OrderDishModel> ordersDishes) {
        ordersDishes.forEach( orderDishModel -> {
                    String dishType = orderDishModel.getDishModel().getCategoryModel().getName();
                    PriorityQueue<OrderDishModel> ordersDishesOfADishType = this.dishesOrdersByQueueType
                            .computeIfAbsent(dishType, value ->
                                    new PriorityQueue<>(new OrderDishPriorityComparator().reversed()));
                    ordersDishesOfADishType.add(orderDishModel);
                    this.dishesOrdersByQueueType.put(dishType, ordersDishesOfADishType);
                }
        );
    }

    public List<OrderDishModel> getOrdersDishesOrderedByHigherPriority() {
        return this.dishesOrdersByQueueType.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
