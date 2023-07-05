package com.reto.plazoleta.infraestructure.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reto.plazoleta.application.dto.request.OrderDishTypeRequestDto;
import com.reto.plazoleta.application.dto.request.SingleDishOrderRequestDto;
import com.reto.plazoleta.domain.model.User;
import com.reto.plazoleta.domain.spi.clients.IUserGateway;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.CategoryEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.DishEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.EmployeeRestaurantEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.OrderDishEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.OrderEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.RestaurantEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.StatusOrder;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.TypeDish;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.ICategoryRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.IDishRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.IEmployeeRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.IOrderRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.IRestaurantRepository;
import com.reto.plazoleta.infraestructure.exceptionhandler.ExceptionResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderControllerTest {

    private static final String TAKE_ORDER_PATH = "/micro-small-square/restaurant/take-order";
    private static final String PENDING_ORDERS_WITH_LOW_PRIORITY_PATH = "/micro-small-square/restaurant/pending-orders";
    private static final String ADD_SINGLE_DISH_ORDER_PATH = "/micro-small-square/restaurant/%d/add-order";
    private static final String ADD_ORDER_WITH_DISH_LIST_PATH = "/micro-small-square/restaurant/%d/add-orders";
    private static final String EMAIL_EMPLOYEE = "employee@employee";
    private static final String EMAIL_CUSTOMER = "customer@customer";
    private static final String PASSWORD_EMPLOYEE = "123";
    private static final String ROL_EMPLOYEE = "EMPLEADO";
    private static final String ROL_CUSTOMER = "CLIENTE";
    private static final String TOKEN_WITH_PREFIX_BEARER = "Bearer + token";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IRestaurantRepository restaurantRepository;
    @Autowired
    private ICategoryRepository categoryRepository;
    @Autowired
    private IDishRepository dishRepository;
    @Autowired
    private IEmployeeRepository employeeRepository;
    @MockBean
    private IUserGateway userGateway;
    @MockBean
    private JwtProvider jwtProvider;

    @BeforeAll
    void initializeTestEnvironment() {
        final RestaurantEntity restaurantEntitySaved = this.restaurantRepository.save(new RestaurantEntity(1L, "name", "address", "3019273456",
                "http://image-logo.com", 10297345345L, 1L));
        this.categoryRepository.save(new CategoryEntity(1L, TypeDish.SOPAS, ""));
        this.categoryRepository.save(new CategoryEntity(2L, TypeDish.CARNE, ""));
        DishEntity dishSoup = this.dishRepository.save(new DishEntity(1L, "Caldo", "description", 300000.0, "http://image.com", true,
                                                        restaurantEntitySaved, new CategoryEntity(1L, TypeDish.SOPAS, ""))
        );
        DishEntity dishMeat = this.dishRepository.save(new DishEntity(2L, "Brochetas de cordero", "description", 300000.0, "http://image.com", true,
                                                        restaurantEntitySaved, new CategoryEntity(2L, TypeDish.CARNE, ""))
        );
        this.employeeRepository.save(new EmployeeRestaurantEntity(1L, 1L, 1L));
        this.orderRepository.save(new OrderEntity(1L, 2L, LocalDate.now(), StatusOrder.PENDIENTE,
                null, restaurantEntitySaved, Collections.singletonList(new OrderDishEntity(1L, OrderEntity.builder().idOrder(1L).build(), dishSoup, 5, null, "Arroz",null))
        ));
        this.orderRepository.save(new OrderEntity(2L, 2L, LocalDate.now(), StatusOrder.PENDIENTE,
                null, restaurantEntitySaved, Collections.singletonList(new OrderDishEntity(2L, OrderEntity.builder().idOrder(2L).build(), dishMeat, 5, 500, null,null))
        ));
    }

    @Transactional
    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_takeOrderInPendingStatus_withRequestCorrect_shouldReturnOrderInHigherPriorityAndOkStatus() throws Exception {
        User employee = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(employee);
        this.mockMvc.perform(MockMvcRequestBuilders.get(TAKE_ORDER_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrder").value(2))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.status").value("EN_PREPARACION"))
                .andExpect(jsonPath("$.idChef").value(1))
                .andExpect(jsonPath("$.dishes[0].idDish").value(2))
                .andExpect(jsonPath("$.dishes[0].typeDish").value("Carne"))
                .andExpect(jsonPath("$.dishes[0].grams").value(500));
    }

    @Transactional
    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_takeOrderInPendingStatus_withRequestCorrect_shouldReturnNotContentStatus() throws Exception {
        User employee = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(employee);
        this.orderRepository.deleteAll();
        this.mockMvc.perform(MockMvcRequestBuilders.get(TAKE_ORDER_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_pendingOrdersWithLowPriority_withRequestCorrect_shouldReturnOkStatusAndOrderList() throws Exception {
        User employee = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(employee);
        this.mockMvc.perform(MockMvcRequestBuilders.get(PENDING_ORDERS_WITH_LOW_PRIORITY_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idOrder").value(1))
                .andExpect(jsonPath("$[0].date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[0].status").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].dishes[0].idDish").value(1))
                .andExpect(jsonPath("$[0].dishes[0].typeDish").value("Sopas"))
                .andExpect(jsonPath("$[0].dishes[0].sideDish").value("Arroz"))
                .andExpect(jsonPath("$[1].idOrder").value(2))
                .andExpect(jsonPath("$[1].date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[1].status").value("PENDIENTE"))
                .andExpect(jsonPath("$[1].dishes[0].idDish").value(2))
                .andExpect(jsonPath("$[1].dishes[0].typeDish").value("Carne"))
                .andExpect(jsonPath("$[1].dishes[0].grams").value(500));
    }

    @Transactional
    @WithMockUser(username = EMAIL_EMPLOYEE, password = PASSWORD_EMPLOYEE, roles = {ROL_EMPLOYEE})
    @Test
    void test_pendingOrdersWithLowPriority_withRequestCorrectButNoOrdersFound_shouldReturnNotContentStatus() throws Exception {
        User employee = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_EMPLOYEE, ROL_EMPLOYEE);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_EMPLOYEE, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_EMPLOYEE, TOKEN_WITH_PREFIX_BEARER)).thenReturn(employee);
        this.orderRepository.deleteAll();
        this.mockMvc.perform(MockMvcRequestBuilders.get(PENDING_ORDERS_WITH_LOW_PRIORITY_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @WithMockUser(username = EMAIL_CUSTOMER, password = PASSWORD_EMPLOYEE, roles = {ROL_CUSTOMER})
    @Test
    void test_addSingleDishOrder_withRequestParamValid_shouldReturnCreatedStatusAndIdOrderSaved() throws Exception {
        User customer = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_CUSTOMER, ROL_CUSTOMER);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_CUSTOMER, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_CUSTOMER, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customer);
        SingleDishOrderRequestDto singleMeatDishOrderRequestDto = new SingleDishOrderRequestDto(2L, "carne", null, null, null, 350);
        long idRestaurantValid = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.post(String.format(ADD_SINGLE_DISH_ORDER_PATH, idRestaurantValid))
                .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(singleMeatDishOrderRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idOrder").value(3));
    }

    @WithMockUser(username = EMAIL_CUSTOMER, password = PASSWORD_EMPLOYEE, roles = {ROL_CUSTOMER})
    @Test
    void test_addSingleDishOrder_withRequestParamIdRestaurantNotExist_shouldReturnNotFoundStatus() throws Exception {
        User customer = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_CUSTOMER, ROL_CUSTOMER);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_CUSTOMER, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_CUSTOMER, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customer);
        SingleDishOrderRequestDto singleMeatDishOrderRequestDto = new SingleDishOrderRequestDto(2L, "carne", null, null, null, 350);
        long restaurantWithIdNotFound = 30000000;
        this.mockMvc.perform(MockMvcRequestBuilders.post(String.format(ADD_SINGLE_DISH_ORDER_PATH, restaurantWithIdNotFound))
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(singleMeatDishOrderRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.RESTAURANT_NOT_EXIST.getMessage()));
    }

    @WithMockUser(username = EMAIL_CUSTOMER, password = PASSWORD_EMPLOYEE, roles = {ROL_CUSTOMER})
    @Test
    void test_addSingleDishOrder_withRequestParamIdDishNotExists_shouldReturnNotFoundStatus() throws Exception {
        User customer = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_CUSTOMER, ROL_CUSTOMER);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_CUSTOMER, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_CUSTOMER, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customer);
        SingleDishOrderRequestDto singleMeatDishOrderRequestDto = new SingleDishOrderRequestDto(2000000L, "carne", null, null, null, 350);
        long idRestaurantValid = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.post(String.format(ADD_SINGLE_DISH_ORDER_PATH, idRestaurantValid))
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(singleMeatDishOrderRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.DISH_NOT_EXISTS.getMessage()));
    }

    @WithMockUser(username = EMAIL_CUSTOMER, password = PASSWORD_EMPLOYEE, roles = {ROL_CUSTOMER})
    @Test
    void test_addSingleDishOrder_withRequestParamDifferentTypeOfDishThanIdDish_shouldReturnNotFoundStatus() throws Exception {
        User customer = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_CUSTOMER, ROL_CUSTOMER);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_CUSTOMER, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_CUSTOMER, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customer);
        SingleDishOrderRequestDto soupDishButRequestOfMeat = new SingleDishOrderRequestDto(1L, "carne", null, null, null, 350);
        long idRestaurantValid = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.post(String.format(ADD_SINGLE_DISH_ORDER_PATH, idRestaurantValid))
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(soupDishButRequestOfMeat))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.DISH_NOT_EXISTS.getMessage()));
    }

    @WithMockUser(username = EMAIL_CUSTOMER, password = PASSWORD_EMPLOYEE, roles = {ROL_CUSTOMER})
    @Test
    void test_addSingleDishOrder_withTypeOfDishMeatBeingGreaterThan750Grams_shouldReturnNotFoundStatus() throws Exception {
        User customer = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_CUSTOMER, ROL_CUSTOMER);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_CUSTOMER, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_CUSTOMER, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customer);
        SingleDishOrderRequestDto meatDishWith900Grams = new SingleDishOrderRequestDto(1L, "Carne", null, null, null, 900);
        long idRestaurantValid = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.post(String.format(ADD_SINGLE_DISH_ORDER_PATH, idRestaurantValid))
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(meatDishWith900Grams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.DISH_NOT_EXISTS.getMessage()));
    }

    @Transactional
    @WithMockUser(username = EMAIL_CUSTOMER, password = PASSWORD_EMPLOYEE, roles = {ROL_CUSTOMER})
    @Test
    void test_addOrderWithMultipleDishesType_withRequestParamValid_shouldReturnCreatedStatusAndIdOrderSaved() throws Exception {
        User customer = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_CUSTOMER, ROL_CUSTOMER);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_CUSTOMER, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_CUSTOMER, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customer);
        OrderDishTypeRequestDto meatDishRequest = new OrderDishTypeRequestDto(2L, "carne", null, null, null, 350);
        OrderDishTypeRequestDto soupDishRequest = new OrderDishTypeRequestDto(1L, "sopas", null, "arroz", null, null);
        long idRestaurantValid = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.post(String.format(ADD_ORDER_WITH_DISH_LIST_PATH, idRestaurantValid))
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(Arrays.asList(meatDishRequest, soupDishRequest)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].idDish").value(2))
                .andExpect(jsonPath("$[1].idDish").value(1));
    }

    @WithMockUser(username = EMAIL_CUSTOMER, password = PASSWORD_EMPLOYEE, roles = {ROL_CUSTOMER})
    @Test
    void test_addOrderWithMultipleDishesType_withRequestParamIdRestaurantNotExist_shouldReturnNotFoundStatus() throws Exception {
        User customer = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_CUSTOMER, ROL_CUSTOMER);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_CUSTOMER, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_CUSTOMER, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customer);
        OrderDishTypeRequestDto meatDishRequest = new OrderDishTypeRequestDto(2L, "carne", null, null, null, 350);
        long restaurantWithIdNotFound = 30000000;
        this.mockMvc.perform(MockMvcRequestBuilders.post(String.format(ADD_ORDER_WITH_DISH_LIST_PATH, restaurantWithIdNotFound))
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(Collections.singletonList(meatDishRequest)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.RESTAURANT_NOT_EXIST.getMessage()));
    }

    @WithMockUser(username = EMAIL_CUSTOMER, password = PASSWORD_EMPLOYEE, roles = {ROL_CUSTOMER})
    @Test
    void test_addOrderWithMultipleDishesType_withRequestParamIdDishNotExists_shouldReturnNotFoundStatus() throws Exception {
        User customer = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_CUSTOMER, ROL_CUSTOMER);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_CUSTOMER, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_CUSTOMER, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customer);
        OrderDishTypeRequestDto meatDishRequest = new OrderDishTypeRequestDto(10000000L, "carne", null, null, null, 350);
        long idRestaurantValid = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.post(String.format(ADD_ORDER_WITH_DISH_LIST_PATH, idRestaurantValid))
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(Collections.singletonList(meatDishRequest)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.DISH_NOT_EXISTS.getMessage()));
    }

    @WithMockUser(username = EMAIL_CUSTOMER, password = PASSWORD_EMPLOYEE, roles = {ROL_CUSTOMER})
    @Test
    void test_addOrderWithMultipleDishesType_withRequestParamDifferentTypeOfDishThanIdDish_shouldReturnNotFoundStatus() throws Exception {
        User customer = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_CUSTOMER, ROL_CUSTOMER);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_CUSTOMER, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_CUSTOMER, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customer);
        OrderDishTypeRequestDto meatDishRequest = new OrderDishTypeRequestDto(2L, "sopas", null, "arroz", null, 350);
        long idRestaurantValid = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.post(String.format(ADD_ORDER_WITH_DISH_LIST_PATH, idRestaurantValid))
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(Collections.singletonList(meatDishRequest)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.DISH_NOT_EXISTS.getMessage()));
    }

    @WithMockUser(username = EMAIL_CUSTOMER, password = PASSWORD_EMPLOYEE, roles = {ROL_CUSTOMER})
    @Test
    void test_addOrderWithMultipleDishesType_withTypeOfDishMeatBeingGreaterThan750Grams_shouldReturnNotFoundStatus() throws Exception {
        User customer = new User(1L, "name", "lastName", 10937745L, "3094369283", EMAIL_CUSTOMER, ROL_CUSTOMER);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_CUSTOMER, null));
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_CUSTOMER, TOKEN_WITH_PREFIX_BEARER)).thenReturn(customer);
        OrderDishTypeRequestDto meatDishWith900Grams = new OrderDishTypeRequestDto(2L, "carne", null, null, null, 900);
        long idRestaurantValid = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.post(String.format(ADD_ORDER_WITH_DISH_LIST_PATH, idRestaurantValid))
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(Collections.singletonList(meatDishWith900Grams)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.DISH_NOT_EXISTS.getMessage()));
    }
}