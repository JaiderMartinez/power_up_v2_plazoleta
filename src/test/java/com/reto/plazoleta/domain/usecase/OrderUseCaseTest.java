package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.model.CategoryModel;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @InjectMocks
    private OrderUseCase orderUseCase;

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

    private List<DishModel> dishModelListExpected = new ArrayList<>();
    private List<OrderDishModel> listOrderDishModelWithValueOfNameFromDishAndAmountOfDishesExpected = new ArrayList<>();

    private static final String EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN = "";
    private static final String TOKEN_WITH_PREFIX_BEARER = "Bearer + Token";

    @Test
    void test_saveOrder_withAllFieldsValidInOrderModelAndListOfDishModelCompleteAndTokenValid_shouldReturnObjectOrderModelSavedInDataBase() {
        //Given
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);

        RestaurantModel restaurantModelExpected = new RestaurantModel(1L, "Restaurante 1", "Dirección 1", "3014896273", "http://restaurante1.com", 111111L, 1L);
        restaurantModelExpected.setIdRestaurant(1L);

        dishModelListExpected.add(new DishModel(1L, "name", "description", 300000.0, "http://image.png", true,
                restaurantModelExpected, new CategoryModel(1L, "name", "description")));

        OrderModel orderModelExpected = new OrderModel();
        orderModelExpected.setIdUserCustomer(userAuthenticatedByToken.getIdUser());
        orderModelExpected.setDate(LocalDate.now());
        orderModelExpected.setStatus(StatusOrder.PENDIENTE);
        orderModelExpected.setRestaurantModel(restaurantModelExpected);

        OrderModel orderModelRequest = new OrderModel();
        orderModelRequest.setRestaurantModel(restaurantModelExpected);

        listOrderDishModelWithValueOfNameFromDishAndAmountOfDishesExpected.add(new OrderDishModel(1L, orderModelExpected, dishModelListExpected.get(0), 4));

        when(this.jwtProvider.getAuthentication("+ Token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_FROM_USER_AUTHENTICATED_BY_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(restaurantModelExpected);
        when(this.orderPersistencePort.findByIdUserCustomerAndIdRestaurant(1L, userAuthenticatedByToken.getIdUser())).thenReturn(Collections.emptyList());
        when(this.orderPersistencePort.saveOrder(orderModelRequest)).thenReturn(orderModelExpected);
        when(this.dishPersistencePort.findById(dishModelListExpected.get(0).getIdDish())).thenReturn(dishModelListExpected.get(0));
        //When
        OrderModel orderSavedModel = this.orderUseCase.saveOrder(orderModelRequest, listOrderDishModelWithValueOfNameFromDishAndAmountOfDishesExpected, TOKEN_WITH_PREFIX_BEARER);
        //Then
        assertEquals(orderModelExpected.getRestaurantModel().getIdRestaurant(), orderSavedModel.getRestaurantModel().getIdRestaurant());
        assertEquals(orderModelExpected.getStatus(), orderSavedModel.getStatus());
        assertEquals(orderModelExpected.getDate(), orderSavedModel.getDate());
        assertEquals(orderModelExpected.getIdUserCustomer(), orderSavedModel.getIdUserCustomer());
    }

    @Test
    void test_saveOrder_withAllFieldsValidInOrderModelAndListOfDishesCompleteButUserHasAnOrderInProcessAndTokenValid_shouldThrowCustomerHasAOrderInProcessException() {

    }

    @Test
    void test_saveOrder_withTheFieldIdRestaurantNotExistTheRestaurantInDataBaseAndTokenValid_shouldThrowObjectNotFoundException() {

    }

    @Test
    void test_saveOrder_withFieldIdDishNotExistsInDataBaseAndTokenValid_shouldThrowDishNotExistsException() {

    }
}