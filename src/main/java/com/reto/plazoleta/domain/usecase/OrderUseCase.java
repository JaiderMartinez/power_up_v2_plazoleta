package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.api.IOrderServicePort;
import com.reto.plazoleta.domain.exception.CustomerHasAOrderInProcessException;
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
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
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
    private final JwtProvider jwtProvider;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort, IRestaurantPersistencePort restaurantPersistencePort,
                        IDishPersistencePort dishPersistencePort, IUserGateway userGateway,
                        IOrderDishPersistencePort orderDishPersistencePort, JwtProvider jwtProvider) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.userGateway = userGateway;
        this.orderDishPersistencePort = orderDishPersistencePort;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public OrderModel saveOrder(OrderModel orderModelRequest, List<OrderDishModel> listOrderDishModelWithValueOfNameFromDishAndAmountOfDishes, String tokenWithPrefixBearer) {
        String emailFromUserAuthenticated = getEmailFromUserAuthenticatedByTokenWithPrefixBearer(tokenWithPrefixBearer);
        final User userCustomerFound = getUserByEmail(emailFromUserAuthenticated, tokenWithPrefixBearer);
        Long idRestaurantFromRequest = orderModelRequest.getRestaurantModel().getIdRestaurant();
        validateRestaurantAndCheckStatusOfOtherUserOrdersInTheSameRestaurant(orderModelRequest.getRestaurantModel().getIdRestaurant(), userCustomerFound.getIdUser());

        orderModelRequest.setIdUserCustomer(userCustomerFound.getIdUser());
        orderModelRequest.setDate(LocalDate.now());
        orderModelRequest.setStatus(StatusOrder.PENDIENTE);
        final OrderModel orderSavedModel = this.orderPersistencePort.saveOrder(orderModelRequest);

        List<OrderDishModel> addOrderAndQuantityByFullDish = createOrderDishList(orderSavedModel, listOrderDishModelWithValueOfNameFromDishAndAmountOfDishes, idRestaurantFromRequest);
        this.orderDishPersistencePort.saveAllOrdersDishes(addOrderAndQuantityByFullDish);

        return orderSavedModel;
    }

    private String getEmailFromUserAuthenticatedByTokenWithPrefixBearer(String tokenWithPrefixBearer) {
        return this.jwtProvider.getAuthentication(tokenWithPrefixBearer.replace("Bearer ", "")).getName();
    }

    private User getUserByEmail(String email, String tokenWithPrefixBearer) {
        return this.userGateway.getUserByEmailInTheToken(email, tokenWithPrefixBearer);
    }

    private void validateRestaurantAndCheckStatusOfOtherUserOrdersInTheSameRestaurant(Long idRestaurant, Long idUserCustomer) {
        final RestaurantModel restaurantFoundModel = this.restaurantPersistencePort.findByIdRestaurant(idRestaurant);
        if (restaurantFoundModel == null)
            throw new ObjectNotFoundException("The restaurant in the order does not exist");

        final List<OrderModel> listOfOrdersFromUserFromSameRestaurant = this.orderPersistencePort.findByIdUserCustomerAndIdRestaurant(
                        idUserCustomer, restaurantFoundModel.getIdRestaurant())
                .stream().filter(order -> !order.getStatus().equals(StatusOrder.CANCELADO) && !order.getStatus().equals(StatusOrder.ENTREGADO))
                .collect(Collectors.toList());
        if (!listOfOrdersFromUserFromSameRestaurant.isEmpty())
            throw new CustomerHasAOrderInProcessException("The customer user has an order in process");
    }

    private List<OrderDishModel> createOrderDishList(OrderModel orderModel, List<OrderDishModel> orderDishModels, Long idRestaurant) {
        List<OrderDishModel> addOrderAndQuantityByFullDish = new ArrayList<>();
        for (OrderDishModel orderDishModel : orderDishModels) {
            DishModel dishFoundWithValueInAllItsFields = this.dishPersistencePort.findById(orderDishModel.getDishModel().getIdDish());

            if (dishFoundWithValueInAllItsFields == null)
                throw new DishNotExistsException("The dish does not exist");
            if (dishFoundWithValueInAllItsFields.getRestaurantModel().getIdRestaurant().equals(idRestaurant)) {
                OrderDishModel orderDishModelToSave = new OrderDishModel();
                orderDishModelToSave.setOrderModel(orderModel);
                orderDishModelToSave.setDishModel(dishFoundWithValueInAllItsFields);
                orderDishModelToSave.setAmount(orderDishModel.getAmount());
                addOrderAndQuantityByFullDish.add(orderDishModelToSave);
            }
        }
        return addOrderAndQuantityByFullDish;
    }
}
