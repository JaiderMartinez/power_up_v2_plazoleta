package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.api.IOwnerRestaurantServicePort;
import com.reto.plazoleta.domain.exception.DishNotExistsException;
import com.reto.plazoleta.domain.exception.InvalidDataException;
import com.reto.plazoleta.domain.exception.ObjectNotFoundException;
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

    public OwnerRestaurantUseCase(IDishPersistencePort persistencePort, IRestaurantPersistencePort restaurantPersistencePort,
                                  ICategoryPersistencePort categoryPersistencePort, IUserGateway userGateway, JwtProvider jwtProvider) {
        this.dishPersistencePort = persistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.categoryPersistencePort = categoryPersistencePort;
        this.userGateway = userGateway;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public DishModel saveDish(DishModel dishModel) {
        if(dishModel.getPrice() <= 0) {
            throw new InvalidDataException("Price must be greater than zero");
        }
        RestaurantModel restaurantModel = restaurantPersistencePort.findByIdRestaurant(dishModel.getRestaurantModel().getIdRestaurant());
        if(restaurantModel == null) {
            throw new InvalidDataException("The restaurant does not exist");
        }
        CategoryModel categoryModel = categoryPersistencePort.findById(dishModel.getCategoryModel().getIdCategory());
        if(categoryModel == null) {
            throw new InvalidDataException("The category does not exist");
        }
        dishModel.setRestaurantModel(restaurantModel);
        dishModel.setCategoryModel(categoryModel);
        dishModel.setStateDish(true);
        return dishPersistencePort.saveDish(dishModel);
    }

    @Override
    public DishModel updateDish(DishModel dishModel) {
        DishModel updateDishModel = dishPersistencePort.findById(dishModel.getIdDish());
        if (updateDishModel == null) {
            throw new DishNotExistsException("The dish not exist");
        }
        RestaurantModel restaurantModel = restaurantPersistencePort.findByIdRestaurant(dishModel.getRestaurantModel().getIdRestaurant());
        if (restaurantModel == null) {
            throw new ObjectNotFoundException("The restaurant does not exist");
        }
        if (!updateDishModel.getRestaurantModel().getIdRestaurant().equals(restaurantModel.getIdRestaurant())) {
            throw new InvalidDataException("Only the owner of the restaurant can update the dish");
        }

        updateDishModel.setPrice(dishModel.getPrice());
        updateDishModel.setDescriptionDish(dishModel.getDescriptionDish());

        return dishPersistencePort.updateDish(updateDishModel);
    }

    @Override
    public DishModel enableOrDisableDishByFieldStatusAndIdRestaurantAndIdDish(DishModel dishModelRequest, String tokenWithPrefixBearer) {
        User userOwnerAuthenticated = getUserOwnerAuthenticated(tokenWithPrefixBearer);
        validateRestaurantExistenceAndIfBelongsToUser(dishModelRequest.getRestaurantModel().getIdRestaurant(), userOwnerAuthenticated.getIdUser());
        return validateDishExistenceAndIfBelongsDishToRestaurantShouldReturnTheDishUpdate(dishModelRequest);
    }

    private User getUserOwnerAuthenticated(String tokenWithPrefixBearer) {
        String emailFromUserAuthenticatedByToken = this.jwtProvider.getAuthentication(tokenWithPrefixBearer.replace("Bearer ", "").trim()).getName();
        return this.userGateway.getUserByEmailInTheToken(emailFromUserAuthenticatedByToken, tokenWithPrefixBearer);
    }

    private void validateRestaurantExistenceAndIfBelongsToUser(Long idRestaurant, Long idUserOwnerAuthenticated) {
        RestaurantModel restaurantFound = this.restaurantPersistencePort.findByIdRestaurant(idRestaurant);
        if (restaurantFound == null) {
            throw new ObjectNotFoundException("The restaurant does not exist");
        } else if (!restaurantFound.getIdOwner().equals(idUserOwnerAuthenticated)) {
            throw new InvalidDataException("The user is not the owner of this restaurant");
        }
    }

    private DishModel validateDishExistenceAndIfBelongsDishToRestaurantShouldReturnTheDishUpdate(DishModel dishModel) {
        DishModel dishFoundAndUpdateStatus = this.dishPersistencePort.findById(dishModel.getIdDish());
        if (dishFoundAndUpdateStatus == null) {
            throw new DishNotExistsException("The dish does not exist");
        } else if (!dishFoundAndUpdateStatus.getRestaurantModel().getIdRestaurant().equals(dishModel.getRestaurantModel().getIdRestaurant())) {
            throw new InvalidDataException("The value of field idRestaurant does not match the dish's idRestaurant");
        }
        dishFoundAndUpdateStatus.setStateDish(dishModel.getStateDish());
        return dishFoundAndUpdateStatus;
    }
}
