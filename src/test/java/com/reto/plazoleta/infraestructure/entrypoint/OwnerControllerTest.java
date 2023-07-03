package com.reto.plazoleta.infraestructure.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reto.plazoleta.application.dto.request.DishCreateRequestDto;
import com.reto.plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.reto.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.reto.plazoleta.domain.model.User;
import com.reto.plazoleta.domain.spi.clients.IUserGateway;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.CategoryEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.DishEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.RestaurantEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.TypeDish;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.ICategoryRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.IDishRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IRestaurantRepository restaurantRepository;
    @Autowired
    private ICategoryRepository categoryRepository;
    @Autowired
    private IDishRepository dishRepository;
    @MockBean
    private IUserGateway userGateway;
    @MockBean
    private JwtProvider jwtProvider;

    private static final String USERNAME_OWNER = "owner@owner.com";
    private static final String PASSWORD_OWNER = "123";
    private static final String ROL_OWNER = "PROPIETARIO";
    private static final String CREATE_DISH = "/micro-small-square/restaurant/dish";
    private static final String ADD_A_EMPLOYEE_TO_THE_RESTAURANT = "/micro-small-square/restaurant/employee";
    private static final String UPDATE_DISH = "/micro-small-square/restaurant/dish/update";
    private static final String TOKEN_VALID = "Bearer + token";
    private static final String EMAIL_TAKEN_FROM_TOKEN = "owner@owner.com";
    private  static final String PATH_RESTAURANT = "/micro-small-square/restaurant/";
    private  static final String PATH_DISH = "/dish/";
    private  static final String PATH_UPDATE_STATUS = "/update/status/";

    @BeforeAll
    void initializeTestEnvironment() {
        this.restaurantRepository.save(new RestaurantEntity(1L, "salado", "bellavista",
                "+123456779", "urlLogo", 108438453L, 15L));
        this.categoryRepository.save(new CategoryEntity(1L, TypeDish.SOPAS, "salado"));

        this.dishRepository.save(new DishEntity(1L, "name", "description", 300000.0, "http://image.com", true, new RestaurantEntity(1L, "salado",
                "bellavista", "+123456779", "urlLogo", 108438453L, 15L), new CategoryEntity(1L, TypeDish.SOPAS, "salado")));
    }

    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_createDishFromARestaurant_withAllFieldsValidDishCreateRequestDto_ShouldReturnADishWithStatusCreated() throws Exception {
        DishCreateRequestDto dish = new DishCreateRequestDto("plato1", 20000.00, "description", "http://image.com",
                1L, 1L);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DISH)
                        .content(objectMapper.writeValueAsString(dish))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    }

    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_createDishFromARestaurant_withDishCreateRequestDtoWhereNotExistTheCategory_ShouldThrowBadRequestExceptionIfCategoryNotExist() throws Exception {
        DishCreateRequestDto dish = new DishCreateRequestDto("plato1", 20000.00, "description", "http://image.com",
                9L, 1L);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DISH)
                        .content(objectMapper.writeValueAsString(dish))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.INVALID_DATA.getMessage()));
    }

    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_createDishFromARestaurant_withTheRestaurantNotExistInObjectDishCreateRequestDto_ShouldResponseAStatusNotFound() throws Exception {
        DishCreateRequestDto dish = new DishCreateRequestDto("plato1", 20000.00, "description", "http://image.com",
                1L, 9L);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DISH)
                        .content(objectMapper.writeValueAsString(dish))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.RESTAURANT_NOT_EXIST.getMessage()));
    }

    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_createDishFromARestaurant_withZeroPriceInDishCreation_ShouldThrowBadRequestException() throws Exception {
        DishCreateRequestDto dish = new DishCreateRequestDto("plato1", 0.0, "description", "http://image.com",
                1L, 1L);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DISH)
                        .content(objectMapper.writeValueAsString(dish))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.INVALID_DATA.getMessage()));
    }

    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_createDishFromARestaurant_withNegativePriceInDishCreation_ShouldThrowBadRequestException() throws Exception {
        DishCreateRequestDto dish = new DishCreateRequestDto("plato1", -10.0, "description", "http://image.com",
                1L, 1L);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DISH)
                        .content(objectMapper.writeValueAsString(dish))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.INVALID_DATA.getMessage()));
    }
  
    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_updateDishPriceAndDescription_withUpdateDishRequestDto_ShouldStatusOk() throws Exception {
        final DishEntity dishSavedEntityExpected = this.dishRepository.save(new DishEntity(1L, "name", "descriptionDish", 15000.0, "http://imagen.jpeg",
                true, new RestaurantEntity(1L, "salado", "bellavista", "+123456779", "urlLogo", 108438453L, 15L),
                        new CategoryEntity(1L, TypeDish.SOPAS, "salado")));
        UpdateDishRequestDto updateDishRequestDto = new UpdateDishRequestDto(1L, 1L, 20.0, "description");

        mockMvc.perform(MockMvcRequestBuilders.patch(UPDATE_DISH)
                        .content(objectMapper.writeValueAsString(updateDishRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDish").value(1));

        assertEquals(dishSavedEntityExpected.getIdDish(), updateDishRequestDto.getIdDish());
        assertEquals(dishSavedEntityExpected.getRestaurantEntity().getIdRestaurant(), updateDishRequestDto.getIdRestaurant());
    }
  
    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_updateDishPriceAndDescription_withInvalidRestaurant_ShouldThrowObjectNotFoundExceptionRestaurantNotPermitted() throws Exception {
        RestaurantEntity restaurantOwnerDish = new RestaurantEntity(2L, "salado", "bellavista", "+123456779", "urlLogo", 108438453L, 15L);

        DishEntity dish = new DishEntity(1L, "name", "descriptionDish", 15000.0, "http://imagen.jpeg", true, restaurantOwnerDish, new CategoryEntity(1L, TypeDish.SOPAS, "salado"));
        restaurantRepository.save(restaurantOwnerDish);
        dishRepository.save(dish);

        UpdateDishRequestDto updateDish = new UpdateDishRequestDto(1L, 1L, 20.0, "description");

        mockMvc.perform(MockMvcRequestBuilders.patch(UPDATE_DISH)
                        .content(objectMapper.writeValueAsString(updateDish))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.INVALID_DATA.getMessage()));
    }

    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_saveUserEmployeeInARestaurant_withAllFieldsCompleteAndValidInTheObjectAsRestaurantEmployeeRequestDto_shouldReturnAStatusCreatedAndTheIdRestaurantEmployee() throws Exception {
        RestaurantEmployeeRequestDto restaurantEmployeeRequest = new RestaurantEmployeeRequestDto();
        restaurantEmployeeRequest.setIdRestaurant(1L);
        restaurantEmployeeRequest.setIdUserEmployee(2L);
        User userFoundByToken = new User();
        userFoundByToken.setIdUser(15L);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_VALID)).thenReturn(userFoundByToken);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_TOKEN, null));
        this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_A_EMPLOYEE_TO_THE_RESTAURANT)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_VALID)
                        .content(objectMapper.writeValueAsString(restaurantEmployeeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idRestaurantEmployee").value(1));
    }

    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_saveUserEmployeeInARestaurant_withFieldIdRestaurantThatIsNotRelatedToARestaurantRequestedInTheParameter_shouldReturnAStatusNotFound() throws Exception {
        RestaurantEmployeeRequestDto restaurantEmployeeRequestWhereIdOwnerRestaurantDoesNotHaveARestaurant = new RestaurantEmployeeRequestDto();
        restaurantEmployeeRequestWhereIdOwnerRestaurantDoesNotHaveARestaurant.setIdRestaurant(0L);
        restaurantEmployeeRequestWhereIdOwnerRestaurantDoesNotHaveARestaurant.setIdUserEmployee(2L);
        User userFoundByToken = new User();
        userFoundByToken.setIdUser(18L);
        when(this.userGateway.getUserByEmailInTheToken(USERNAME_OWNER, TOKEN_VALID)).thenReturn(userFoundByToken);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_TOKEN, null));
        this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_A_EMPLOYEE_TO_THE_RESTAURANT)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_VALID)
                        .content(objectMapper.writeValueAsString(restaurantEmployeeRequestWhereIdOwnerRestaurantDoesNotHaveARestaurant))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.RESTAURANT_NOT_EXIST.getMessage()));
    }

    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_enableOrDisableDishByFieldStatus_withFieldsCompleteAndValidFromObjectAsDishUpdateStatusRequestDtoAndTokenValid_shouldResponseStatusOKAndObjectAsDishStatusResponseDto() throws Exception {
        User userFoundByToken = new User();
        userFoundByToken.setIdUser(15L);
        long restaurantValid = 1L;
        long dishValid = 1L;
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_VALID)).thenReturn(userFoundByToken);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_TOKEN, null));
        this.mockMvc.perform(MockMvcRequestBuilders.patch(PATH_RESTAURANT + restaurantValid + PATH_DISH + dishValid + PATH_UPDATE_STATUS + false)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_VALID))
                .andExpect(jsonPath("$.idDish").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.active").value(false));

        verify(this.userGateway).getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_VALID);
        verify(this.jwtProvider).getAuthentication("+ token");
    }

    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_enableOrDisableDishByFieldStatus_withDishUpdateStatusRequestDtoButTheValueFromFieldIdRestaurantNotBelongsToUserOwnerThisRestaurantAndTokenValid_shouldResponseStatusBadRequest() throws Exception {
        User userFoundByTokenWhereNotHasARestaurant = new User();
        userFoundByTokenWhereNotHasARestaurant.setIdUser(10L);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_VALID)).thenReturn(userFoundByTokenWhereNotHasARestaurant);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_TOKEN, null));
        long restaurantDoesNotBelongsToUserOwner = 2L;
        long dishValid = 1L;
        this.mockMvc.perform(MockMvcRequestBuilders.patch(PATH_RESTAURANT + restaurantDoesNotBelongsToUserOwner + PATH_DISH + dishValid + PATH_UPDATE_STATUS + false)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_VALID))
                .andExpect(jsonPath("$.message").value(ExceptionResponse.RESTAURANT_NOT_EXIST.getMessage()));

        verify(this.userGateway).getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_VALID);
        verify(this.jwtProvider).getAuthentication("+ token");
    }

    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_enableOrDisableDishByFieldStatus_withMismatchedIdRestaurantWithFieldIdRestaurantFromDishAndTokenValid_shouldResponseStatusBadRequest() throws Exception {
        RestaurantEntity restaurantEntityExpected = new RestaurantEntity();
        restaurantEntityExpected.setIdRestaurant(2L);
        restaurantEntityExpected.setIdOwner(18L);
        this.restaurantRepository.save(restaurantEntityExpected);
        this.dishRepository.save(new DishEntity(1L, "name", "description", 300000.0, "http://image.com", true, restaurantEntityExpected, new CategoryEntity(1L, TypeDish.SOPAS, "salado")));

        User userEmployeeFoundByTokenWhereNotHasARestaurant = new User();
        userEmployeeFoundByTokenWhereNotHasARestaurant.setIdUser(15L);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_VALID)).thenReturn(userEmployeeFoundByTokenWhereNotHasARestaurant);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_TOKEN, null));
        long restaurantDifferentFromOneOfTheDishToUpdateDishStatus  = 2L;
        long dishValid = 1L;
        this.mockMvc.perform(MockMvcRequestBuilders.patch(PATH_RESTAURANT + restaurantDifferentFromOneOfTheDishToUpdateDishStatus + PATH_DISH + dishValid + PATH_UPDATE_STATUS + false)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_VALID))
                .andExpect(jsonPath("$.message").value(ExceptionResponse.INVALID_DATA.getMessage()));

        verify(this.userGateway).getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_VALID);
        verify(this.jwtProvider).getAuthentication("+ token");
    }

    @Transactional
    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_enableOrDisableDishByFieldStatus_withValueFromFieldIdDishNotFoundInDataBaseAndTokenValid_shouldResponseStatusNotFound() throws Exception {
        this.dishRepository.deleteAll();
        User userEmployeeFoundByTokenWhereNotHasARestaurant = new User();
        userEmployeeFoundByTokenWhereNotHasARestaurant.setIdUser(15L);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_VALID)).thenReturn(userEmployeeFoundByTokenWhereNotHasARestaurant);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_TOKEN, null));
        long restaurantValid = 1L;
        long dishNotExist = 2L;
        this.mockMvc.perform(MockMvcRequestBuilders.patch(PATH_RESTAURANT + restaurantValid + PATH_DISH + dishNotExist + PATH_UPDATE_STATUS + false)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_VALID))
                .andExpect(jsonPath("$.message").value(ExceptionResponse.DISH_NOT_EXISTS.getMessage()));

        verify(this.userGateway).getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_VALID);
        verify(this.jwtProvider).getAuthentication("+ token");
    }

    @WithMockUser(username = USERNAME_OWNER, password = PASSWORD_OWNER, roles = {ROL_OWNER})
    @Test
    void test_enableOrDisableDishByFieldStatus_withValueFromFieldIdRestaurantNotFoundInDataBaseAndTokenValid_shouldResponseStatusNotFound() throws Exception {
        User userEmployeeFoundByTokenWhereNotHasARestaurant = new User();
        userEmployeeFoundByTokenWhereNotHasARestaurant.setIdUser(15L);
        when(this.userGateway.getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_VALID)).thenReturn(userEmployeeFoundByTokenWhereNotHasARestaurant);
        when(this.jwtProvider.getAuthentication("+ token")).thenReturn(new UsernamePasswordAuthenticationToken(EMAIL_TAKEN_FROM_TOKEN, null));
        long restaurantNotExist = 10L;
        long dishValid = 1L;
        this.mockMvc.perform(MockMvcRequestBuilders.patch(PATH_RESTAURANT + restaurantNotExist + PATH_DISH + dishValid + PATH_UPDATE_STATUS + false)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_VALID))
                .andExpect(jsonPath("$.message").value(ExceptionResponse.RESTAURANT_NOT_EXIST.getMessage()));

        verify(this.userGateway).getUserByEmailInTheToken(EMAIL_TAKEN_FROM_TOKEN, TOKEN_VALID);
        verify(this.jwtProvider).getAuthentication("+ token");
    }
}