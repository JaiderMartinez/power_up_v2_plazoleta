package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.api.ICustomerServicePort;
import com.reto.plazoleta.domain.exceptions.CustomerHasAOrderInProcessException;
import com.reto.plazoleta.domain.exceptions.DishNotExistsException;
import com.reto.plazoleta.domain.exceptions.EmptyFieldsException;
import com.reto.plazoleta.domain.exceptions.OrderInProcessException;
import com.reto.plazoleta.domain.exceptions.OrderNotExistsException;
import com.reto.plazoleta.domain.exceptions.RestaurantNotExistException;
import com.reto.plazoleta.domain.model.CategoryModel;
import com.reto.plazoleta.domain.model.User;
import com.reto.plazoleta.domain.model.dishes.FlanDessertDish;
import com.reto.plazoleta.domain.model.dishes.IceCreamDessertDish;
import com.reto.plazoleta.domain.model.dishes.MeatDish;
import com.reto.plazoleta.domain.model.dishes.SoupDish;
import com.reto.plazoleta.domain.model.orders.OrderProcessor;
import com.reto.plazoleta.domain.spi.clients.IUserGateway;
import com.reto.plazoleta.domain.model.dishes.DishModel;
import com.reto.plazoleta.domain.model.orders.OrderDishModel;
import com.reto.plazoleta.domain.model.orders.OrderModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.persistence.IDishPersistencePort;
import com.reto.plazoleta.domain.spi.persistence.IOrderDishPersistencePort;
import com.reto.plazoleta.domain.spi.persistence.IOrderPersistencePort;
import com.reto.plazoleta.domain.spi.persistence.IRestaurantPersistencePort;
import com.reto.plazoleta.domain.spi.token.ITokenServiceProviderPort;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.StatusOrder;
import com.reto.plazoleta.domain.exceptions.NoDataFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerUseCase implements ICustomerServicePort {

    private static final String MEAT_DISH_TYPE = "CARNE";
    private static final String SOUP_DISH_TYPE = "SOPAS";
    private static final String FLAN_DESSERT_DISH_TYPE = "POSTRE_FLAN";
    private static final String ICE_CREAM_DESSERT_DISH_TYPE = "POSTRE_HELADO";
    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IUserGateway userGateway;
    private final JwtProvider jwtProvider;
    private final IOrderDishPersistencePort orderDishPersistencePort;
    private final ITokenServiceProviderPort tokenServiceProviderPort;

    public CustomerUseCase(IOrderPersistencePort orderPersistencePort, IRestaurantPersistencePort restaurantPersistencePort,
                           IDishPersistencePort dishPersistencePort, IUserGateway userGateway,
                           JwtProvider jwtProvider, IOrderDishPersistencePort orderDishPersistencePort,
                           ITokenServiceProviderPort tokenServiceProviderPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.userGateway = userGateway;
        this.jwtProvider = jwtProvider;
        this.orderDishPersistencePort = orderDishPersistencePort;
        this.tokenServiceProviderPort = tokenServiceProviderPort;
    }

    @Override
    public Page<RestaurantModel> findAllByOrderByNameAsc(Integer numberPage, Integer sizeItems) {
        Page<RestaurantModel> resultRestaurantsPageable = this.restaurantPersistencePort
                                            .findAllByOrderByNameAsc(PageRequest.of(numberPage, sizeItems));
        checkIfListIsEmpty(resultRestaurantsPageable.isEmpty());
        return resultRestaurantsPageable;
    }

    @Override
    public Page<DishModel> getAllDishesActivePaginatedFromARestaurantOrderByCategoryAscending(Integer numberPage, Integer sizeItems, Long idRestaurant) {
        validateRestaurant(idRestaurant);
        Page<DishModel> dishesPaginatedAndOrderByCategory = this.dishPersistencePort
                        .getAllDishesActiveOfARestaurantOrderByCategoryAscending(PageRequest.of(numberPage, sizeItems), idRestaurant);
        checkIfListIsEmpty(dishesPaginatedAndOrderByCategory.isEmpty());
        return dishesPaginatedAndOrderByCategory;
    }

    private void checkIfListIsEmpty(boolean isTheListEmpty) {
        if (isTheListEmpty)
            throw new NoDataFoundException();
    }

    @Override
    public OrderModel saveOrder(OrderModel orderModelRequest, String tokenWithPrefixBearer) {
        String emailFromUserAuthenticated = getEmailFromUserAuthenticatedByTokenWithPrefixBearer(tokenWithPrefixBearer);
        final User userCustomerFound = getUserByEmail(emailFromUserAuthenticated, tokenWithPrefixBearer);
        validateRestaurant(orderModelRequest.getRestaurantModel().getIdRestaurant());
        checkStatusFromUserOrdersInARestaurant(orderModelRequest.getRestaurantModel().getIdRestaurant(), userCustomerFound.getIdUser());

        List<OrderDishModel> ordersDishesModelsRequest = orderModelRequest.getOrdersDishesModel();

        orderModelRequest.setIdUserCustomer(userCustomerFound.getIdUser());
        orderModelRequest.setDate(LocalDate.now());
        orderModelRequest.setStatus(StatusOrder.PENDIENTE);
        orderModelRequest.setOrdersDishesModel(new ArrayList<>());
        OrderModel orderModelSaved = this.orderPersistencePort.saveOrder(orderModelRequest);

        orderModelSaved.setOrdersDishesModel(ordersDishesModelsRequest);
        List<OrderDishModel> addOrderAndAmountOfDish = createOrdersDishesComplete(orderModelSaved);

        List<OrderDishModel> orderDishModelsSaved = this.orderDishPersistencePort.saveAllOrdersDishes(addOrderAndAmountOfDish);
        orderModelSaved.setOrdersDishesModel(orderDishModelsSaved);
        return orderModelSaved;
    }

    private String getEmailFromUserAuthenticatedByTokenWithPrefixBearer(String tokenWithPrefixBearer) {
        return this.jwtProvider.getAuthentication(tokenWithPrefixBearer.replace("Bearer ", "")).getName();
    }

    private User getUserByEmail(String email, String tokenWithPrefixBearer) {
        return this.userGateway.getUserByEmailInTheToken(email, tokenWithPrefixBearer);
    }

    private void validateRestaurant(Long idRestaurant) {
        final RestaurantModel restaurantFoundModel = this.restaurantPersistencePort.findByIdRestaurant(idRestaurant);
        if (restaurantFoundModel == null)
            throw new RestaurantNotExistException("The restaurant not exist");
    }

    private void checkStatusFromUserOrdersInARestaurant(Long idRestaurant, Long idUserCustomer) {
        long numberOfOrdersFoundInProcess = this.orderPersistencePort.findByIdUserCustomerAndIdRestaurant(
                         idUserCustomer, idRestaurant).stream()
                .filter(order -> !order.getStatus().equals(StatusOrder.CANCELADO) && !order.getStatus().equals(StatusOrder.ENTREGADO))
                .count();
        if (numberOfOrdersFoundInProcess != 0)
            throw new CustomerHasAOrderInProcessException("The customer user has an order in process");
    }

    private List<OrderDishModel> createOrdersDishesComplete(OrderModel orderModel) {
        List<OrderDishModel> ordersDishesModelToSave = new ArrayList<>();
        for (OrderDishModel orderDishModel : orderModel.getOrdersDishesModel()) {
            DishModel dishFoundWithValueInAllItsFields = this.dishPersistencePort.findById(orderDishModel.getDishModel().getIdDish());
            if (dishFoundWithValueInAllItsFields == null) {
                throw new DishNotExistsException("The dish does not exist");
            } else if (dishFoundWithValueInAllItsFields.getRestaurantModel().getIdRestaurant().equals(orderModel.getRestaurantModel().getIdRestaurant())) {
                OrderDishModel orderDishModelToSave = new OrderDishModel();
                orderDishModelToSave.setOrderModel(orderModel);
                orderDishModelToSave.setDishModel(dishFoundWithValueInAllItsFields);
                orderDishModelToSave.setAmount(orderDishModel.getAmount());
                ordersDishesModelToSave.add(orderDishModelToSave);
            }
        }
        return ordersDishesModelToSave;
    }

    @Override
    public OrderModel cancelOrder(Long idOrder, String tokenWithPrefixBearer) {
        String emailFromUserAuthenticated = getEmailFromUserAuthenticatedByTokenWithPrefixBearer(tokenWithPrefixBearer);
        final User userCustomerAuthenticated = getUserByEmail(emailFromUserAuthenticated, tokenWithPrefixBearer);
        OrderModel orderModelToChangeStatusToCanceled = this.orderPersistencePort.findByIdOrder(idOrder);
        validateStatusFromOrderAndIfBelongTheUserAuthenticated(orderModelToChangeStatusToCanceled, userCustomerAuthenticated.getIdUser());
        orderModelToChangeStatusToCanceled.setStatus(StatusOrder.CANCELADO);
        return this.orderPersistencePort.saveOrder(orderModelToChangeStatusToCanceled);
    }

    private void validateStatusFromOrderAndIfBelongTheUserAuthenticated(OrderModel orderModelToValidate, Long idUserAuthenticated) {
        if (orderModelToValidate == null) {
            throw new OrderNotExistsException("The order not exist");
        } else if (!orderModelToValidate.getIdUserCustomer().equals(idUserAuthenticated)) {
            throw new OrderNotExistsException("The order does not belong to the user");
        } else if (!orderModelToValidate.getStatus().equals(StatusOrder.PENDIENTE)) {
            throw new OrderInProcessException("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse");
        }
    }

    @Override
    public OrderModel addSingleDishOrder(OrderModel orderRequest) {
        validateIfRestaurantExists(orderRequest.getRestaurantModel().getIdRestaurant());
        String tokenWithPrefixBearer = this.tokenServiceProviderPort.getTokenWithPrefixBearerFromUserAuthenticated();
        User customer = getUserByEmail(getEmailFromToken(tokenWithPrefixBearer), tokenWithPrefixBearer);
        orderRequest.setStatus(StatusOrder.PENDIENTE);
        orderRequest.setIdUserCustomer(customer.getIdUser());
        orderRequest.setOrdersDishesModel(getOrdersDishesOrganizedByPriority(orderRequest));
        return this.orderPersistencePort.saveOrderAndOrdersDishes(orderRequest);
    }

    private void validateIfRestaurantExists(Long idRestaurant) {
        if (!this.restaurantPersistencePort.existRestaurantById(idRestaurant)) {
            throw new RestaurantNotExistException("");
        }
    }

    private List<OrderDishModel> getOrdersDishesOrganizedByPriority(OrderModel orderRequest) {
        OrderProcessor orderProcessor = new OrderProcessor();
        for (OrderDishModel orderDishModel : orderRequest.getOrdersDishesModel()) {
            DishModel dishCompleteData = this.dishPersistencePort.findById(orderDishModel.getDishModel().getIdDish());
            validateIfDishExists(dishCompleteData);
            orderDishModel.setDishModel(getDishType(orderDishModel.getDishModel(), dishCompleteData));
            orderDishModel.setOrderModel(orderRequest);

            orderProcessor.addOrderDish(orderDishModel);
        }
        return orderProcessor.getOrdersDishesAsList();
    }

    private DishModel getDishType(DishModel searchDishType, DishModel dishWithDataComplete) {
        CategoryModel categoryModel = dishWithDataComplete.getCategoryModel();
        String dishType = categoryModel.getName();
        if (searchDishType instanceof MeatDish && dishType.equalsIgnoreCase(MEAT_DISH_TYPE)) {
            return validateGramsFromMeatDish(
                    buildMeatDish(searchDishType, dishWithDataComplete)
            );
        } else if (searchDishType instanceof SoupDish && dishType.equalsIgnoreCase(SOUP_DISH_TYPE)) {
            return validateFieldSideDishIfIsEmptyFromSoupDish(
                    buildSoupDish(searchDishType, dishWithDataComplete)
            );
        } else if (searchDishType instanceof FlanDessertDish && dishType.equalsIgnoreCase(FLAN_DESSERT_DISH_TYPE)) {
            return validateFlanDessertDishFieldEmpty(
                    buildFlanDessertDish(searchDishType, dishWithDataComplete)
            );
        } else if (searchDishType instanceof IceCreamDessertDish && dishType.equalsIgnoreCase(ICE_CREAM_DESSERT_DISH_TYPE)) {
            return validateIceCreamDessertDishFieldEmpty(
                    buildIceCreamDessertDish(searchDishType, dishWithDataComplete)
            );
        }
        throw new DishNotExistsException("");
    }

    private MeatDish buildMeatDish(DishModel dishTypeMeat, DishModel dishWithDataComplete) {
        dishTypeMeat.updateAllDataFromAllFieldsFromDishModel(dishWithDataComplete);
        return ((MeatDish) dishTypeMeat);
    }

    private MeatDish validateGramsFromMeatDish(MeatDish meatDish) {
        if (meatDish.getGrams() == null) {
            throw new EmptyFieldsException("");
        }
        else if ( !(meatDish.getGrams() >= 250 && meatDish.getGrams() <= 750) ) {
            throw new DishNotExistsException("");
        }
        return meatDish;
    }

    private SoupDish buildSoupDish(DishModel dishTypeSoupDish, DishModel dishWithDataComplete) {
        dishTypeSoupDish.updateAllDataFromAllFieldsFromDishModel(dishWithDataComplete);
        return (SoupDish) dishTypeSoupDish;
    }

    private SoupDish validateFieldSideDishIfIsEmptyFromSoupDish(SoupDish soupDish) {
        validateIfFieldIsEmpty(soupDish.getSideDish());
        return soupDish;
    }

    private FlanDessertDish buildFlanDessertDish(DishModel dishTypeFlanDessertDish, DishModel dishWithDataComplete) {
        dishTypeFlanDessertDish.updateAllDataFromAllFieldsFromDishModel(dishWithDataComplete);
        return (FlanDessertDish) dishTypeFlanDessertDish;
    }

    private FlanDessertDish validateFlanDessertDishFieldEmpty(FlanDessertDish flanDessertDish) {
        validateIfFieldIsEmpty(flanDessertDish.getTopping());
        return flanDessertDish;
    }

    private void validateIfFieldIsEmpty(String fieldToValidate) {
        if (fieldToValidate == null || fieldToValidate.replace(" ", "").isEmpty()) {
            throw new EmptyFieldsException("");
        }
    }

    private IceCreamDessertDish buildIceCreamDessertDish(DishModel dishTypeIceCreamDessertDish, DishModel dishWithDataComplete) {
        dishTypeIceCreamDessertDish.updateAllDataFromAllFieldsFromDishModel(dishWithDataComplete);
        return (IceCreamDessertDish) dishTypeIceCreamDessertDish;
    }

    private IceCreamDessertDish validateIceCreamDessertDishFieldEmpty(IceCreamDessertDish iceCreamDessertDish) {
        validateIfFieldIsEmpty(iceCreamDessertDish.getFlavor());
        return iceCreamDessertDish;
    }

    private void validateIfDishExists(DishModel dishToValidate) {
        if (dishToValidate == null) {
            throw new DishNotExistsException("");
        }
    }

    private String getEmailFromToken(String tokenWithPrefixBearer) {
        return this.tokenServiceProviderPort.getEmailFromToken(tokenWithPrefixBearer);
    }

    @Override
    public OrderModel addOrderWithMultipleDishesType(OrderModel orderWithMultipleDishes) {
        validateIfRestaurantExists(orderWithMultipleDishes.getRestaurantModel().getIdRestaurant());
        String tokenWithPrefixBearer = this.tokenServiceProviderPort.getTokenWithPrefixBearerFromUserAuthenticated();
        User customer = getUserByEmail(getEmailFromToken(tokenWithPrefixBearer), tokenWithPrefixBearer);
        orderWithMultipleDishes.setStatus(StatusOrder.PENDIENTE);
        orderWithMultipleDishes.setIdUserCustomer(customer.getIdUser());
        orderWithMultipleDishes.setOrdersDishesModel(getOrdersDishesOrganizedByPriority(orderWithMultipleDishes));
        return this.orderPersistencePort.saveOrderAndOrdersDishes(orderWithMultipleDishes);
    }
}
