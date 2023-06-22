package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.exceptions.CustomerHasAOrderInProcessException;
import com.reto.plazoleta.domain.exceptions.DishNotExistsException;
import com.reto.plazoleta.domain.exceptions.OrderInProcessException;
import com.reto.plazoleta.domain.exceptions.OrderNotExistsException;
import com.reto.plazoleta.domain.exceptions.RestaurantNotExistException;
import com.reto.plazoleta.domain.model.User;
import com.reto.plazoleta.domain.spi.clients.IUserGateway;
import com.reto.plazoleta.domain.model.CategoryModel;
import com.reto.plazoleta.domain.model.DishModel;
import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.persistence.IDishPersistencePort;
import com.reto.plazoleta.domain.spi.persistence.IOrderDishPersistencePort;
import com.reto.plazoleta.domain.spi.persistence.IOrderPersistencePort;
import com.reto.plazoleta.domain.spi.persistence.IRestaurantPersistencePort;
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
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerUseCaseTest {

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

    private static final String EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN = "customer@customer.com";
    private static final String TOKEN_WITH_PREFIX_BEARER = "Bearer + Token";

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
        Integer numberPage = 0;
        Integer sizeItems = 10;
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
        when(this.orderPersistencePort.findByIdUserCustomerAndIdRestaurant(1L, userAuthenticatedByToken.getIdUser())).thenReturn(asList(orderModelExpected));
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
        orderModelRequest.setOrdersDishesModel(asList(new OrderDishModel(1L, orderModelExpected, dishModelRequest, 4)));

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
        orderModelRequest.setOrdersDishesModel(asList(new OrderDishModel(1L, orderModelExpected, dishModelRequest, 4)));

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
        Integer numberPageRequest = 0;
        Integer sizeItemsByPage = 2;
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
        Integer numberPageRequest = 0;
        Integer sizeItemsByPage = 2;
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
}