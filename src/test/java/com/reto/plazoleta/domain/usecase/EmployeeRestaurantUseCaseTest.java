package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.exception.ObjectNotFoundException;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.IEmployeeRestaurantPersistencePort;
import com.reto.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private static final String TOKEN_WITH_PREFIX_BEARER = "";
    private static final String EMAIL_TAKEN_FROM_TOKEN = "owner@owner.com";

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
        assertEquals("Restaurant not Exist", message.getMessage());
    }
}