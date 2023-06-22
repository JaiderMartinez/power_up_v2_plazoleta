package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.exceptions.EmptyFieldsException;
import com.reto.plazoleta.domain.exceptions.InvalidDataException;
import com.reto.plazoleta.domain.spi.clients.IUserGateway;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.persistence.IRestaurantPersistencePort;
import com.reto.plazoleta.infraestructure.drivenadapter.webclients.dto.request.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUseCaseTest {

    @InjectMocks
    private AdminUseCase adminUseCase;
    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock
    private IUserGateway userGateway;
    private static final String TOKEN = "token";

    @Test
    void test_saveRestaurant_withNonEmptyRestaurantModelTheFieldsNameAndPhoneFormatCorrectAndIdOwnerExistAndEqualsOwnerRoleAndAndValidToken_ShouldReturnRestaurantModelSaved() {
        //Given
        UserDto userWithRoleOwner = new UserDto();
        userWithRoleOwner.setRol("PROPIETARIO");
        RestaurantModel restaurantExpected = new RestaurantModel(1L, "Sabroson17", "Cra 10", "3018452367", "http://sabroson.img", 843775L, 1L);
        when(userGateway.getUserById(1L, TOKEN)).thenReturn(userWithRoleOwner);
        when(restaurantPersistencePort.saveRestaurant(restaurantExpected)).thenReturn(restaurantExpected);
        //When
        RestaurantModel restaurantSaved = adminUseCase.saveRestaurant(restaurantExpected, TOKEN);
        //Then
        verify(restaurantPersistencePort).saveRestaurant(restaurantExpected);
        assertEquals(restaurantExpected.getIdRestaurant(), restaurantSaved.getIdRestaurant());
        assertEquals(restaurantExpected.getName(), restaurantSaved.getName());
        assertEquals(restaurantExpected.getUrlLogo(), restaurantSaved.getUrlLogo());
        assertEquals(restaurantExpected.getAddress(), restaurantSaved.getAddress());
        assertEquals(restaurantExpected.getPhone(), restaurantSaved.getPhone());
        assertEquals(restaurantExpected.getNit(), restaurantSaved.getNit());
        assertEquals(restaurantExpected.getIdOwner(), restaurantSaved.getIdOwner());
    }

    @Test
    void test_saveRestaurant_withStringAsPhoneInvalidRestaurant_ShouldThrowInvalidDataException() {
        //Given
        RestaurantModel restaurantWithPhoneWrong = new RestaurantModel();
        restaurantWithPhoneWrong.setName("Sabroson17");
        restaurantWithPhoneWrong.setUrlLogo("http://sabroson.img");
        restaurantWithPhoneWrong.setAddress("Cra 10");
        restaurantWithPhoneWrong.setPhone("4563018452367");
        restaurantWithPhoneWrong.setNit(843775L);
        restaurantWithPhoneWrong.setIdOwner(5L);
        // When & Then
        Assertions.assertThrows(
                InvalidDataException.class,
                () -> adminUseCase.saveRestaurant(restaurantWithPhoneWrong, TOKEN)
        );
    }

    @Test
    void test_saveRestaurant_withStringAsNameRestaurantWithOnlyNumbers_ShouldThrowInvalidDataException() {
        //Given
        RestaurantModel restaurantWhereNameOnlyContainsNumbers = new RestaurantModel();
        restaurantWhereNameOnlyContainsNumbers.setName("17645676");
        restaurantWhereNameOnlyContainsNumbers.setUrlLogo("http://sabroson.img");
        restaurantWhereNameOnlyContainsNumbers.setAddress("Cra 10");
        restaurantWhereNameOnlyContainsNumbers.setPhone("3018452367");
        restaurantWhereNameOnlyContainsNumbers.setNit(843775L);
        restaurantWhereNameOnlyContainsNumbers.setIdOwner(5L);

        // When & Then
        Assertions.assertThrows(
                InvalidDataException.class,
                () -> adminUseCase.saveRestaurant(restaurantWhereNameOnlyContainsNumbers, TOKEN)
        );
    }

    @Test
    void test_saveRestaurant_withAllFieldsAreEmptyInObjectRestaurantModel_ShouldThrowEmptyFieldsException() {
        //Given
        RestaurantModel restaurantWhereAllFieldsAreEmpty = new RestaurantModel();
        restaurantWhereAllFieldsAreEmpty.setName(" ");
        restaurantWhereAllFieldsAreEmpty.setUrlLogo("");
        restaurantWhereAllFieldsAreEmpty.setAddress("");
        restaurantWhereAllFieldsAreEmpty.setPhone("");
        restaurantWhereAllFieldsAreEmpty.setNit(null);
        restaurantWhereAllFieldsAreEmpty.setIdOwner(null);
        // When & Then
        Assertions.assertThrows(
                EmptyFieldsException.class,
                () -> adminUseCase.saveRestaurant(restaurantWhereAllFieldsAreEmpty, TOKEN)
        );
    }

    @Test
    void test_saveRestaurant_withFieldIdOwnerContainsARoleOtherThanInObjectRestaurantModel_ShouldThrowAccessDeniedException() {
        //Given
        RestaurantModel restaurantRequestModel = new RestaurantModel();
        restaurantRequestModel.setName("Sabroson17");
        restaurantRequestModel.setUrlLogo("http://sabroson.img");
        restaurantRequestModel.setAddress("Cra 10");
        restaurantRequestModel.setPhone("+573018452367");
        restaurantRequestModel.setNit(84373275L);
        restaurantRequestModel.setIdOwner(1L);

        UserDto userWithRoleOtherThanOwner = new UserDto();
        userWithRoleOtherThanOwner.setRol("ADMINISTRADOR");
        when(userGateway.getUserById(1L, TOKEN)).thenReturn(userWithRoleOtherThanOwner);
        // When
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> adminUseCase.saveRestaurant(restaurantRequestModel, TOKEN));
        //Then
        assertEquals("The user id does not have the required role to use this action", exception.getMessage());
    }
}