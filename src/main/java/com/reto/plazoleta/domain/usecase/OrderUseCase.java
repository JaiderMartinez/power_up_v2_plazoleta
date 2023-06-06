package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.api.IOrderServicePort;
import com.reto.plazoleta.domain.exception.CustomerCanNotOrderException;
import com.reto.plazoleta.domain.exception.DishNotExistsException;
import com.reto.plazoleta.domain.exception.ObjectNotFoundException;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.model.DishModel;
import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.IDishPersistencePort;
import com.reto.plazoleta.domain.spi.IOrderDishPersistencePort;
import com.reto.plazoleta.domain.spi.IOrderPersistencePort;
import com.reto.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.StatusOrder;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IUserGateway userGateway;
    private final IOrderDishPersistencePort orderDishPersistencePort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort, IRestaurantPersistencePort restaurantPersistencePort,
                        IDishPersistencePort dishPersistencePort, IUserGateway userGateway,
                        IOrderDishPersistencePort orderDishPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.userGateway = userGateway;
        this.orderDishPersistencePort = orderDishPersistencePort;
    }

    @Override
    public OrderModel saveOrder(OrderModel orderModel, List<OrderDishModel> listOrderDishModelWithValueOfNameFromDishAndAmountOfDishes, String tokenWithPrefixBearer) {
        String usernameFromCustomer = "";
        final User userCustomerFound = this.userGateway.getUserByEmailInTheToken(usernameFromCustomer, tokenWithPrefixBearer);
        final RestaurantModel restaurantFoundModel = this.restaurantPersistencePort.findByIdRestaurant(orderModel.getRestaurantModel().getIdRestaurant());
        if (restaurantFoundModel == null) throw new ObjectNotFoundException("The restaurant in the order does not exist");

        final List<OrderModel> listOfOrdersFromUserCustomer = this.orderPersistencePort.findByIdUserCustomerAndIdRestaurant(
                        userCustomerFound.getIdUser(), restaurantFoundModel.getIdRestaurant())
                .stream().filter(order -> !order.getStatus().equals(StatusOrder.CANCELADO) && !order.getStatus().equals(StatusOrder.ENTREGADO))
                .collect(Collectors.toList());
        if (!listOfOrdersFromUserCustomer.isEmpty()) throw new CustomerCanNotOrderException("The customer user has an order in process");
        orderModel.setIdUserCustomer(userCustomerFound.getIdUser());
        orderModel.setDate(LocalDate.now());
        orderModel.setStatus(StatusOrder.PENDIENTE);
        final OrderModel orderSavedModel = this.orderPersistencePort.saveOrder(orderModel);

        List<OrderDishModel> orderDishListModelCompleteToSave = new ArrayList<>();
        OrderDishModel orderDishModelToSave = new OrderDishModel();
        DishModel dishFoundWithValueInAllItsFields;
        for (OrderDishModel orderDishModel : listOrderDishModelWithValueOfNameFromDishAndAmountOfDishes) {
            dishFoundWithValueInAllItsFields = this.dishPersistencePort.findById(orderDishModel.getDishModel().getIdDish());
            if (dishFoundWithValueInAllItsFields == null) throw new DishNotExistsException("The dish not exists");
            if (dishFoundWithValueInAllItsFields.getRestaurantModel().getIdRestaurant().equals(restaurantFoundModel.getIdRestaurant())) {
                orderDishModelToSave.setOrderModel(orderSavedModel);
                orderDishModelToSave.setDishModel(dishFoundWithValueInAllItsFields);
                orderDishModelToSave.setAmount(orderDishModel.getAmount());
                orderDishListModelCompleteToSave.add(orderDishModelToSave);
            }
        }
        this.orderDishPersistencePort.saveAllOrdersDishes(orderDishListModelCompleteToSave);
        return orderSavedModel;
    }
}
