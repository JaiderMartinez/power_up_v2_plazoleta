package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.exception.RestaurantNotExistException;
import com.reto.plazoleta.domain.exception.OrderInProcessException;
import com.reto.plazoleta.domain.exception.OrderNotExistsException;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.IEmployeeRestaurantPersistencePort;
import com.reto.plazoleta.domain.spi.IOrderPersistencePort;
import com.reto.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.reto.plazoleta.domain.spi.token.ITokenServiceProviderPort;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.StatusOrder;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;
import com.reto.plazoleta.domain.exception.NoDataFoundException;
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

    @Mock
    private ITokenServiceProviderPort tokenServiceProviderPort;

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
        RestaurantNotExistException message = assertThrows( RestaurantNotExistException.class, () ->
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
        RestaurantNotExistException messageException = assertThrows(RestaurantNotExistException.class,
                () -> this.employeeRestaurantUseCase.getAllOrdersFilterByStatusAndSizeItemsByPage(1, 0, STATUS_FROM_ORDER, TOKEN_WITH_PREFIX_BEARER));
        //Then
        verify(this.jwtProvider, times(1)).getAuthentication(TOKEN_WITH_PREFIX_BEARER);
        verify(this.userGateway, times(1)).getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER);
        verify(this.employeeRestaurantPersistencePort, times(1)).findByIdUserEmployee(userEmployeeAuthenticatedByToken.getIdUser());
        verify(this.restaurantPersistencePort, times(1)).findByIdRestaurant(restaurantFromEmployeeWhereRestaurantAlreadyNoExists.getIdRestaurant());
        assertEquals("Restaurant not Exist", messageException.getMessage());
    }

    @Test
    void test_assignEmployeeToOrderAndChangeStatusToInPreparation_withListFromIdOrdersCorrectAndTokenValid_shouldReturnTheListFromOrdersSaved() {
        //Given
        User userEmployeeAuthenticatedByToken = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, "123", "EMPLEADO");
        EmployeeRestaurantModel restaurantFromEmployee = new EmployeeRestaurantModel(1L, 1L, 1L);
        RestaurantModel restaurantModelExpected = new RestaurantModel(1L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 1L);
        OrderModel orderModelWithIdOneExpected = new OrderModel(1L, 1L, LocalDate.now(), StatusOrder.EN_PREPARACION, restaurantFromEmployee, restaurantModelExpected);
        OrderModel orderModelWithIdTwoExpected = new OrderModel(2L, 1L, LocalDate.now(), StatusOrder.EN_PREPARACION, restaurantFromEmployee, restaurantModelExpected);
        List<OrderModel> ordersAssignedToAEmployeeModelExpected = new ArrayList<>();
        ordersAssignedToAEmployeeModelExpected.add(orderModelWithIdOneExpected);
        ordersAssignedToAEmployeeModelExpected.add(orderModelWithIdTwoExpected);
        List<Long> idOrdersRequest = new ArrayList<>();
        idOrdersRequest.add(1L);
        idOrdersRequest.add(2L);
        when(this.jwtProvider.getAuthentication(TOKEN_WITH_PREFIX_BEARER)).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticatedByToken);
        when(this.employeeRestaurantPersistencePort.findByIdUserEmployee(1L)).thenReturn(restaurantFromEmployee);
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(restaurantModelExpected);
        when(this.orderPersistencePort.saveOrder(orderModelWithIdOneExpected)).thenReturn(orderModelWithIdOneExpected);
        when(this.orderPersistencePort.saveOrder(orderModelWithIdTwoExpected)).thenReturn(orderModelWithIdOneExpected);
        orderModelWithIdOneExpected.setEmployeeRestaurantModel(null);
        when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(orderModelWithIdOneExpected);
        orderModelWithIdTwoExpected.setEmployeeRestaurantModel(null);
        when(this.orderPersistencePort.findByIdOrder(2L)).thenReturn(orderModelWithIdTwoExpected);
        //When
        List<OrderModel> ordersAssignedToEmployeeModelSaved = this.employeeRestaurantUseCase.assignEmployeeToOrderAndChangeStatusToInPreparation(idOrdersRequest, TOKEN_WITH_PREFIX_BEARER);
        //Then
        assertEquals(ordersAssignedToAEmployeeModelExpected.get(0).getIdOrder(), ordersAssignedToEmployeeModelSaved.get(0).getIdOrder());
        assertEquals(ordersAssignedToAEmployeeModelExpected.get(0).getEmployeeRestaurantModel().getIdRestaurant(), ordersAssignedToEmployeeModelSaved.get(0).getEmployeeRestaurantModel().getIdRestaurant());
        assertEquals(ordersAssignedToAEmployeeModelExpected.get(0).getEmployeeRestaurantModel().getIdUserEmployee(), ordersAssignedToEmployeeModelSaved.get(0).getEmployeeRestaurantModel().getIdUserEmployee());
        assertEquals(ordersAssignedToAEmployeeModelExpected.get(0).getRestaurantModel().getIdRestaurant(), ordersAssignedToEmployeeModelSaved.get(0).getRestaurantModel().getIdRestaurant());
        assertEquals(ordersAssignedToAEmployeeModelExpected.get(0).getDate(), ordersAssignedToEmployeeModelSaved.get(0).getDate());
        assertEquals(ordersAssignedToAEmployeeModelExpected.get(0).getStatus(), ordersAssignedToEmployeeModelSaved.get(0).getStatus());

        assertEquals(ordersAssignedToAEmployeeModelExpected.get(1).getIdOrder(), ordersAssignedToEmployeeModelSaved.get(1).getIdOrder());
        assertEquals(ordersAssignedToAEmployeeModelExpected.get(1).getEmployeeRestaurantModel().getIdRestaurant(), ordersAssignedToEmployeeModelSaved.get(1).getEmployeeRestaurantModel().getIdRestaurant());
        assertEquals(ordersAssignedToAEmployeeModelExpected.get(1).getEmployeeRestaurantModel().getIdUserEmployee(), ordersAssignedToEmployeeModelSaved.get(1).getEmployeeRestaurantModel().getIdUserEmployee());
        assertEquals(ordersAssignedToAEmployeeModelExpected.get(1).getRestaurantModel().getIdRestaurant(), ordersAssignedToEmployeeModelSaved.get(1).getRestaurantModel().getIdRestaurant());
        assertEquals(ordersAssignedToAEmployeeModelExpected.get(1).getDate(), ordersAssignedToEmployeeModelSaved.get(1).getDate());
        assertEquals(ordersAssignedToAEmployeeModelExpected.get(1).getStatus(), ordersAssignedToEmployeeModelSaved.get(1).getStatus());
    }

    @Test
    void test_assignEmployeeToOrderAndChangeStatusToInPreparation_withListFromIdOrdersCorrectAndTokenValidButNotExistTheRestaurantFromEmployee_shouldThrowObjectNotFoundException() {
        //Given
        User userEmployeeAuthenticatedByToken = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, "123", "EMPLEADO");
        EmployeeRestaurantModel restaurantFromEmployee = new EmployeeRestaurantModel(1L, 1L, 1L);

        List<Long> idOrdersRequest = new ArrayList<>();
        idOrdersRequest.add(1L);
        idOrdersRequest.add(2L);
        when(this.jwtProvider.getAuthentication(TOKEN_WITH_PREFIX_BEARER)).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticatedByToken);
        when(this.employeeRestaurantPersistencePort.findByIdUserEmployee(1L)).thenReturn(restaurantFromEmployee);
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(null);
        //When
        RestaurantNotExistException messageException = assertThrows(RestaurantNotExistException.class,
                () ->this.employeeRestaurantUseCase.assignEmployeeToOrderAndChangeStatusToInPreparation(idOrdersRequest, TOKEN_WITH_PREFIX_BEARER));
        //Then
        assertEquals("Restaurant not Exist", messageException.getMessage());
    }

    @Test
    void test_assignEmployeeToOrderAndChangeStatusToInPreparation_withListFromIdOrdersInvalidBecauseThereIsNoExistOrder_shouldThrowOrderNotExistsException() {
        //Given
        User userEmployeeAuthenticatedByToken = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, "123", "EMPLEADO");
        EmployeeRestaurantModel restaurantFromEmployee = new EmployeeRestaurantModel(1L, 1L, 1L);
        RestaurantModel restaurantModelExpected = new RestaurantModel(1L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 1L);
        List<Long> idOrdersRequest = new ArrayList<>();
        idOrdersRequest.add(1L);
        idOrdersRequest.add(2L);
        when(this.jwtProvider.getAuthentication(TOKEN_WITH_PREFIX_BEARER)).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticatedByToken);
        when(this.employeeRestaurantPersistencePort.findByIdUserEmployee(1L)).thenReturn(restaurantFromEmployee);
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(restaurantModelExpected);
        when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(null);
        //When
        OrderNotExistsException messageException = assertThrows(OrderNotExistsException.class,
                () ->this.employeeRestaurantUseCase.assignEmployeeToOrderAndChangeStatusToInPreparation(idOrdersRequest, TOKEN_WITH_PREFIX_BEARER));
        //Then
        assertEquals("The order no exist", messageException.getMessage());
    }

    @Test
    void test_assignEmployeeToOrderAndChangeStatusToInPreparation_withAnOrderIsAlreadyAssignedToAnotherEmployee_shouldThrowOrderInProcessException() {
        //Given
        User userEmployeeAuthenticatedByToken = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, "123", "EMPLEADO");
        EmployeeRestaurantModel restaurantFromEmployee = new EmployeeRestaurantModel(1L, 1L, 1L);
        RestaurantModel restaurantModelExpected = new RestaurantModel(1L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 1L);
        OrderModel orderModelWithIdOneExpectedAndFoundAssignedToAEmployee = new OrderModel(1L, 1L, LocalDate.now(), StatusOrder.EN_PREPARACION, restaurantFromEmployee, restaurantModelExpected);
        List<Long> idOrdersRequest = new ArrayList<>();
        idOrdersRequest.add(1L);
        idOrdersRequest.add(2L);
        when(this.jwtProvider.getAuthentication(TOKEN_WITH_PREFIX_BEARER)).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticatedByToken);
        when(this.employeeRestaurantPersistencePort.findByIdUserEmployee(1L)).thenReturn(restaurantFromEmployee);
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(restaurantModelExpected);
        when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(orderModelWithIdOneExpectedAndFoundAssignedToAEmployee);
        //When
        OrderInProcessException messageException = assertThrows(OrderInProcessException.class,
                () ->this.employeeRestaurantUseCase.assignEmployeeToOrderAndChangeStatusToInPreparation(idOrdersRequest, TOKEN_WITH_PREFIX_BEARER));
        //Then
        assertEquals("This order is in process with another employee", messageException.getMessage());
    }

    @Test
    void test_assignEmployeeToOrderAndChangeStatusToInPreparation_withTheRestaurantFromTheOrderIsDifferentFromTheOneEmployeeBelongsTo_shouldThrowOrderNotExistsException() {
        //Given
        User userEmployeeAuthenticatedByToken = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, "123", "EMPLEADO");
        EmployeeRestaurantModel restaurantFromEmployee = new EmployeeRestaurantModel(1L, 1L, 1L);
        RestaurantModel restaurantFoundByIdFromRestaurantThatBelongsTheEmployeeExpected = new RestaurantModel(1L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 1L);
        RestaurantModel restaurantThatBelongsToOrderWithValueFromIdRestaurantTwo = new RestaurantModel(2L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 1L);
        OrderModel orderModelWithIdOneExpected = new OrderModel(1L, 1L, LocalDate.now(), StatusOrder.PENDIENTE, null, restaurantThatBelongsToOrderWithValueFromIdRestaurantTwo);
        List<Long> idOrdersRequest = new ArrayList<>();
        idOrdersRequest.add(1L);
        idOrdersRequest.add(2L);
        when(this.jwtProvider.getAuthentication(TOKEN_WITH_PREFIX_BEARER)).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticatedByToken);
        when(this.employeeRestaurantPersistencePort.findByIdUserEmployee(1L)).thenReturn(restaurantFromEmployee);
        when(this.restaurantPersistencePort.findByIdRestaurant(1L)).thenReturn(restaurantFoundByIdFromRestaurantThatBelongsTheEmployeeExpected);
        orderModelWithIdOneExpected.setEmployeeRestaurantModel(null);
        when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(orderModelWithIdOneExpected);
        //When
        OrderNotExistsException messageException = assertThrows(OrderNotExistsException.class,
                () ->this.employeeRestaurantUseCase.assignEmployeeToOrderAndChangeStatusToInPreparation(idOrdersRequest, TOKEN_WITH_PREFIX_BEARER));
        //Then
        assertEquals("The restaurant no belongs to this restaurant", messageException.getMessage());
    }

    @Test
    void test_changeOrderStatusToDelivered_withRequestParamOrderPinAndTokenCorrect_shouldReturnOrderUpdated() {
        //Given
        User userEmployeeAuthenticatedByToken = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, "123", "EMPLEADO");
        RestaurantModel restaurantFromOrder = new RestaurantModel(1L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 1L);
        EmployeeRestaurantModel restaurantEmployeeAuthenticated = new EmployeeRestaurantModel(1L, 1L, 1L);
        OrderModel orderModelToUpdate = new OrderModel(1L, 2L, LocalDate.now(), StatusOrder.LISTO, restaurantEmployeeAuthenticated, restaurantFromOrder);
        OrderModel orderModelExpected = new OrderModel(1L, 2L, LocalDate.now(), StatusOrder.ENTREGADO, restaurantEmployeeAuthenticated, restaurantFromOrder);
        when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(orderModelToUpdate);
        when(this.tokenServiceProviderPort.getEmailFromToken(TOKEN_WITH_PREFIX_BEARER)).thenReturn(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticatedByToken);
        when(this.employeeRestaurantPersistencePort.findByIdUserEmployee(userEmployeeAuthenticatedByToken.getIdUser())).thenReturn(restaurantEmployeeAuthenticated);
        when(this.orderPersistencePort.saveOrder(orderModelToUpdate)).thenReturn(orderModelExpected);
        //When
        OrderModel orderModelUpdated = this.employeeRestaurantUseCase.changeOrderStatusToDelivered(33334L, TOKEN_WITH_PREFIX_BEARER);
        //Then
        assertEquals(orderModelExpected.getIdOrder(), orderModelUpdated.getIdOrder());
        assertEquals(orderModelExpected.getStatus(), orderModelUpdated.getStatus());
        assertEquals(orderModelExpected.getDate(), orderModelUpdated.getDate());
        assertEquals(orderModelExpected.getRestaurantModel().getIdRestaurant(), orderModelUpdated.getRestaurantModel().getIdRestaurant());
    }

    @Test
    void test_changeOrderStatusToDelivered_withRequestParamOrderPinTheOrderDoesNotExist_shouldThrowOrderNotExistsException() {
        //Given
         when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(null);
        //When
        OrderNotExistsException messageException = assertThrows(OrderNotExistsException.class,
                () ->this.employeeRestaurantUseCase.changeOrderStatusToDelivered(33334L, TOKEN_WITH_PREFIX_BEARER));
        //Then
        assertEquals("The order no exist", messageException.getMessage());
    }

    @Test
    void test_changeOrderStatusToDelivered_withOrderInAStatusOtherThanReady_shouldThrowOrderInProcessException() {
        //Given
        RestaurantModel restaurantFromOrder = new RestaurantModel(1L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 1L);
        EmployeeRestaurantModel restaurantEmployeeAuthenticated = new EmployeeRestaurantModel(1L, 1L, 1L);
        OrderModel orderModelToUpdate = new OrderModel(1L, 2L, LocalDate.now(), StatusOrder.EN_PREPARACION, restaurantEmployeeAuthenticated, restaurantFromOrder);
        when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(orderModelToUpdate);
        //When
        OrderInProcessException messageException = assertThrows(OrderInProcessException.class,
                () ->this.employeeRestaurantUseCase.changeOrderStatusToDelivered(33334L, TOKEN_WITH_PREFIX_BEARER));
        //Then
        assertEquals("This order is in process", messageException.getMessage());
    }

    @Test
    void test_changeOrderStatusToDelivered_withRequestParamOrderPinValidButEmployeeNoBelongsToThisRestaurant_shouldThrowOrderInProcessException() {
        //Given
        User userEmployeeAuthenticatedByToken = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, "123", "EMPLEADO");
        RestaurantModel restaurantFromOrder = new RestaurantModel(1L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 1L);
        EmployeeRestaurantModel restaurantEmployeeAuthenticatedBuIsNotEqualTheFromOrder = new EmployeeRestaurantModel(1L, 1L, 200000L);
        OrderModel orderModelToUpdate = new OrderModel(1L, 2L, LocalDate.now(), StatusOrder.LISTO, restaurantEmployeeAuthenticatedBuIsNotEqualTheFromOrder, restaurantFromOrder);
        when(this.orderPersistencePort.findByIdOrder(1L)).thenReturn(orderModelToUpdate);
        when(this.tokenServiceProviderPort.getEmailFromToken(TOKEN_WITH_PREFIX_BEARER)).thenReturn(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_EMPLOYEE_TOKEN, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticatedByToken);
        when(this.employeeRestaurantPersistencePort.findByIdUserEmployee(userEmployeeAuthenticatedByToken.getIdUser())).thenReturn(restaurantEmployeeAuthenticatedBuIsNotEqualTheFromOrder);
        //When
        OrderNotExistsException messageException = assertThrows(OrderNotExistsException.class,
                () ->this.employeeRestaurantUseCase.changeOrderStatusToDelivered(33334L, TOKEN_WITH_PREFIX_BEARER));
        //Then
        assertEquals("The employee no belongs to this restaurant", messageException.getMessage());
    }
}