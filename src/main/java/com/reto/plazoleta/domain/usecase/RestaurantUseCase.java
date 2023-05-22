package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.api.IRestaurantServicePort;
import com.reto.plazoleta.domain.exception.EmptyFieldsException;
import com.reto.plazoleta.domain.exception.InvalidDataException;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;
import com.reto.plazoleta.infraestructure.exception.NoDataFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserGateway userGateway;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort, IUserGateway userGateway) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userGateway = userGateway;
    }

    @Override
    public void saveRestaurant(RestaurantModel restaurantModel, String tokenWithBearerPrefix) {
        if(isContainsRestaurantNameOnlyNumbers(restaurantModel.getName())) {
            throw new InvalidDataException("The name of the restaurant cannot only contain numbers");
        }
        validateRestaurantFieldsEmpty(restaurantModel);
        validateRestaurantPhone(restaurantModel.getPhone());
        User user = userGateway.getUserById(restaurantModel.getIdOwner(), tokenWithBearerPrefix);
        if(!user.getRol().equals("PROPIETARIO")) {
            throw new AccessDeniedException("The user id does not have the required role to use this action");
        }
        restaurantPersistencePort.saveRestaurant(restaurantModel);
    }

    private void validateRestaurantFieldsEmpty(RestaurantModel restaurantModel) {
        if( restaurantModel.getName().replace(" ", "").isEmpty() ||
                restaurantModel.getUrlLogo().replace(" ", "").isEmpty() ||
                restaurantModel.getAddress().replace(" ", "").isEmpty() ||
                restaurantModel.getPhone().replace(" ", "").isEmpty() ||
                restaurantModel.getNit() == null || restaurantModel.getIdOwner() == null ) {
            throw new EmptyFieldsException("The field must not be empty");
        }
    }


    private void validateRestaurantPhone(String phoneRestaurant) {
        String phoneRestaurantNoSpaces = phoneRestaurant.replace(" ", "");
        if(phoneRestaurantNoSpaces.startsWith("+")){
            if(!phoneRestaurantNoSpaces.matches("\\+\\d+") ||
                    phoneRestaurantNoSpaces.length() != 13 ){
                throw new InvalidDataException("The cell phone format is wrong");
            }
        } else {
            if (phoneRestaurantNoSpaces.length() != 10 || !phoneRestaurantNoSpaces.matches("\\d+")) {
                throw new InvalidDataException("The cell phone format is wrong");
            }
        }
    }

    private boolean isContainsRestaurantNameOnlyNumbers(String nameRestaurant) {
        return nameRestaurant.matches("\\d+");
    }

    @Override
    public Page<RestaurantModel> findAllByOrderByNameAsc(int numberPage, int sizeItems) {
        Page<RestaurantModel> resultRestaurantsPageable = restaurantPersistencePort.findAllByOrderByNameAsc(
                                                                PageRequest.of(numberPage, sizeItems));
        if (resultRestaurantsPageable.isEmpty()) throw new NoDataFoundException();
        return resultRestaurantsPageable;
    }

}
