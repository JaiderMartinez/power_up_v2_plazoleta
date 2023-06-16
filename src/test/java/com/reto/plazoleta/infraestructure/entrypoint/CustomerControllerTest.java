package com.reto.plazoleta.infraestructure.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reto.plazoleta.application.dto.request.DishFromOrderRequestDto;
import com.reto.plazoleta.application.dto.request.OrderRequestDto;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.CategoryEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.DishEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.EmployeeRestaurantEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.OrderDishEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.OrderEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.RestaurantEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.StatusOrder;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.ICategoryRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IDishRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IEmployeeRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IOrderDishRepository;
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
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IRestaurantRepository restaurantRepository;

    @Autowired
    private IDishRepository dishRepository;

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private IEmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IOrderDishRepository orderDishRepository;

    @MockBean
    private IUserGateway userGateway;

    @MockBean
    private JwtProvider jwtProvider;

    private List<DishEntity> listDishEntities;

    private static final String USERNAME_CUSTOMER = "customer@customer.com";
    private static final String USERNAME_ADMIN = "admin@dmin.com";
    private static final String PASSWORD = "123";
    private static final String ROLE_ADMIN = "ADMINISTRADOR";
    private static final String ROLE_CUSTOMER = "CLIENTE";
    private static final String RESTAURANT_API_PATH = "/micro-small-square/restaurants";
    private static final String PAGE_SIZE_PARAM = "sizePage";
    private static final String REGISTER_ORDER_API_PATH = "/micro-small-square/order";
    private static final String TOKE_WITH_PREFIX_BEARER = "Bearer + token";
    private static final String NAME_OF_THE_ENTITY_ORDER = "pedidos";
    private static final String NAME_OF_THE_COLUM_PRIMARY_KEY_OF_ORDER_ENTITY = "id_pedido";


    @BeforeAll
    void initializeTestEnvironment() {
        List<RestaurantEntity> restaurantList = new ArrayList<>();
        restaurantList.add(new RestaurantEntity(1L, "Restaurante 1", "Dirección 1", "3014896273", "http://restaurante1.com", 111111L, 1L));
        restaurantList.add(new RestaurantEntity(2L, "Restaurante 2", "Dirección 2", "3224196283", "http://restaurante2.com", 222222L, 2L));
        restaurantRepository.saveAll(restaurantList);

        listDishEntities = new ArrayList<>();
        final CategoryEntity categoryEntityToSave = new CategoryEntity(1L, "Plato tipico", "Comida real");
        this.categoryRepository.save(categoryEntityToSave);

        listDishEntities.add(new DishEntity(1L, "BOWL MONTAÑERO", "Carne desmechada, Lentejas en guiso con salchichas", 35000.0, "http://image.png",
                true, restaurantList.get(0), categoryEntityToSave));
        listDishEntities.add(new DishEntity(2L, "BOWL DE POLLO", "Pechuga de pollo bañada en hongo", 45000.0, "http://image.png",
                true, restaurantList.get(0), categoryEntityToSave));
        listDishEntities.add(new DishEntity(3L, "BOWL PAISA", "Deliciosos chicarrones, frijoles, arroz blanco", 49000.0, "http://image.png",
                true, restaurantList.get(1), categoryEntityToSave));
        this.dishRepository.saveAll(listDishEntities);

        EmployeeRestaurantEntity employeeRestaurantEntityExpected = new EmployeeRestaurantEntity(1L,2L, 2L);
        this.employeeRepository.save(employeeRestaurantEntityExpected);

        OrderDishEntity orderDishEntity = new OrderDishEntity();
        orderDishEntity.setAmount(4);
        orderDishEntity.setDishEntity(listDishEntities.get(0));
        this.orderRepository.save(new OrderEntity(1L, 1L, LocalDate.now(), StatusOrder.EN_PREPARACION, employeeRestaurantEntityExpected, restaurantList.get(1), asList(orderDishEntity)));
    }

    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_getAllRestaurantsOrderByNameAsc_withIntAsSizeItemsByPages_ShouldResponseAListOfNameAndUrlLogoOfRestaurantsPageableByPageSizeOrderByNameAsc() throws Exception {
        mockMvc.perform(get(RESTAURANT_API_PATH)
                        .param(PAGE_SIZE_PARAM, "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.content[0].name").value("Restaurante 1"))
                .andExpect(jsonPath("$.content[0].urlLogo").value("http://restaurante1.com"))
                .andExpect(jsonPath("$.content[1].name").value("Restaurante 2"))
                .andExpect(jsonPath("$.content[1].urlLogo").value("http://restaurante2.com"));
    }

    @WithMockUser(username = USERNAME_ADMIN, password = PASSWORD, roles = {ROLE_ADMIN})
    @Test
    void test_getAllRestaurantsByOrderByNameAsc_withRoleAdminInTheToken_ShouldThrowAStatusForbidden() throws Exception {
        mockMvc.perform(get(RESTAURANT_API_PATH)
                        .param(PAGE_SIZE_PARAM, "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.ACCESS_DENIED.getMessage()));
    }

    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_getAllRestaurantsOrderByNameAsc_withPageSizeOneAndTwoRestaurantsInDatabase_ShouldReturnSuccessAndTwoTotalPagesAndARestaurant() throws Exception {
        mockMvc.perform(get(RESTAURANT_API_PATH)
                        .param(PAGE_SIZE_PARAM, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageSize").value(1))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Restaurante 1"))
                .andExpect(jsonPath("$.content[0].urlLogo").value("http://restaurante1.com"));
    }

    @Transactional
    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_getAllRestaurantsOrderByNameAsc_withPageSizeOneAndNotDataFound_ShouldThrowAStatusNoContent() throws Exception {
        this.orderRepository.deleteAll();
        this.orderDishRepository.deleteAll();
        this.dishRepository.deleteAll();
        restaurantRepository.deleteAll();
        mockMvc.perform(get(RESTAURANT_API_PATH)
                        .param(PAGE_SIZE_PARAM, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_getAllRestaurantsOrderByNameAsc_withNullPageSizeAndByDefaultTheValueIsFive_ShouldReturnMaximumFivePaginatedRestaurants() throws Exception {
        mockMvc.perform(get(RESTAURANT_API_PATH)
                        .param(PAGE_SIZE_PARAM, "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0));
    }

    @Transactional
    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_registerOrderFromCustomer_withAllFieldsCompleteAndValidInTheObjectOrderRequestDtoAndTokenValid_ShouldResponseAStatusCreatedAndTheValueFromFieldIdOrderSavedInTheDataBase() throws Exception {
        this.orderRepository.deleteAll();
        this.jdbcTemplate.execute("ALTER TABLE " + NAME_OF_THE_ENTITY_ORDER + " ALTER COLUMN " + NAME_OF_THE_COLUM_PRIMARY_KEY_OF_ORDER_ENTITY + " RESTART WITH 1");
        List<DishFromOrderRequestDto> listDishAndAmountRequest = new ArrayList<>();
        listDishAndAmountRequest.add(new DishFromOrderRequestDto(listDishEntities.get(0).getIdDish(), listDishEntities.get(0).getName(), 4));
        listDishAndAmountRequest.add(new DishFromOrderRequestDto(listDishEntities.get(1).getIdDish(), listDishEntities.get(1).getName(), 2));
        OrderRequestDto createOrderRequest = new OrderRequestDto(1L, listDishAndAmountRequest);
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(USERNAME_CUSTOMER, null, null));
        when(this.userGateway.getUserByEmailInTheToken(USERNAME_CUSTOMER, TOKE_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        this.mockMvc.perform(post(REGISTER_ORDER_API_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TOKE_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idOrder").value(1));
    }

    @Transactional
    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_registerOrderFromCustomer_withAllFieldsCompletedAndValidFromOrderRequestDtoButCustomerHasAnOrderInProcessAndTokenValid_ShouldResponseAStatusConflict() throws Exception {
        List<DishFromOrderRequestDto> listDishAndAmountRequest = new ArrayList<>();
        listDishAndAmountRequest.add(new DishFromOrderRequestDto(listDishEntities.get(0).getIdDish(), listDishEntities.get(0).getName(), 4));
        listDishAndAmountRequest.add(new DishFromOrderRequestDto(listDishEntities.get(1).getIdDish(), listDishEntities.get(1).getName(), 2));
        OrderRequestDto createOrderRequest = new OrderRequestDto(2L, listDishAndAmountRequest);
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(USERNAME_CUSTOMER, null, null));
        when(this.userGateway.getUserByEmailInTheToken(USERNAME_CUSTOMER, TOKE_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        this.mockMvc.perform(post(REGISTER_ORDER_API_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TOKE_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.ORDER_IN_PROCESS.getMessage()));
    }

    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_registerOrderFromCustomer_withNoExistsARestaurantWhereTheValueBeEqualTheFieldIdRestaurantFromObjectOrderRequestDtoAndTokenValid_ShouldResponseAStatusNotFound() throws Exception {
        List<DishFromOrderRequestDto> listDishAndAmountRequest = new ArrayList<>();
        listDishAndAmountRequest.add(new DishFromOrderRequestDto(listDishEntities.get(0).getIdDish(), listDishEntities.get(0).getName(), 4));
        listDishAndAmountRequest.add(new DishFromOrderRequestDto(listDishEntities.get(1).getIdDish(), listDishEntities.get(1).getName(), 2));
        OrderRequestDto createOrderRequest = new OrderRequestDto(3L, listDishAndAmountRequest);
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(USERNAME_CUSTOMER, null, null));
        when(this.userGateway.getUserByEmailInTheToken(USERNAME_CUSTOMER, TOKE_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        this.mockMvc.perform(post(REGISTER_ORDER_API_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TOKE_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.RESTAURANT_NOT_EXIST.getMessage()));
    }

    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_registerOrderFromCustomer_withNoSuchDishExistsWhereEqualTheValueOfTheIdDishFieldOfTheObjectOrderRequestDtoAndTokenValid_ShouldResponseAStatusNotFound() throws Exception {
        List<DishFromOrderRequestDto> listDishAndAmountRequest = new ArrayList<>();
        listDishAndAmountRequest.add(new DishFromOrderRequestDto(10L, listDishEntities.get(0).getName(), 4));
        listDishAndAmountRequest.add(new DishFromOrderRequestDto(listDishEntities.get(1).getIdDish(), listDishEntities.get(1).getName(), 2));
        OrderRequestDto createOrderRequest = new OrderRequestDto(1L, listDishAndAmountRequest);
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(USERNAME_CUSTOMER, null, null));
        when(this.userGateway.getUserByEmailInTheToken(USERNAME_CUSTOMER, TOKE_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);
        this.mockMvc.perform(post(REGISTER_ORDER_API_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TOKE_WITH_PREFIX_BEARER)
                        .content(this.objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.DISH_NOT_EXISTS.getMessage()));
    }

    @Transactional
    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_cancelOrder_withValidPinAndCorrectToken_shouldReturnOkStatusAndIdOrderFromOrder() throws Exception {
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        RestaurantEntity restaurantEntityFromOrder = new RestaurantEntity();
        restaurantEntityFromOrder.setIdRestaurant(1L);
        this.orderRepository.save(new OrderEntity(2L, userAuthenticatedByToken.getIdUser(), LocalDate.now(), StatusOrder.PENDIENTE, null, restaurantEntityFromOrder, null));
        long pinValid = 2L;
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(USERNAME_CUSTOMER, null, null));
        when(this.userGateway.getUserByEmailInTheToken(USERNAME_CUSTOMER, TOKE_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);

        this.mockMvc.perform(patch(REGISTER_ORDER_API_PATH + "/pin/" + pinValid)
                .header(HttpHeaders.AUTHORIZATION, TOKE_WITH_PREFIX_BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrder").value(pinValid));
    }

    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_cancelOrder_withIncorrectPinBecauseOrderDoesNotExistAndCorrectToken_shouldReturnNotFoundStatus() throws Exception {
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        long pinInvalid = 2000000L;
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(USERNAME_CUSTOMER, null, null));
        when(this.userGateway.getUserByEmailInTheToken(USERNAME_CUSTOMER, TOKE_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);

        this.mockMvc.perform(patch(REGISTER_ORDER_API_PATH + "/pin/" + pinInvalid)
                        .header(HttpHeaders.AUTHORIZATION, TOKE_WITH_PREFIX_BEARER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.ORDER_NOT_FOUND.getMessage()));
    }

    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_cancelOrder_withValidPinButOrderStatusOtherThanPendingAndCorrectToken_shouldReturnConflictStatus() throws Exception {
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(1L);
        long pinInvalidItsStatusIsInPreparation = 1L;
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(USERNAME_CUSTOMER, null, null));
        when(this.userGateway.getUserByEmailInTheToken(USERNAME_CUSTOMER, TOKE_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);

        this.mockMvc.perform(patch(REGISTER_ORDER_API_PATH + "/pin/" + pinInvalidItsStatusIsInPreparation)
                        .header(HttpHeaders.AUTHORIZATION, TOKE_WITH_PREFIX_BEARER))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse"));
    }

    @WithMockUser(username = USERNAME_CUSTOMER, password = PASSWORD, roles = {ROLE_CUSTOMER})
    @Test
    void test_cancelOrder_withValidPinButTheOrderNotOwnedByUserAndCorrectToken_shouldReturnNotFoundStatus() throws Exception {
        User userAuthenticatedByToken = new User();
        userAuthenticatedByToken.setIdUser(2L);
        long pinInvalidOrderDoesNotBelongToUser = 1;
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(USERNAME_CUSTOMER, null, null));
        when(this.userGateway.getUserByEmailInTheToken(USERNAME_CUSTOMER, TOKE_WITH_PREFIX_BEARER)).thenReturn(userAuthenticatedByToken);

        this.mockMvc.perform(patch(REGISTER_ORDER_API_PATH + "/pin/" + pinInvalidOrderDoesNotBelongToUser)
                        .header(HttpHeaders.AUTHORIZATION, TOKE_WITH_PREFIX_BEARER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.ORDER_NOT_FOUND.getMessage()));
    }
}