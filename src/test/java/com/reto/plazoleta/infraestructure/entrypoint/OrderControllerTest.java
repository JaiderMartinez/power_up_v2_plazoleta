package com.reto.plazoleta.infraestructure.entrypoint;

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
    private static final String EMAIL_EMPLOYEE = "employee@employee";
    private static final String PASSWORD_EMPLOYEE = "123";
    private static final String ROL_EMPLOYEE = "EMPLEADO";
    private static final String TOKEN_WITH_PREFIX_BEARER = "Bearer + token";
    @Autowired
    private MockMvc mockMvc;
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
        this.categoryRepository.save(new CategoryEntity(1L, TypeDish.SOPA, ""));
        this.categoryRepository.save(new CategoryEntity(2L, TypeDish.CARNE, ""));
        DishEntity dishSoup = this.dishRepository.save(new DishEntity(1L, "Caldo", "description", 300000.0, "http://image.com", true,
                                                        restaurantEntitySaved, new CategoryEntity(1L, TypeDish.SOPA, ""))
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
}