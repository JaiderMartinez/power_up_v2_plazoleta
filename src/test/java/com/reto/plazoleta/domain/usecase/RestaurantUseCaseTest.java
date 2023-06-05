package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.exception.EmptyFieldsException;
import com.reto.plazoleta.domain.exception.InvalidDataException;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;
import com.reto.plazoleta.infraestructure.exception.NoDataFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;
    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock
    private IUserGateway userGateway;
    private static final String TOKEN = "";

    @Test
    void test_SaveRestaurant_withNonEmptyRestaurantModelTheFieldsNameAndPhoneFormatCorrectAndIdOwnerExistAndEqualsOwnerRoleAndAndValidToken_ShouldReturnVoid() {
        //Given
        User userWithRoleOwner = new User();
        userWithRoleOwner.setRol("PROPIETARIO");

        RestaurantModel restaurantExpected = new RestaurantModel();
        restaurantExpected.setIdRestaurant(1L);
        restaurantExpected.setName("Sabroson17");
        restaurantExpected.setUrlLogo("http://sabroson.img");
        restaurantExpected.setAddress("Cra 10");
        restaurantExpected.setPhone("3018452367");
        restaurantExpected.setNit(843775L);
        restaurantExpected.setIdOwner(1L);

        when(userGateway.getUserById(1L, TOKEN)).thenReturn(userWithRoleOwner);
        when(restaurantPersistencePort.saveRestaurant(restaurantExpected)).thenReturn(restaurantExpected);
        //When
        RestaurantModel restaurantSaved = restaurantUseCase.saveRestaurant(restaurantExpected, TOKEN);
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
    void test_SaveRestaurant_withStringAsPhoneInvalidRestaurant_ShouldThrowInvalidDataException() {
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
                () -> restaurantUseCase.saveRestaurant(restaurantWithPhoneWrong, TOKEN)
        );
    }

    @Test
    void test_SaveRestaurant_withStringAsNameRestaurantWithOnlyNumbers_ShouldThrowInvalidDataException() {
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
                () -> restaurantUseCase.saveRestaurant(restaurantWhereNameOnlyContainsNumbers, TOKEN)
        );
    }

    @Test
    void test_SaveRestaurant_withAllFieldsAreEmptyInObjectRestaurantModel_ShouldThrowEmptyFieldsException() {
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
                () -> restaurantUseCase.saveRestaurant(restaurantWhereAllFieldsAreEmpty, TOKEN)
        );
    }

    @Test
    void test_SaveRestaurant_withFieldIdOwnerContainsARoleOtherThanInObjectRestaurantModel_ShouldThrowAccessDeniedException() {
        //Given
        RestaurantModel restaurantRequestModel = new RestaurantModel();
        restaurantRequestModel.setName("Sabroson17");
        restaurantRequestModel.setUrlLogo("http://sabroson.img");
        restaurantRequestModel.setAddress("Cra 10");
        restaurantRequestModel.setPhone("+573018452367");
        restaurantRequestModel.setNit(84373275L);
        restaurantRequestModel.setIdOwner(1L);

        User userWithRoleOtherThanOwner = new User();
        userWithRoleOtherThanOwner.setRol("ADMINISTRADOR");
        when(userGateway.getUserById(1L, TOKEN)).thenReturn(userWithRoleOtherThanOwner);
        // When
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> restaurantUseCase.saveRestaurant(restaurantRequestModel, TOKEN));
        //Then
        assertEquals("The user id does not have the required role to use this action", exception.getMessage());
    }

    @Test
    void test_findAllByOrderByNameAsc_withIntAsSizeItemsGreaterThanZero_ShouldReturnListRestaurantPageableWithAllFields() {
        //Given
        List<RestaurantModel> restaurantList = new ArrayList<>();
        restaurantList.add(new RestaurantModel(1L, "Restaurante 1", "Dirección 1", "3014896273", "http://restaurante1.com", 111111L, 1L));
        restaurantList.add(new RestaurantModel(2L, "Restaurante 2", "Dirección 2", "3224196283", "http://restaurante2.com", 222222L, 2L));
        Page<RestaurantModel> pageableRestaurantsExpected = new PageImpl<>(restaurantList);
        when(restaurantPersistencePort.findAllByOrderByNameAsc(PageRequest.of(0, 10))).thenReturn(pageableRestaurantsExpected);
        //When
        Page<RestaurantModel> result = restaurantUseCase.findAllByOrderByNameAsc(0, 10);
        //Then
        verify(restaurantPersistencePort, times(1)).findAllByOrderByNameAsc(PageRequest.of(0, 10));
        assertEquals(pageableRestaurantsExpected, result);
        assertEquals(pageableRestaurantsExpected.getTotalElements(), result.getTotalElements());
        assertEquals(pageableRestaurantsExpected.toList().get(0).getPhone(), result.toList().get(0).getPhone());
        assertEquals(pageableRestaurantsExpected.toList().get(1).getNit(), result.toList().get(1).getNit());
    }

    @Test
    void test_findAllByOrderByNameAsc_withIntAsSizeItemsGreaterThanZeroAndNoDataFound_ShouldThrowNoDataFoundException() {
        //Given
        int numberPage = 0;
        int sizeItems = 10;
        Page<RestaurantModel> emptyRestaurantPage = Page.empty();
        when(restaurantPersistencePort.findAllByOrderByNameAsc(PageRequest.of(numberPage, sizeItems))).thenReturn(emptyRestaurantPage);
        // When & Then
        Assertions.assertThrows(
                NoDataFoundException.class,
                () -> restaurantUseCase.findAllByOrderByNameAsc(numberPage, sizeItems)
        );
    }
}