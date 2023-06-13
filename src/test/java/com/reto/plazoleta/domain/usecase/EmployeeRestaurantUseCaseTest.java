package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.exception.ObjectNotFoundException;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.IEmployeeRestaurantPersistencePort;
import com.reto.plazoleta.domain.spi.IOrderPersistencePort;
import com.reto.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.StatusOrder;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;
import com.reto.plazoleta.infraestructure.exception.NoDataFoundException;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeRestaurantUseCaseTest {

    @InjectMocks
    private EmployeeRestaurantUseCase employeeRestaurantUseCase;

    @Mock
    private IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private IUserGateway userGateway;

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    private static final String TOKEN_WITH_PREFIX_BEARER = "token";
    private static final String EMAIL_TAKEN_FROM_TOKEN = "owner@owner.com";
    private static final String EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN = "employee@employee.com";
    private static final String STATUS_FROM_ORDER = "PENDIENTE";

    @Test
    void test_saveEmployeeRestaurant_withValidFieldIdUserEmployeeInTheObjectAsEmployeeRestaurantModelAndLongAsIdOwnerHasARestaurant_shouldReturnAEmployeeRestaurantSavedModel() {
        //Given
        final RestaurantModel restaurantFoundExpected = new RestaurantModel(1L, "Puro sabor mexicano", "Libertadores Av, 18 st - 60",
                                    "+573112421021", "http://puro.sabot.com/mexicano.jpeg", 1234324454L, 1L );
        final EmployeeRestaurantModel employeeRestaurantExpected = new EmployeeRestaurantModel(1L, 1L, restaurantFoundExpected.getIdRestaurant());
        User userOwner = new User();
        userOwner.setIdUser(restaurantFoundExpected.getIdOwner());
        EmployeeRestaurantModel employeeRestaurantRequestModel = new EmployeeRestaurantModel();
        employeeRestaurantRequestModel.setIdUserEmployee(1L);
        employeeRestaurantRequestModel.setIdRestaurant(1L);

        when(this.jwtProvider.getAuthentication(TOKEN_WITH_PREFIX_BEARER)).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userOwner);
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(restaurantFoundExpected);
        when(this.employeeRestaurantPersistencePort.saveEmployeeRestaurant(employeeRestaurantRequestModel)).thenReturn(employeeRestaurantExpected);
        //When
        final EmployeeRestaurantModel employeeRestaurantSaved = this.employeeRestaurantUseCase.saveEmployeeRestaurant(employeeRestaurantRequestModel, TOKEN_WITH_PREFIX_BEARER);
        //Then
        verify(this.restaurantPersistencePort).findByIdRestaurant(1L);
        verify(this.employeeRestaurantPersistencePort).saveEmployeeRestaurant(employeeRestaurantRequestModel);
        assertEquals(employeeRestaurantExpected.getIdRestaurantEmployee(), employeeRestaurantSaved.getIdRestaurantEmployee());
        assertEquals(employeeRestaurantExpected.getIdUserEmployee(), employeeRestaurantSaved.getIdUserEmployee());
        assertEquals(employeeRestaurantExpected.getIdRestaurant(), employeeRestaurantSaved.getIdRestaurant());
    }

    @Test
    void test_saveEmployeeRestaurant_withIdOwnerFieldDoesNotOwnTheRestaurantSearchedForTheParameterOfTheIdRestaurantFieldInTheRequest_shouldThrowObjectNotFoundException() {
        //Given
        Long idOwnerFromRestaurantExpected = 1L;
        Long idOwnerFoundByEmail = 2L;
        final RestaurantModel restaurantFoundExpected = new RestaurantModel(2L, "Puro sabor mexicano", "Libertadores Av, 18 st - 60",
                "+573112421021", "http://puro.sabot.com/mexicano.jpeg", 1234324454L, idOwnerFromRestaurantExpected );

        EmployeeRestaurantModel employeeRestaurantRequestModel = new EmployeeRestaurantModel();
        employeeRestaurantRequestModel.setIdUserEmployee(1L);
        employeeRestaurantRequestModel.setIdRestaurant(1L);
        User userOwner = new User();
        userOwner.setIdUser(idOwnerFoundByEmail);
        when(this.jwtProvider.getAuthentication(TOKEN_WITH_PREFIX_BEARER)).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userOwner);
        when(this.restaurantPersistencePort.findByIdRestaurant(employeeRestaurantRequestModel.getIdRestaurant())).thenReturn(restaurantFoundExpected);
        //When
        ObjectNotFoundException message = assertThrows( ObjectNotFoundException.class, () ->
                this.employeeRestaurantUseCase.saveEmployeeRestaurant(employeeRestaurantRequestModel, TOKEN_WITH_PREFIX_BEARER));
        //Then
        verify(this.jwtProvider, times(1)).getAuthentication(TOKEN_WITH_PREFIX_BEARER);
        verify(this.userGateway, times(1)).getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_WITH_PREFIX_BEARER);
        verify(this.restaurantPersistencePort, times(1)).findByIdRestaurant(employeeRestaurantRequestModel.getIdRestaurant());
        assertEquals("Restaurant not Exist", message.getMessage());
    }

    @Test
    void test_getAllOrdersFilterByStatusAndSizeItemsByPage_withAllTheRequestParamCorrectAndTokenValid_shouldReturnAListPaginatedBySizePageAndFilteredByStatusTheOrders() {
        //Given
        User userEmployeeAuthenticatedByToken = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, "123", "EMPLEADO");
        EmployeeRestaurantModel restaurantFromEmployeeFound = new EmployeeRestaurantModel(1L, 1L, 1L);
        StatusOrder status = StatusOrder.valueOf(STATUS_FROM_ORDER.toUpperCase());
        List<OrderModel> listFromOrders = new ArrayList<>();
        listFromOrders.add(new OrderModel(1L, 1L, LocalDate.now(), StatusOrder.PENDIENTE, new EmployeeRestaurantModel(1L, 1L, 1L),
                new RestaurantModel(1L, "name", "address", "3019273456", "http://image-logo.com", 10297345345L, 2L)));
        Page<OrderModel> ordersModelPaginatedExpected = new PageImpl<>(listFromOrders);

        when(this.jwtProvider.getAuthentication(TOKEN_WITH_PREFIX_BEARER)).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticatedByToken);
        when(this.employeeRestaurantPersistencePort.findByIdUserEmployee(userEmployeeAuthenticatedByToken.getIdUser())).thenReturn(restaurantFromEmployeeFound);
        when(this.restaurantPersistencePort.findByIdRestaurant(restaurantFromEmployeeFound.getIdRestaurant())).thenReturn(new RestaurantModel(1L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 2L));
        when(this.orderPersistencePort.findAllByRestaurantEntityIdRestaurantAndStatusOrder(PageRequest.of(0, 1), restaurantFromEmployeeFound.getIdRestaurant(), status)).thenReturn(ordersModelPaginatedExpected);
        //When
        final Page<OrderModel> resultOrdersPaginated = this.employeeRestaurantUseCase.getAllOrdersFilterByStatusAndSizeItemsByPage(1, 0, STATUS_FROM_ORDER, TOKEN_WITH_PREFIX_BEARER);
        //Then
        verify(this.jwtProvider, times(1)).getAuthentication(TOKEN_WITH_PREFIX_BEARER);
        verify(this.userGateway, times(1)).getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER);
        verify(this.employeeRestaurantPersistencePort, times(1)).findByIdUserEmployee(userEmployeeAuthenticatedByToken.getIdUser());
        verify(this.restaurantPersistencePort, times(1)).findByIdRestaurant(restaurantFromEmployeeFound.getIdRestaurant());
        assertEquals(ordersModelPaginatedExpected.getTotalElements(), resultOrdersPaginated.getTotalElements());
        assertEquals(ordersModelPaginatedExpected.getTotalPages(), resultOrdersPaginated.getTotalPages());
        assertEquals(ordersModelPaginatedExpected.getNumberOfElements(), resultOrdersPaginated.getNumberOfElements());
        assertEquals(ordersModelPaginatedExpected.getContent(), resultOrdersPaginated.getContent());
    }

    @Test
    void test_getAllOrdersFilterByStatusAndSizeItemsByPage_withAllTheRequestParamCorrectButNoOrderFoundAndTokenValid_shouldThrowNoDataFoundException() {
        //Given
        User userEmployeeAuthenticatedByToken = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, "123", "EMPLEADO");
        EmployeeRestaurantModel restaurantFromEmployeeFound = new EmployeeRestaurantModel(1L, 1L, 1L);
        StatusOrder status = StatusOrder.valueOf(STATUS_FROM_ORDER.toUpperCase());

        when(this.jwtProvider.getAuthentication(TOKEN_WITH_PREFIX_BEARER)).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticatedByToken);
        when(this.employeeRestaurantPersistencePort.findByIdUserEmployee(userEmployeeAuthenticatedByToken.getIdUser())).thenReturn(restaurantFromEmployeeFound);
        when(this.restaurantPersistencePort.findByIdRestaurant(restaurantFromEmployeeFound.getIdRestaurant())).thenReturn(new RestaurantModel(1L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 2L));
        when(this.orderPersistencePort.findAllByRestaurantEntityIdRestaurantAndStatusOrder(PageRequest.of(0, 1), restaurantFromEmployeeFound.getIdRestaurant(), status)).thenReturn(Page.empty());
        //When & Then
        assertThrows( NoDataFoundException.class,
                () -> this.employeeRestaurantUseCase.getAllOrdersFilterByStatusAndSizeItemsByPage(1, 0, STATUS_FROM_ORDER, TOKEN_WITH_PREFIX_BEARER)
        );
        verify(this.jwtProvider, times(1)).getAuthentication(TOKEN_WITH_PREFIX_BEARER);
        verify(this.userGateway, times(1)).getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER);
        verify(this.employeeRestaurantPersistencePort, times(1)).findByIdUserEmployee(userEmployeeAuthenticatedByToken.getIdUser());
        verify(this.restaurantPersistencePort, times(1)).findByIdRestaurant(restaurantFromEmployeeFound.getIdRestaurant());
    }

    @Test
    void test_getAllOrdersFilterByStatusAndSizeItemsByPage_withAllTheRequestParamCorrectAndTokenValidButTheRestaurantAlreadyNoExists_shouldThrowObjectNotFoundException() {
        //Given
        User userEmployeeAuthenticatedByToken = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, "123", "EMPLEADO");
        EmployeeRestaurantModel restaurantFromEmployeeWhereRestaurantAlreadyNoExists = new EmployeeRestaurantModel(1L, 1L, 1L);

        when(this.jwtProvider.getAuthentication(TOKEN_WITH_PREFIX_BEARER)).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticatedByToken);
        when(this.employeeRestaurantPersistencePort.findByIdUserEmployee(userEmployeeAuthenticatedByToken.getIdUser())).thenReturn(restaurantFromEmployeeWhereRestaurantAlreadyNoExists);
        when(this.restaurantPersistencePort.findByIdRestaurant(restaurantFromEmployeeWhereRestaurantAlreadyNoExists.getIdRestaurant())).thenReturn(null);
        //When
        ObjectNotFoundException messageException = assertThrows(ObjectNotFoundException.class,
                () -> this.employeeRestaurantUseCase.getAllOrdersFilterByStatusAndSizeItemsByPage(1, 0, STATUS_FROM_ORDER, TOKEN_WITH_PREFIX_BEARER));
        //Then
        verify(this.jwtProvider, times(1)).getAuthentication(TOKEN_WITH_PREFIX_BEARER);
        verify(this.userGateway, times(1)).getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER);
        verify(this.employeeRestaurantPersistencePort, times(1)).findByIdUserEmployee(userEmployeeAuthenticatedByToken.getIdUser());
        verify(this.restaurantPersistencePort, times(1)).findByIdRestaurant(restaurantFromEmployeeWhereRestaurantAlreadyNoExists.getIdRestaurant());
        assertEquals("Restaurant not Exist", messageException.getMessage());
    }
}