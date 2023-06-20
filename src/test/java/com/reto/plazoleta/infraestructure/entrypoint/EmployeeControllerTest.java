package com.reto.plazoleta.infraestructure.entrypoint;

import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.spi.IMessengerServiceProviderPort;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.EmployeeRestaurantEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.OrderEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.RestaurantEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.StatusOrder;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IEmployeeRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IOrderRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IRestaurantRepository;
import com.reto.plazoleta.infraestructure.exceptionhandler.ExceptionResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserGateway userGateway;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private IMessengerServiceProviderPort messengerServiceProviderPort;

    @Autowired
    private IEmployeeRepository employeeRepository;

    @Autowired
    private IRestaurantRepository restaurantRepository;

    @Autowired
    private IOrderRepository orderRepository;

    private static final String GET_ALL_ORDERS_BY_STATUS_PATH = "/micro-small-square/filters-orders";
    private static final String EMAIL_EMPLOYEE = "employee@employee";
    private static final String PASSWORD_EMPLOYEE = "123";
    private static final String ROL_EMPLOYEE = "EMPLEADO";
    private static final String TOKEN_WITH_PREFIX_BEARER = "Bearer + token";
    private static final String ASSIGN_AN_EMPLOYEE_TO_AN_ORDER_PATH = "/micro-small-square/restaurant/order";
    private static final String REQUEST_PARAM_ID_ORDER = "idOrder";
    private static final String CHANGE_ORDER_STATUS_AND_NOTIFY_CUSTOMER = "/micro-small-square/restaurant/order/";

    @BeforeAll
    void initializeTestEnvironment() {
        final RestaurantEntity restaurantEntitySaved = this.restaurantRepository.save(new RestaurantEntity(1L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 1L));
        OrderEntity orderEntityWithoutListFromDishes = new OrderEntity(1L, null, LocalDate.now(), StatusOrder.PENDIENTE,
                                                                        null, restaurantEntitySaved, null);
        this.orderRepository.save(orderEntityWithoutListFromDishes);

        OrderEntity orderEntityWithoutListFromDishesToSave = new OrderEntity(2L, null, LocalDate.now(), StatusOrder.EN_PREPARACION,
                                                        null, restaurantEntitySaved, null);
        this.orderRepository.save(orderEntityWithoutListFromDishesToSave);

        EmployeeRestaurantEntity employeeRestaurantSavedWithIdOne = this.employeeRepository.save(new EmployeeRestaurantEntity(1L, 1L, 1L));
        this.employeeRepository.save(new EmployeeRestaurantEntity(2L, 2L, 2L));

        OrderEntity orderEntityWithoutListFromDishesAndWithAEmployeeAssignedToTheOrder = new OrderEntity(3L, 2L, LocalDate.now(), StatusOrder.EN_PREPARACION,
                                                                                                employeeRestaurantSavedWithIdOne, restaurantEntitySaved, null);
        this.orderRepository.save(orderEntityWithoutListFromDishesAndWithAEmployeeAssignedToTheOrder);
    }

    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_getAllOrdersFilterByStatus_withTheFieldsSizeItemsAndStatusValidAndTokenValid_shouldReturnAStatusOKAndListFromOrdersPaginatedByStatus() throws Exception {
        User userEmployeeFound = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);

        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeFound);
        this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_ORDERS_BY_STATUS_PATH)
                        .param("sizeItems", "1")
                        .param("pageNumber", "0")
                        .param("status", "EN_PREPARACION")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageSize").value(1))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.content[0].status").value("EN_PREPARACION"));
    }

    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_getAllOrdersFilterByStatus_withTheFieldsSizeItemsAndStatusValidAndTokenValidButNoOrderFound_shouldReturnAStatusNotContent() throws Exception {
        User userEmployeeFound = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);

        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeFound);
        this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_ORDERS_BY_STATUS_PATH)
                        .param("sizeItems", "1")
                        .param("pageNumber", "0")
                        .param("status", "CANCELADO")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_getAllOrdersFilterByStatus_withTheFieldsSizeItemsAndStatusValidByTheDefaultValueAndTokenValid_shouldReturnListFromOrdersPaginatedWithASizeByPageDefaultAndOrderedByStatusDefaultAndStatusOK() throws Exception {
        User userEmployeeFound = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);

        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeFound);
        this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_ORDERS_BY_STATUS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.content[0].status").value("PENDIENTE"));
    }

    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_getAllOrdersFilterByStatus_withAllRequestParamValidAndTokenValidButRestaurantNotExist_shouldReturnAStatusNotFound() throws Exception {
        User userEmployeeFound = new User(2L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);

        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeFound);
        this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_ORDERS_BY_STATUS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.RESTAURANT_NOT_EXIST.getMessage()));
    }

    @Transactional
    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_assignEmployeeToOrderAndChangeStatusToInPreparation_withMultipleIdOrdersValidAndTokenCorrect_shouldReturnStatusOKAndAssignedIdOrders() throws Exception {
        User userEmployeeFound = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeFound);
        this.mockMvc.perform(MockMvcRequestBuilders.patch(ASSIGN_AN_EMPLOYEE_TO_AN_ORDER_PATH)
                        .param(REQUEST_PARAM_ID_ORDER, "1")
                        .param(REQUEST_PARAM_ID_ORDER, "2")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idOrder").value(1))
                .andExpect(jsonPath("$[1].idOrder").value(2));
    }

    @Transactional
    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_assignEmployeeToOrderAndChangeStatusToInPreparation_withSingleValidIdOrderAndCorrectToken_shouldReturnOKStatusAndAssignedIdOrder() throws Exception {
        User userEmployeeFound = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeFound);
        this.mockMvc.perform(MockMvcRequestBuilders.patch(ASSIGN_AN_EMPLOYEE_TO_AN_ORDER_PATH)
                        .param(REQUEST_PARAM_ID_ORDER, "1")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idOrder").value(1));
    }

    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_assignEmployeeToOrderAndChangeStatusToInPreparation_withNonExistingIdOrdersAndCorrectToken_shouldReturnNotFoundStatus() throws Exception {
        User userEmployeeFound = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeFound);
        this.mockMvc.perform(MockMvcRequestBuilders.patch(ASSIGN_AN_EMPLOYEE_TO_AN_ORDER_PATH)
                        .param(REQUEST_PARAM_ID_ORDER, "4")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.ORDER_NOT_FOUND.getMessage()));
    }

    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_assignEmployeeToOrderAndChangeStatusToInPreparation_withNonExistingRestaurantForEmployeeAndCorrectToken_shouldReturnNotFoundStatus() throws Exception {
        User userEmployeeFoundWhereNotExistTheRestaurant = new User(2L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeFoundWhereNotExistTheRestaurant);
        this.mockMvc.perform(MockMvcRequestBuilders.patch(ASSIGN_AN_EMPLOYEE_TO_AN_ORDER_PATH)
                        .param(REQUEST_PARAM_ID_ORDER, "4")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.RESTAURANT_NOT_EXIST.getMessage()));
    }

    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_assignEmployeeToOrderAndChangeStatusToInPreparation_withOrderAlreadyAssignedAndCorrectToken_shouldReturnConflictStatus() throws Exception {
        User userEmployeeFoundWhereNotExistTheRestaurant = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeFoundWhereNotExistTheRestaurant);
        this.mockMvc.perform(MockMvcRequestBuilders.patch(ASSIGN_AN_EMPLOYEE_TO_AN_ORDER_PATH)
                        .param(REQUEST_PARAM_ID_ORDER, "3")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("This order is in process"));
    }

    @Transactional
    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_changeOrderStatusToReadyAndNotifyCustomer_withRequestParamIdOrderIsValidAndTokenCorrect_shouldReturnStatusOkWithFieldIdOrderAndStatusReadyFromOrder() throws Exception {
        User userEmployeeAuthenticated = new User(1L, "name", "lastName", 10937745L, "3094369283",
                                                    EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);
        User userCustomerToNotify = new  User(2L, "name", "lastName", 10937745L, "3094369283",
                                        "customer@customer.com", PASSWORD_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticated);
        when(this.userGateway.getUserById(2L, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userCustomerToNotify);
        this.mockMvc.perform(MockMvcRequestBuilders.patch(CHANGE_ORDER_STATUS_AND_NOTIFY_CUSTOMER + 3)
                .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrder").value(3))
                .andExpect(jsonPath("$.status").value("LISTO"));
    }

    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_changeOrderStatusToReadyAndNotifyCustomer_withRequestParamIdOrderInvalidBecauseOrderNotExistAndTokenCorrect_shouldReturnNotFoundStatus() throws Exception {
        User userEmployeeAuthenticated = new User(1L, "name", "lastName", 10937745L, "3094369283",
                                                    EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticated);
        this.mockMvc.perform(MockMvcRequestBuilders.patch(CHANGE_ORDER_STATUS_AND_NOTIFY_CUSTOMER + 100000)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.ORDER_NOT_FOUND.getMessage()));
    }

    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_changeOrderStatusToReadyAndNotifyCustomer_withRequestParamIdOrderInvalidBecauseOrderIsInProcessAndTokenCorrect_shouldReturnConflictStatus() throws Exception {
        User userEmployeeAuthenticated = new User(1L, "name", "lastName", 10937745L, "3094369283",
                                                    EMAIL_EMPLOYEE, PASSWORD_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticated);
        this.mockMvc.perform(MockMvcRequestBuilders.patch(CHANGE_ORDER_STATUS_AND_NOTIFY_CUSTOMER + 1)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The order is in process"));
    }

    @Transactional
    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_changeOrderStatusToReadyAndNotifyCustomer_withRequestParamIdOrderValidButOrderDoesNotBelongToRestaurantAndTokenCorrect_shouldReturnNotFoundStatus() throws Exception {
        this.restaurantRepository.save(new RestaurantEntity(2L, "name", "address", "3019273456",
                                                                "http://image-logo.com", 10297345345L, 1L));
        User userEmployeeAuthenticatedButOrderNotBelongToRestaurant = new User(2L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE,
                                                    PASSWORD_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(userEmployeeAuthenticatedButOrderNotBelongToRestaurant);
        this.mockMvc.perform(MockMvcRequestBuilders.patch(CHANGE_ORDER_STATUS_AND_NOTIFY_CUSTOMER + 2)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.ORDER_NOT_FOUND.getMessage()));
    }
}