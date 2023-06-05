package com.reto.plazoleta.infraestructure.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reto.plazoleta.application.dto.request.RequestToCreateRestaurantDto;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.infraestructure.configuration.security.exception.UserDoesNotExistException;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;
import com.reto.plazoleta.infraestructure.exceptionhandler.ExceptionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IUserGateway userGateway;

    private static final String CREATE_RESTAURANT_API_PATH = "/micro-small-square/create-restaurant";
    private static final String USERNAME_ADMIN = "admin@dmin.com";
    private static final String PASSWORD = "123";
    private static final String ROLE_ADMIN = "ADMINISTRADOR";
    private static final String BEARER_TOKEN = "Bearer Token";

    @WithMockUser(username = USERNAME_ADMIN, password = PASSWORD, roles = {ROLE_ADMIN})
    @Test
    void test_saveRestaurant_withAllDataCorrectInTheObjectRequestToCreateRestaurantDto_shouldResponseSavedIdRestaurantAndStatusCreated() throws Exception {
        RequestToCreateRestaurantDto restaurantRequestDto = new RequestToCreateRestaurantDto();
        restaurantRequestDto.setName("Nachos de Monterrey");
        restaurantRequestDto.setAddress("Calle 42 # 15-34");
        restaurantRequestDto.setPhone("3893981259");
        restaurantRequestDto.setUrlLogo("http://url-logo.com");
        restaurantRequestDto.setNit(187273543L);
        restaurantRequestDto.setIdOwner(1L);

        User userExpected = new User();
        userExpected.setRol("PROPIETARIO");
        when(userGateway.getUserById(1L, BEARER_TOKEN)).thenReturn(userExpected);

        mockMvc.perform(post(CREATE_RESTAURANT_API_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idRestaurant").value(1));
    }

    @WithMockUser(username = USERNAME_ADMIN, password = PASSWORD, roles = {ROLE_ADMIN})
    @Test
    void test_saveRestaurant_withInvalidFieldPhoneInRequestToCreateRestaurantDto_shouldReturnStatusBadRequest() throws Exception {
        RequestToCreateRestaurantDto restaurantRequestDto = new RequestToCreateRestaurantDto();
        restaurantRequestDto.setName("Nachos de Monterrey");
        restaurantRequestDto.setAddress("Calle 42 # 15-34");
        restaurantRequestDto.setPhone("+54834573454322");
        restaurantRequestDto.setUrlLogo("http://url-logo.com");
        restaurantRequestDto.setNit(187273543L);
        restaurantRequestDto.setIdOwner(1L);

        mockMvc.perform(post(CREATE_RESTAURANT_API_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.INVALID_DATA.getMessage()));
    }

    @WithMockUser(username = USERNAME_ADMIN, password = PASSWORD, roles = {ROLE_ADMIN})
    @Test
    void test_saveRestaurant_withInvalidFieldNameInRequestToCreateRestaurantDto_shouldReturnStatusConflict() throws Exception {
        RequestToCreateRestaurantDto restaurantRequestDto = new RequestToCreateRestaurantDto();
        restaurantRequestDto.setName("54234723");
        restaurantRequestDto.setAddress("Calle 42 # 15-34");
        restaurantRequestDto.setPhone("+543893981259");
        restaurantRequestDto.setUrlLogo("http://url-logo.com");
        restaurantRequestDto.setNit(187273543L);
        restaurantRequestDto.setIdOwner(1L);

        mockMvc.perform(post(CREATE_RESTAURANT_API_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.INVALID_DATA.getMessage()));
    }

    @WithMockUser(username = USERNAME_ADMIN, password = PASSWORD, roles = {ROLE_ADMIN})
    @Test
    void test_saveRestaurant_withAllFieldsEmptyInRequestToCreateRestaurantDto_shouldReturnStatusConflict() throws Exception {
        RequestToCreateRestaurantDto restaurantRequestDto = new RequestToCreateRestaurantDto();
        restaurantRequestDto.setName("");
        restaurantRequestDto.setAddress("");
        restaurantRequestDto.setPhone("");
        restaurantRequestDto.setUrlLogo("");
        restaurantRequestDto.setNit(null);
        restaurantRequestDto.setIdOwner(null);

        mockMvc.perform(post(CREATE_RESTAURANT_API_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.EMPTY_FIELDS.getMessage()));
    }

    @WithMockUser(username = USERNAME_ADMIN, password = PASSWORD, roles = {ROLE_ADMIN})
    @Test
    void test_saveRestaurant_withObjectRequestToCreateRestaurantDtoAndFieldIdOwnerIsRoleDifferentFromOwner_shouldResponseStatusForbidden() throws Exception {
        RequestToCreateRestaurantDto restaurantRequestDto = new RequestToCreateRestaurantDto();
        restaurantRequestDto.setName("Nachos de Monterrey");
        restaurantRequestDto.setAddress("Calle 42 # 15-34");
        restaurantRequestDto.setPhone("3893981259");
        restaurantRequestDto.setUrlLogo("http://url-logo.com");
        restaurantRequestDto.setNit(187273543L);
        restaurantRequestDto.setIdOwner(1L);

        User userExpected = new User();
        userExpected.setRol("ADMINISTRADOR");
        when(userGateway.getUserById(1L, BEARER_TOKEN)).thenReturn(userExpected);

        mockMvc.perform(post(CREATE_RESTAURANT_API_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantRequestDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.ACCESS_DENIED.getMessage()));
    }

    @WithMockUser(username = USERNAME_ADMIN, password = PASSWORD, roles = {ROLE_ADMIN})
    @Test
    void test_saveRestaurant_withObjectRequestToCreateRestaurantDtoAndValueInTheFieldIdOwnerThereIsNotUserWhereTheIdIsOne_shouldReturnNotFoundStatus() throws Exception {
        RequestToCreateRestaurantDto restaurantRequestDto = new RequestToCreateRestaurantDto();
        restaurantRequestDto.setName("Nachos de Monterrey");
        restaurantRequestDto.setAddress("Calle 42 # 15-34");
        restaurantRequestDto.setPhone("3893981259");
        restaurantRequestDto.setUrlLogo("http://url-logo.com");
        restaurantRequestDto.setNit(187273543L);
        restaurantRequestDto.setIdOwner(1L);
        when(userGateway.getUserById(1L, BEARER_TOKEN)).thenThrow(UserDoesNotExistException.class);

        mockMvc.perform(post(CREATE_RESTAURANT_API_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionResponse.USER_DOES_NOT_EXIST.getMessage()));
    }
}