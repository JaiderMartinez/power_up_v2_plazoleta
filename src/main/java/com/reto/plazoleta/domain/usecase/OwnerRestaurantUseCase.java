package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.api.IOwnerRestaurantServicePort;
import com.reto.plazoleta.domain.exception.DishNotExistsException;
import com.reto.plazoleta.domain.exception.InvalidDataException;
import com.reto.plazoleta.domain.exception.RestaurantNotExistException;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.model.CategoryModel;
import com.reto.plazoleta.domain.model.DishModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.ICategoryPersistencePort;
import com.reto.plazoleta.domain.spi.IDishPersistencePort;
import com.reto.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;

public class OwnerRestaurantUseCase implements IOwnerRestaurantServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;
    private final IUserGateway userGateway;
    private final JwtProvider jwtProvider;
    private static final String MESSAGE_RESTAURANT_NOT_EXIST = "The restaurant does not exist";

    public OwnerRestaurantUseCase(IDishPersistencePort persistencePort, IRestaurantPersistencePort restaurantPersistencePort,
                                  ICategoryPersistencePort categoryPersistencePort, IUserGateway userGateway, JwtProvider jwtProvider) {
        this.dishPersistencePort = persistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.categoryPersistencePort = categoryPersistencePort;
        this.userGateway = userGateway;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public DishModel saveDish(DishModel dishModelRequest) {
        if(dishModelRequest.getPrice() <= 0)
            throw new InvalidDataException("Price must be greater than zero");
        RestaurantModel restaurantModel = this.restaurantPersistencePort.findByIdRestaurant(dishModelRequest.getRestaurantModel().getIdRestaurant());
        if(restaurantModel == null)
            throw new RestaurantNotExistException(MESSAGE_RESTAURANT_NOT_EXIST);
        CategoryModel categoryModel = this.categoryPersistencePort.findById(dishModelRequest.getCategoryModel().getIdCategory());
        if(categoryModel == null)
            throw new InvalidDataException("The category does not exist");
        dishModelRequest.setRestaurantModel(restaurantModel);
        dishModelRequest.setCategoryModel(categoryModel);
        dishModelRequest.setState(true);
        return this.dishPersistencePort.saveDish(dishModelRequest);
    }

    @Override
    public DishModel updateDish(DishModel dishModelRequestToUpdate) {
        DishModel updateDishModel = dishPersistencePort.findById(dishModelRequestToUpdate.getIdDish());
        if (updateDishModel == null)
            throw new DishNotExistsException("The dish not exist");
        RestaurantModel restaurantModel = restaurantPersistencePort.findByIdRestaurant(dishModelRequestToUpdate.getRestaurantModel().getIdRestaurant());
        if (restaurantModel == null) {
            throw new RestaurantNotExistException(MESSAGE_RESTAURANT_NOT_EXIST);
        }else if (!updateDishModel.getRestaurantModel().getIdRestaurant().equals(restaurantModel.getIdRestaurant())) {
            throw new InvalidDataException("Only the owner of the restaurant can update the dish");
        }
        updateDishModel.setPrice(dishModelRequestToUpdate.getPrice());
        updateDishModel.setDescription(dishModelRequestToUpdate.getDescription());
        return this.dishPersistencePort.saveDish(updateDishModel);
    }

    @Override
    public DishModel enableOrDisableDishByFieldStatusAndIdRestaurantAndIdDish(Long idDish, Long idRestaurant, boolean active, String tokenWithPrefixBearer) {
        User userOwnerAuthenticated = getUserOwnerAuthenticated(tokenWithPrefixBearer);
        validateRestaurantExistenceAndIfBelongsToUser(idRestaurant, userOwnerAuthenticated.getIdUser());
        return validateDishExistenceAndIfBelongsDishToRestaurantShouldReturnTheDishSavedAndUpdate(idDish, idRestaurant, active);
    }

    private User getUserOwnerAuthenticated(String tokenWithPrefixBearer) {
        String emailFromUserAuthenticatedByToken = this.jwtProvider.getAuthentication(tokenWithPrefixBearer.replace("Bearer ", "").trim()).getName();
        return this.userGateway.getUserByEmailInTheToken(emailFromUserAuthenticatedByToken, tokenWithPrefixBearer);
    }

    private void validateRestaurantExistenceAndIfBelongsToUser(Long idRestaurant, Long idUserOwnerAuthenticated) {
        RestaurantModel restaurantFound = this.restaurantPersistencePort.findByIdRestaurant(idRestaurant);
        if (restaurantFound == null) {
            throw new RestaurantNotExistException(MESSAGE_RESTAURANT_NOT_EXIST);
        } else if (!restaurantFound.getIdOwner().equals(idUserOwnerAuthenticated)) {
            throw new InvalidDataException("The user is not the owner of this restaurant");
        }
    }

    private DishModel validateDishExistenceAndIfBelongsDishToRestaurantShouldReturnTheDishSavedAndUpdate(Long idDish, Long idRestaurant, boolean active) {
        DishModel dishFoundAndUpdateStatus = this.dishPersistencePort.findById(idDish);
        if (dishFoundAndUpdateStatus == null) {
            throw new DishNotExistsException("The dish does not exist");
        } else if (!dishFoundAndUpdateStatus.getRestaurantModel().getIdRestaurant().equals(idRestaurant)) {
            throw new InvalidDataException("The value of field idRestaurant does not match the dish's idRestaurant");
        }
        dishFoundAndUpdateStatus.setState(active);
        return this.dishPersistencePort.saveDish(dishFoundAndUpdateStatus);
    }
}
