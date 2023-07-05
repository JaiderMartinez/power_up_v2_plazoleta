package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.exceptions.CustomerHasAOrderInProcessException;
import com.reto.plazoleta.domain.exceptions.DishNotExistsException;
import com.reto.plazoleta.domain.exceptions.EmptyFieldsException;
import com.reto.plazoleta.domain.exceptions.OrderInProcessException;
import com.reto.plazoleta.domain.exceptions.OrderNotExistsException;
import com.reto.plazoleta.domain.exceptions.RestaurantNotExistException;
import com.reto.plazoleta.domain.model.User;
import com.reto.plazoleta.domain.model.dishes.FlanDessertDish;
import com.reto.plazoleta.domain.model.dishes.MeatDish;
import com.reto.plazoleta.domain.model.dishes.SoupDish;
import com.reto.plazoleta.domain.spi.clients.IUserGateway;
import com.reto.plazoleta.domain.model.CategoryModel;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerUseCaseTest {

    private static final String EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN = "customer@customer.com";
    private static final String TOKEN_WITH_PREFIX_BEARER = "Bearer + Token";

    @InjectMocks
    private CustomerUseCase customerUseCase;

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IUserGateway userGateway;

    @Mock
    private IOrderDishPersistencePort orderDishPersistencePort;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private ITokenServiceProviderPort tokenServiceProviderPort;

    @Test
    void test_findAllOrderByNameAsc_withIntAsSizeItemsGreaterThanZero_ShouldReturnListRestaurantPageableWithAllFields() {
        //Given
        List<RestaurantModel> restaurantList = new ArrayList<>();
        restaurantList.add(new RestaurantModel(1L, "Restaurante 1", "Dirección 1", "3014896273", "http://restaurante1.com", 111111L, 1L));
        restaurantList.add(new RestaurantModel(2L, "Restaurante 2", "Dirección 2", "3224196283", "http://restaurante2.com", 222222L, 2L));
        Page<RestaurantModel> pageableRestaurantsExpected = new PageImpl<>(restaurantList);
        when(restaurantPersistencePort.findAllByOrderByNameAsc(PageRequest.of(0, 10))).thenReturn(pageableRestaurantsExpected);
        //When
        Page<RestaurantModel> result = this.customerUseCase.findAllByOrderByNameAsc(0, 10);
        //Then
        verify(this.restaurantPersistencePort, times(1)).findAllByOrderByNameAsc(PageRequest.of(0, 10));
        assertEquals(pageableRestaurantsExpected, result);
        assertEquals(pageableRestaurantsExpected.getTotalElements(), result.getTotalElements());
        assertEquals(pageableRestaurantsExpected.toList().get(0).getPhone(), result.toList().get(0).getPhone());
        assertEquals(pageableRestaurantsExpected.toList().get(1).getNit(), result.toList().get(1).getNit());
    }

    @Test
    void test_findAllOrderByNameAsc_withIntAsSizeItemsGreaterThanZeroAndNoDataFound_ShouldThrowNoDataFoundException() {
        //Given
        int numberPage = 0;
        int sizeItems = 10;
        when(restaurantPersistencePort.findAllByOrderByNameAsc(PageRequest.of(numberPage, sizeItems))).thenReturn(Page.empty());
        // When & Then
        Assertions.assertThrows(
                NoDataFoundException.class,
                () -> this.customerUseCase.findAllByOrderByNameAsc(numberPage, sizeItems)
        );
    }

    @Test
    void test_saveOrder_withAllFieldsValidInObjectAsOrderModelAndListOfDishModelCorrectAndTokenValid_shouldReturnObjectOrderModelSavedInDataBase() {
        //Given
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);

        RestaurantModel restaurantModelExpected = new RestaurantModel(1L, "Restaurante 1", "Dirección 1", "3014896273", "http://restaurante1.com", 111111L, 1L);

        List<DishModel> dishesListExpected = new ArrayList<>();
        dishesListExpected.add(new DishModel(1L, "name", "description", 300000.0, "http://image.png", true,
                restaurantModelExpected, new CategoryModel(1L, "name", "description")));

        OrderModel orderModelExpected = new OrderModel();
        orderModelExpected.setIdUserCustomer(userAuthenticatedByToken.getIdUser());
        orderModelExpected.setDate(LocalDate.now());
        orderModelExpected.setStatus(StatusOrder.PENDIENTE);
        orderModelExpected.setRestaurantModel(restaurantModelExpected);

        List<OrderDishModel> ordersDishesRequest = new ArrayList<>();
        ordersDishesRequest.add(new OrderDishModel(1L, orderModelExpected, dishesListExpected.get(0), 4));

        OrderModel orderModelRequest = new OrderModel();
        orderModelRequest.setRestaurantModel(restaurantModelExpected);
        orderModelRequest.setOrdersDishesModel(ordersDishesRequest);
        when(this.jwtProvider.getAuthentication("+ Token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(restaurantModelExpected);
        when(this.orderPersistencePort.findByIdUserCustomerAndIdRestaurant(1L, userAuthenticatedByToken.getIdUser())).thenReturn(Collections.emptyList());
        when(this.orderPersistencePort.saveOrder(orderModelRequest)).thenReturn(orderModelExpected);
        when(this.dishPersistencePort.findById(dishesListExpected.get(0).getIdDish())).thenReturn(dishesListExpected.get(0));
        //When
        final OrderModel orderSavedModel = this.customerUseCase.saveOrder(orderModelRequest, TOKEN_WITH_PREFIX_BEARER);
        //Then
        verify(this.jwtProvider, times(1)).getAuthentication("+ Token");
        verify(this.userGateway, times(1)).getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER);
        verify(this.restaurantPersistencePort, times(1)).findByIdRestaurant(1L);
        verify(this.orderPersistencePort, times(1)).findByIdUserCustomerAndIdRestaurant(1L, userAuthenticatedByToken.getIdUser());
        verify(this.orderPersistencePort, times(1)).saveOrder(orderModelRequest);
        verify(this.dishPersistencePort, times(1)).findById(dishesListExpected.get(0).getIdDish());
        assertEquals(orderModelExpected.getRestaurantModel().getIdRestaurant(), orderSavedModel.getRestaurantModel().getIdRestaurant());
        assertEquals(orderModelExpected.getStatus(), orderSavedModel.getStatus());
        assertEquals(orderModelExpected.getDate(), orderSavedModel.getDate());
        assertEquals(orderModelExpected.getIdUserCustomer(), orderSavedModel.getIdUserCustomer());
    }

    @Test
    void test_saveOrder_withAllFieldsValidInOrderModelAndListOfDishesValidButCustomerHasAnOrderInProcessAndTokenValid_shouldThrowCustomerHasAOrderInProcessException() {
        //Given
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        RestaurantModel restaurantModelExpected = new RestaurantModel(1L, "Restaurante 1", "Dirección 1", "3014896273", "http://restaurante1.com", 111111L, 1L);

        OrderModel orderModelExpected = new OrderModel();
        orderModelExpected.setIdUserCustomer(userAuthenticatedByToken.getIdUser());
        orderModelExpected.setDate(LocalDate.now());
        orderModelExpected.setStatus(StatusOrder.PENDIENTE);
        orderModelExpected.setRestaurantModel(restaurantModelExpected);

        OrderModel orderModelRequestAndUserWithAnOrderInProcess = new OrderModel();
        orderModelRequestAndUserWithAnOrderInProcess.setRestaurantModel(restaurantModelExpected);

        when(this.jwtProvider.getAuthentication("+ Token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(restaurantModelExpected);
        when(this.orderPersistencePort.findByIdUserCustomerAndIdRestaurant(1L, userAuthenticatedByToken.getIdUser())).thenReturn(Collections.singletonList(orderModelExpected));
        //When
        CustomerHasAOrderInProcessException messageException = assertThrows(
                CustomerHasAOrderInProcessException.class,
                () -> this.customerUseCase.saveOrder(orderModelRequestAndUserWithAnOrderInProcess, TOKEN_WITH_PREFIX_BEARER));
        //Then
        verify(this.jwtProvider, times(1)).getAuthentication("+ Token");
        verify(this.userGateway, times(1)).getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER);
        verify(this.restaurantPersistencePort, times(1)).findByIdRestaurant(1L);
        verify(this.orderPersistencePort, times(1)).findByIdUserCustomerAndIdRestaurant(1L, userAuthenticatedByToken.getIdUser());
        assertEquals("The customer user has an order in process", messageException.getMessage());
    }

    @Test
    void test_saveOrder_withValueFromFieldIdRestaurantInvalidSinceRestaurantWithThatIdDoesNotExistAndTokenValid_shouldThrowObjectNotFoundException() {
        //Given
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        RestaurantModel restaurantModelExpected = new RestaurantModel(1L, "Restaurante 1", "Dirección 1", "3014896273", "http://restaurante1.com", 111111L, 1L);

        OrderModel orderModelExpected = new OrderModel();
        orderModelExpected.setIdUserCustomer(userAuthenticatedByToken.getIdUser());
        orderModelExpected.setDate(LocalDate.now());
        orderModelExpected.setStatus(StatusOrder.PENDIENTE);
        orderModelExpected.setRestaurantModel(restaurantModelExpected);

        DishModel dishModelRequest = new DishModel();
        dishModelRequest.setIdDish(1L);

        OrderModel orderModelRequest = new OrderModel();
        orderModelRequest.setRestaurantModel(restaurantModelExpected);
        orderModelRequest.setOrdersDishesModel(Collections.singletonList(new OrderDishModel(1L, orderModelExpected, dishModelRequest, 4)));

        when(this.jwtProvider.getAuthentication("+ Token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(null);
        //When
        RestaurantNotExistException messageException = assertThrows(
                RestaurantNotExistException.class,
                () -> this.customerUseCase.saveOrder(orderModelRequest, TOKEN_WITH_PREFIX_BEARER));
        //Then
        verify(this.jwtProvider, times(1)).getAuthentication("+ Token");
        verify(this.userGateway, times(1)).getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER);
        verify(this.restaurantPersistencePort, times(1)).findByIdRestaurant(1L);
        assertEquals("The restaurant not exist", messageException.getMessage());
    }

    @Test
    void test_saveOrder_withValueFromFieldIdDishIsInvalidAndTokenValid_shouldThrowDishNotExistsException() {
        //Given
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);

        RestaurantModel restaurantModelExpected = new RestaurantModel(1L, "Restaurante 1", "Dirección 1", "3014896273", "http://restaurante1.com", 111111L, 1L);

        OrderModel orderModelExpected = new OrderModel();
        orderModelExpected.setIdUserCustomer(userAuthenticatedByToken.getIdUser());
        orderModelExpected.setDate(LocalDate.now());
        orderModelExpected.setStatus(StatusOrder.PENDIENTE);
        orderModelExpected.setRestaurantModel(restaurantModelExpected);

        DishModel dishModelRequest = new DishModel();
        dishModelRequest.setIdDish(1L);

        OrderModel orderModelRequest = new OrderModel();
        orderModelRequest.setRestaurantModel(restaurantModelExpected);
        orderModelRequest.setOrdersDishesModel(Collections.singletonList(new OrderDishModel(1L, orderModelExpected, dishModelRequest, 4)));

        when(this.jwtProvider.getAuthentication("+ Token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(restaurantModelExpected);
        when(this.orderPersistencePort.findByIdUserCustomerAndIdRestaurant(1L, userAuthenticatedByToken.getIdUser())).thenReturn(Collections.emptyList());
        when(this.orderPersistencePort.saveOrder(orderModelRequest)).thenReturn(orderModelExpected);
        when(this.dishPersistencePort.findById(dishModelRequest.getIdDish())).thenReturn(null);
        //When
        DishNotExistsException messageException = assertThrows(
                DishNotExistsException.class,
                () -> this.customerUseCase.saveOrder(orderModelRequest, TOKEN_WITH_PREFIX_BEARER));
        //Then
        verify(this.jwtProvider, times(1)).getAuthentication("+ Token");
        verify(this.userGateway, times(1)).getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER);
        verify(this.restaurantPersistencePort, times(1)).findByIdRestaurant(1L);
        verify(this.orderPersistencePort, times(1)).findByIdUserCustomerAndIdRestaurant(1L, userAuthenticatedByToken.getIdUser());
        verify(this.orderPersistencePort, times(1)).saveOrder(orderModelRequest);
        verify(this.dishPersistencePort, times(1)).findById(dishModelRequest.getIdDish());
        assertEquals("The dish does not exist", messageException.getMessage());
    }

    @Test
    void test_cancelOrder_withValidIdOrderAndTokenCorrect_shouldReturnTheOrderUpdatedStatusToCanceled() {
        //Given
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        RestaurantModel restaurantModelFromOrder = new RestaurantModel();
        restaurantModelFromOrder.setIdRestaurant(1L);
        OrderModel orderModelExpected = new OrderModel(1L, 1L, LocalDate.now(), StatusOrder.CANCELADO, null, restaurantModelFromOrder);
        OrderModel orderModelToUpdate = new OrderModel(1L, 1L, LocalDate.now(), StatusOrder.PENDIENTE, null, restaurantModelFromOrder);
        when(this.jwtProvider.getAuthentication("+ Token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(orderModelToUpdate);
        when(this.orderPersistencePort.saveOrder(orderModelToUpdate)).thenReturn(orderModelExpected);
        //When
        OrderModel orderCanceledResult = this.customerUseCase.cancelOrder(1L, TOKEN_WITH_PREFIX_BEARER);
        //Then
        assertEquals(orderModelExpected.getIdOrder(), orderCanceledResult.getIdOrder());
        assertEquals(orderModelExpected.getStatus(), orderCanceledResult.getStatus());
        assertEquals(orderModelExpected.getDate(), orderCanceledResult.getDate());
        assertEquals(orderModelExpected.getRestaurantModel().getIdRestaurant(), orderCanceledResult.getRestaurantModel().getIdRestaurant());
        assertEquals(orderModelExpected.getIdUserCustomer(), orderCanceledResult.getIdUserCustomer());
    }

    @Test
    void test_cancelOrder_withRequestIdOrderInvalidBecauseNotExistTheOrderAndTokenCorrect_shouldThrowOrderNotExistsException() {
        //Given
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        when(this.jwtProvider.getAuthentication("+ Token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(null);
        //When
        OrderNotExistsException messageException = assertThrows(
                OrderNotExistsException.class,
                () ->  this.customerUseCase.cancelOrder(1L, TOKEN_WITH_PREFIX_BEARER));
        //Then
        assertEquals("The order not exist", messageException.getMessage());
    }

    @Test
    void test_cancelOrder_withRequestIdOrderInvalidButTheOrderDoesNotBelongToUserAndTokenCorrect_shouldThrowOrderNotExistsException() {
        //Given
        User userAuthenticatedByTokenButNeverPlacedAnOrder = new User();
        userAuthenticatedByTokenButNeverPlacedAnOrder.setIdUser(2L);
        RestaurantModel restaurantModelFromOrder = new RestaurantModel();
        restaurantModelFromOrder.setIdRestaurant(1L);
        OrderModel orderModelToUpdate = new OrderModel(1L, 1L, LocalDate.now(), StatusOrder.PENDIENTE, null, restaurantModelFromOrder);
        when(this.jwtProvider.getAuthentication("+ Token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByTokenButNeverPlacedAnOrder);
        when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(orderModelToUpdate);
        //When
        OrderNotExistsException messageException = assertThrows(
                OrderNotExistsException.class,
                () ->  this.customerUseCase.cancelOrder(1L, TOKEN_WITH_PREFIX_BEARER));
        //Then
        assertEquals("The order does not belong to the user", messageException.getMessage());
    }

    @Test
    void test_cancelOrder_withRequestIdOrderValidButTheStatusFromOrderIsDifferentFromPending_shouldThrowOrderInProcessException() {
        //Given
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        RestaurantModel restaurantModelFromOrder = new RestaurantModel();
        restaurantModelFromOrder.setIdRestaurant(1L);
        OrderModel orderModelToUpdate = new OrderModel(1L, 1L, LocalDate.now(), StatusOrder.LISTO, null, restaurantModelFromOrder);
        when(this.jwtProvider.getAuthentication("+ Token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(orderModelToUpdate);
        //When
        OrderInProcessException messageException = assertThrows(
                OrderInProcessException.class,
                () ->  this.customerUseCase.cancelOrder(1L, TOKEN_WITH_PREFIX_BEARER));
        //Then
        assertEquals("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse", messageException.getMessage());
    }

    @Test
    void test_getAllDishesActivePaginatedFromARestaurantOrderByCategoryAscending_withRequestParamValid_shouldReturnAnListPaginatedFromDishes() {
        //Given
        RestaurantModel restaurantModelExpected = new RestaurantModel();
        restaurantModelExpected.setIdRestaurant(1L);
        CategoryModel categoryModelExpected = new CategoryModel();
        categoryModelExpected.setIdCategory(1L);
        List<DishModel> dishesWithVariableActiveAndOrderByCategory = new ArrayList<>();
        dishesWithVariableActiveAndOrderByCategory.add(new DishModel(1L, "name", "description", 3000000.0, "http://imagen.png", true, restaurantModelExpected, categoryModelExpected));
        int numberPageRequest = 0;
        int sizeItemsByPage = 2;
        Page<DishModel> dishesExpected = new PageImpl<>(dishesWithVariableActiveAndOrderByCategory, PageRequest.of(numberPageRequest, sizeItemsByPage), dishesWithVariableActiveAndOrderByCategory.size());
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(restaurantModelExpected);
        when(this.dishPersistencePort.getAllDishesActiveOfARestaurantOrderByCategoryAscending(PageRequest.of(numberPageRequest, sizeItemsByPage), restaurantModelExpected.getIdRestaurant()))
                                                                                              .thenReturn(dishesExpected);
        //When
        Page<DishModel> dishesResult = this.customerUseCase.getAllDishesActivePaginatedFromARestaurantOrderByCategoryAscending(numberPageRequest, sizeItemsByPage, 1L);
        //Then
        assertEquals(dishesExpected.getTotalPages(), dishesResult.getTotalPages());
        assertEquals(dishesExpected.getNumberOfElements(), dishesResult.getNumberOfElements());
        assertEquals(dishesExpected.getSize(), dishesResult.getSize());
        assertEquals(dishesExpected.getPageable(), dishesResult.getPageable());
        assertEquals(dishesExpected.getNumber(), dishesResult.getNumber());
        assertEquals(dishesExpected.getContent().get(0).getIdDish(), dishesResult.getContent().get(0).getIdDish());
        assertEquals(dishesExpected.getContent().get(0).getName(), dishesResult.getContent().get(0).getName());
        assertEquals(dishesExpected.getContent().get(0).getRestaurantModel().getIdRestaurant(), dishesResult.getContent().get(0).getRestaurantModel().getIdRestaurant());
        assertEquals(dishesExpected.getContent().get(0).getState(), dishesResult.getContent().get(0).getState());
    }

    @Test
    void test_getAllDishesActivePaginatedFromARestaurantOrderByCategoryAscending_withRequestParamIdRestaurantInvalidBecauseRestaurantNotExist_shouldThrowRestaurantNotExistException() {
        //Given
        Long idRestaurantRequest = 1L;
        Integer numberPageRequest = 0;
        Integer sizeItemsByPage = 2;
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(null);
        //When
        RestaurantNotExistException messageException = assertThrows(
                RestaurantNotExistException.class,
                () ->  this.customerUseCase.getAllDishesActivePaginatedFromARestaurantOrderByCategoryAscending(numberPageRequest, sizeItemsByPage, idRestaurantRequest));
        //Then
        assertEquals("The restaurant not exist", messageException.getMessage());
    }

    @Test
    void test_getAllDishesActivePaginatedFromARestaurantOrderByCategoryAscending_withRequestParamValidButNoDishesFound_shouldThrowNoDataFoundException() {
        //Given
        RestaurantModel restaurantModelExpected = new RestaurantModel();
        restaurantModelExpected.setIdRestaurant(1L);
        int numberPageRequest = 0;
        int sizeItemsByPage = 2;
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(restaurantModelExpected);
        when(this.dishPersistencePort.getAllDishesActiveOfARestaurantOrderByCategoryAscending(PageRequest.of(numberPageRequest, sizeItemsByPage), restaurantModelExpected.getIdRestaurant()))
                                                                .thenReturn(Page.empty());
        //When
        NoDataFoundException messageException = assertThrows(
                NoDataFoundException.class,
                () ->  this.customerUseCase.getAllDishesActivePaginatedFromARestaurantOrderByCategoryAscending(numberPageRequest, sizeItemsByPage, restaurantModelExpected.getIdRestaurant()));
        //Then
        assertEquals("No content", messageException.getMessage());
    }

    @Test
    void test_addSingleDishOrder_withRequestParamOrderModelCorrect_shouldReturnOrderModelSaved() {
        //Given
        User customerOwnerOrder = new User();
        customerOwnerOrder.setIdUser(1L);
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setIdRestaurant(1L);
        CategoryModel dishType = new CategoryModel(1L, "CARNE", "");
        MeatDish meatDish = new MeatDish(1L, "name", "description", 3000000.0, "http://imagen.png", true, restaurant, dishType, 500);
        OrderDishModel orderDishFromOrderValid = new OrderDishModel();
        orderDishFromOrderValid.setDishModel(meatDish);

        OrderModel orderValid = new OrderModel(1L, 1L, LocalDate.now(), null, null, restaurant, null);
        orderDishFromOrderValid.setOrderModel(orderValid);
        orderValid.setOrdersDishesModel(Collections.singletonList(orderDishFromOrderValid));
        when(this.restaurantPersistencePort.existRestaurantById(restaurant.getIdRestaurant())).thenReturn(true);
        when(this.tokenServiceProviderPort.getTokenWithPrefixBearerFromUserAuthenticated()).thenReturn(TOKEN_WITH_PREFIX_BEARER);
        when(this.tokenServiceProviderPort.getEmailFromToken(TOKEN_WITH_PREFIX_BEARER)).thenReturn(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customerOwnerOrder);
        when(this.dishPersistencePort.findById(meatDish.getIdDish())).thenReturn(meatDish);
        when(this.orderPersistencePort.saveOrderAndOrdersDishes(orderValid)).thenReturn(orderValid);
        //When
        OrderModel orderSaved = this.customerUseCase.addSingleDishOrder(orderValid);
        //Then
        assertEquals(orderValid.getIdOrder(), orderSaved.getIdOrder());
        assertEquals(orderValid.getDate(), orderSaved.getDate());
        assertEquals(customerOwnerOrder.getIdUser(), orderSaved.getIdUserCustomer());
        assertEquals(MeatDish.class, orderSaved.getOrdersDishesModel().get(0).getDishModel().getClass());
        assertEquals(meatDish.getGrams(), ((MeatDish)orderSaved.getOrdersDishesModel().get(0).getDishModel()).getGrams());
    }

    @Test
    void test_addSingleDishOrder_withIdRestaurantInvalid_shouldThrowRestaurantNotExistException() {
        //Given
        RestaurantModel restaurantNotExist = new RestaurantModel();
        restaurantNotExist.setIdRestaurant(10000000L);
        OrderModel orderInvalidNotFoundRestaurant = new OrderModel(1L, 1L, LocalDate.now(), null, null, restaurantNotExist, null);
        when(this.restaurantPersistencePort.existRestaurantById(restaurantNotExist.getIdRestaurant())).thenReturn(false);
        //When & Then
        assertThrows(
                RestaurantNotExistException.class,
                () ->   this.customerUseCase.addSingleDishOrder(orderInvalidNotFoundRestaurant));

    }

    @Test
    void test_addSingleDishOrder_withWrongOrderBecauseDishDoesNotExist_shouldReturnThrowDishNotExistsException() {
        //Given
        User customerOwnerOrder = new User();
        customerOwnerOrder.setIdUser(1L);
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setIdRestaurant(1L);
        CategoryModel dishType = new CategoryModel(1L, "CARNE", "");
        MeatDish meatDish = new MeatDish(1L, "name", "description", 3000000.0, "http://imagen.png", true, restaurant, dishType, 500);
        OrderDishModel orderDishFromOrderValid = new OrderDishModel();
        orderDishFromOrderValid.setDishModel(meatDish);

        OrderModel orderWithDishNotExists = new OrderModel(1L, 1L, LocalDate.now(), null, null, restaurant, null);
        orderDishFromOrderValid.setOrderModel(orderWithDishNotExists);
        orderWithDishNotExists.setOrdersDishesModel(Collections.singletonList(orderDishFromOrderValid));
        when(this.restaurantPersistencePort.existRestaurantById(restaurant.getIdRestaurant())).thenReturn(true);
        when(this.tokenServiceProviderPort.getTokenWithPrefixBearerFromUserAuthenticated()).thenReturn(TOKEN_WITH_PREFIX_BEARER);
        when(this.tokenServiceProviderPort.getEmailFromToken(TOKEN_WITH_PREFIX_BEARER)).thenReturn(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customerOwnerOrder);
        when(this.dishPersistencePort.findById(meatDish.getIdDish())).thenReturn(null);
        //When & Then
        assertThrows(
                DishNotExistsException.class,
                () ->   this.customerUseCase.addSingleDishOrder(orderWithDishNotExists));
    }

    @Test
    void test_addSingleDishOrder_withTypeOfDishMeatBeingGreaterThan750Grams_shouldReturnThrowDishNotExistsException() {
        //Given
        User customerOwnerOrder = new User();
        customerOwnerOrder.setIdUser(1L);
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setIdRestaurant(1L);
        CategoryModel dishType = new CategoryModel(1L, "CARNE", "");
        MeatDish meatDishWith900Grams = new MeatDish(1L, "name", "description", 3000000.0, "http://imagen.png", true, restaurant, dishType, 900);
        OrderDishModel orderDishFromOrderValid = new OrderDishModel();
        orderDishFromOrderValid.setDishModel(meatDishWith900Grams);

        OrderModel orderWithDishNotExists = new OrderModel(1L, 1L, LocalDate.now(), null, null, restaurant, null);
        orderDishFromOrderValid.setOrderModel(orderWithDishNotExists);
        orderWithDishNotExists.setOrdersDishesModel(Collections.singletonList(orderDishFromOrderValid));
        when(this.restaurantPersistencePort.existRestaurantById(restaurant.getIdRestaurant())).thenReturn(true);
        when(this.tokenServiceProviderPort.getTokenWithPrefixBearerFromUserAuthenticated()).thenReturn(TOKEN_WITH_PREFIX_BEARER);
        when(this.tokenServiceProviderPort.getEmailFromToken(TOKEN_WITH_PREFIX_BEARER)).thenReturn(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customerOwnerOrder);
        when(this.dishPersistencePort.findById(meatDishWith900Grams.getIdDish())).thenReturn(meatDishWith900Grams);
        //When & Then
        assertThrows(
                DishNotExistsException.class,
                () ->   this.customerUseCase.addSingleDishOrder(orderWithDishNotExists));
    }

    @Test
    void test_addOrderWithMultipleDishesType_withRequestParamOrderModelCorrect_shouldReturnOrderModelSaved() {
        //Given
        User customerOwnerOrder = new User();
        customerOwnerOrder.setIdUser(1L);
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setIdRestaurant(1L);
        CategoryModel dishType = new CategoryModel(1L, "SOPAS", "");
        SoupDish soupDish = new SoupDish(1L, "name", "description", 3000000.0, "http://imagen.png", true, restaurant, dishType, "arroz");
        OrderDishModel orderDishFromOrderValid = new OrderDishModel();
        orderDishFromOrderValid.setDishModel(soupDish);

        OrderModel orderValid = new OrderModel(1L, 1L, LocalDate.now(), null, null, restaurant, null);
        orderDishFromOrderValid.setOrderModel(orderValid);
        orderValid.setOrdersDishesModel(Collections.singletonList(orderDishFromOrderValid));
        when(this.restaurantPersistencePort.existRestaurantById(restaurant.getIdRestaurant())).thenReturn(true);
        when(this.tokenServiceProviderPort.getTokenWithPrefixBearerFromUserAuthenticated()).thenReturn(TOKEN_WITH_PREFIX_BEARER);
        when(this.tokenServiceProviderPort.getEmailFromToken(TOKEN_WITH_PREFIX_BEARER)).thenReturn(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customerOwnerOrder);
        when(this.dishPersistencePort.findById(soupDish.getIdDish())).thenReturn(soupDish);
        when(this.orderPersistencePort.saveOrderAndOrdersDishes(orderValid)).thenReturn(orderValid);
        //When
        OrderModel orderSaved = this.customerUseCase.addOrderWithMultipleDishesType(orderValid);
        //Then
        assertEquals(orderValid.getIdOrder(), orderSaved.getIdOrder());
        assertEquals(orderValid.getDate(), orderSaved.getDate());
        assertEquals(customerOwnerOrder.getIdUser(), orderSaved.getIdUserCustomer());
        assertEquals(SoupDish.class, orderSaved.getOrdersDishesModel().get(0).getDishModel().getClass());
        assertEquals(soupDish.getSideDish(), ((SoupDish)orderSaved.getOrdersDishesModel().get(0).getDishModel()).getSideDish());
    }

    @Test
    void test_addOrderWithMultipleDishesType_withIdRestaurantInvalid_shouldThrowRestaurantNotExistException() {
        //Given
        RestaurantModel restaurantNotExist = new RestaurantModel();
        restaurantNotExist.setIdRestaurant(10000000L);
        OrderModel orderInvalidNotFoundRestaurant = new OrderModel(1L, 1L, LocalDate.now(), null, null, restaurantNotExist, null);
        when(this.restaurantPersistencePort.existRestaurantById(restaurantNotExist.getIdRestaurant())).thenReturn(false);
        //When & Then
        assertThrows(
                RestaurantNotExistException.class,
                () ->   this.customerUseCase.addOrderWithMultipleDishesType(orderInvalidNotFoundRestaurant));

    }

    @Test
    void test_addOrderWithMultipleDishesType_withWrongOrderBecauseDishTypeNotIsEqualsTheDish_shouldReturnThrowDishNotExistsException() {
        //Given
        User customerOwnerOrder = new User();
        customerOwnerOrder.setIdUser(1L);
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setIdRestaurant(1L);
        CategoryModel dishType = new CategoryModel(1L, "SOPAS", "");
        MeatDish meatDishWhereDishTypeIsSoup = new MeatDish(1L, "name", "description", 300000.0, "http://imagen.png", true, restaurant, dishType, 500);
        OrderDishModel orderDishFromOrderValid = new OrderDishModel();
        orderDishFromOrderValid.setDishModel(meatDishWhereDishTypeIsSoup);

        OrderModel orderWithDishNotExists = new OrderModel(1L, 1L, LocalDate.now(), null, null, restaurant, null);
        orderDishFromOrderValid.setOrderModel(orderWithDishNotExists);
        orderWithDishNotExists.setOrdersDishesModel(Collections.singletonList(orderDishFromOrderValid));
        when(this.restaurantPersistencePort.existRestaurantById(restaurant.getIdRestaurant())).thenReturn(true);
        when(this.tokenServiceProviderPort.getTokenWithPrefixBearerFromUserAuthenticated()).thenReturn(TOKEN_WITH_PREFIX_BEARER);
        when(this.tokenServiceProviderPort.getEmailFromToken(TOKEN_WITH_PREFIX_BEARER)).thenReturn(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customerOwnerOrder);
        when(this.dishPersistencePort.findById(meatDishWhereDishTypeIsSoup.getIdDish())).thenReturn(meatDishWhereDishTypeIsSoup);
        //When & Then
        assertThrows(
                DishNotExistsException.class,
                () ->   this.customerUseCase.addOrderWithMultipleDishesType(orderWithDishNotExists));
    }

    @Test
    void test_addOrderWithMultipleDishesType_withTypeOfDishMeatBeingGreaterThan750Grams_shouldReturnThrowDishNotExistsException() {
        //Given
        User customerOwnerOrder = new User();
        customerOwnerOrder.setIdUser(1L);
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setIdRestaurant(1L);
        CategoryModel dishType = new CategoryModel(1L, "CARNE", "");
        MeatDish meatDishWith900Grams = new MeatDish(1L, "name", "description", 3000000.0, "http://imagen.png", true, restaurant, dishType, 900);
        OrderDishModel orderDishFromOrderValid = new OrderDishModel();
        orderDishFromOrderValid.setDishModel(meatDishWith900Grams);

        OrderModel orderWithDishNotExists = new OrderModel(1L, 1L, LocalDate.now(), null, null, restaurant, null);
        orderDishFromOrderValid.setOrderModel(orderWithDishNotExists);
        orderWithDishNotExists.setOrdersDishesModel(Collections.singletonList(orderDishFromOrderValid));
        when(this.restaurantPersistencePort.existRestaurantById(restaurant.getIdRestaurant())).thenReturn(true);
        when(this.tokenServiceProviderPort.getTokenWithPrefixBearerFromUserAuthenticated()).thenReturn(TOKEN_WITH_PREFIX_BEARER);
        when(this.tokenServiceProviderPort.getEmailFromToken(TOKEN_WITH_PREFIX_BEARER)).thenReturn(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customerOwnerOrder);
        when(this.dishPersistencePort.findById(meatDishWith900Grams.getIdDish())).thenReturn(meatDishWith900Grams);
        //When & Then
        assertThrows(
                DishNotExistsException.class,
                () ->   this.customerUseCase.addOrderWithMultipleDishesType(orderWithDishNotExists));
    }

    @Test
    void test_addOrderWithMultipleDishesType_withFieldsEmptyInTheDish_shouldReturnThrowEmptyFieldsException() {
        //Given
        User customerOwnerOrder = new User();
        customerOwnerOrder.setIdUser(1L);
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setIdRestaurant(1L);
        CategoryModel dishTypeMeat = new CategoryModel(1L, "CARNE", "");
        CategoryModel dishTypeFlanDessert = new CategoryModel(2L, "POSTRE_FLAN", "");
        MeatDish meatDish = new MeatDish(1L, "name", "description", 300000.0, "http://imagen.png", true, restaurant, dishTypeMeat, 500);
        FlanDessertDish flanDessertDishWithToppingEmpty = new FlanDessertDish(2L, "name", "description", 300000.0, "http://imagen.png", true, restaurant, dishTypeFlanDessert, "");
        OrderDishModel orderDishTypeMeat = new OrderDishModel(null, null, meatDish, 1);

        OrderDishModel orderDishTypeFlanDessert = new OrderDishModel(null, null, flanDessertDishWithToppingEmpty, 1);

        OrderModel order = new OrderModel(1L, 1L, LocalDate.now(), null, null, restaurant, null);
        orderDishTypeMeat.setOrderModel(order);
        orderDishTypeFlanDessert.setOrderModel(order);
        order.setOrdersDishesModel(Arrays.asList(orderDishTypeMeat, orderDishTypeFlanDessert));
        when(this.restaurantPersistencePort.existRestaurantById(restaurant.getIdRestaurant())).thenReturn(true);
        when(this.tokenServiceProviderPort.getTokenWithPrefixBearerFromUserAuthenticated()).thenReturn(TOKEN_WITH_PREFIX_BEARER);
        when(this.tokenServiceProviderPort.getEmailFromToken(TOKEN_WITH_PREFIX_BEARER)).thenReturn(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customerOwnerOrder);
        when(this.dishPersistencePort.findById(meatDish.getIdDish())).thenReturn(meatDish);
        when(this.dishPersistencePort.findById(flanDessertDishWithToppingEmpty.getIdDish())).thenReturn(flanDessertDishWithToppingEmpty);
        //When & Then
        assertThrows(
                EmptyFieldsException.class,
                () ->   this.customerUseCase.addOrderWithMultipleDishesType(order));
    }
}