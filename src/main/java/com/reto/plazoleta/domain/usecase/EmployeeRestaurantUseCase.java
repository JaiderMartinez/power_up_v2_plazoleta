package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.api.IEmployeeRestaurantServicePort;
import com.reto.plazoleta.domain.exception.ObjectNotFoundException;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.IEmployeeRestaurantPersistencePort;
import com.reto.plazoleta.domain.spi.IOrderPersistencePort;
import com.reto.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.StatusOrder;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.User;
import com.reto.plazoleta.infraestructure.exception.NoDataFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class EmployeeRestaurantUseCase implements IEmployeeRestaurantServicePort {

    private final IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserGateway userGateway;
    private final JwtProvider jwtProvider;
    private final IOrderPersistencePort orderPersistencePort;

    public EmployeeRestaurantUseCase(IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort, IRestaurantPersistencePort restaurantPersistencePort,
                                     IUserGateway userGateway, JwtProvider jwtProvider, IOrderPersistencePort orderPersistencePort) {
        this.employeeRestaurantPersistencePort = employeeRestaurantPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userGateway = userGateway;
        this.jwtProvider = jwtProvider;
        this.orderPersistencePort = orderPersistencePort;
    }

    @Override
    public EmployeeRestaurantModel saveEmployeeRestaurant(EmployeeRestaurantModel employeeRestaurantModel, String tokenWithBearerPrefix) {
        String emailFromUserOwnerOfARestaurant = jwtProvider.getAuthentication(tokenWithBearerPrefix.replace("Bearer ", "").trim()).getPrincipal().toString();
        User userOwnerFound = userGateway.getUserByEmailInTheToken(emailFromUserOwnerOfARestaurant, tokenWithBearerPrefix);
        final RestaurantModel restaurantFoundModelByIdRestaurant = this.restaurantPersistencePort.findByIdRestaurant(employeeRestaurantModel.getIdRestaurant());
        if(restaurantFoundModelByIdRestaurant == null || !restaurantFoundModelByIdRestaurant.getIdOwner().equals(userOwnerFound.getIdUser())) {
            throw new ObjectNotFoundException("Restaurant not Exist");
        }
        employeeRestaurantModel.setIdRestaurant(restaurantFoundModelByIdRestaurant.getIdRestaurant());
        return this.employeeRestaurantPersistencePort.saveEmployeeRestaurant(employeeRestaurantModel);
    }

    @Override
    public Page<OrderModel> getAllOrdersFilterByStatusAndSizeItemsByPage(Integer sizeItems, Integer pageNumber, String status, String tokenWithPrefixBearer) {
        User userEmployeeFound = this.userGateway.getUserByEmailInTheToken(getEmailFromToken(tokenWithPrefixBearer), tokenWithPrefixBearer);
        EmployeeRestaurantModel employeeFromRestaurantFound = getRestaurantFromEmployeeByIdUserEmployeeAndValidateIfExistsTheRestaurant(userEmployeeFound.getIdUser());
        StatusOrder statusOrder = StatusOrder.valueOf(status.toUpperCase());
        Page<OrderModel> ordersPaginatedByFieldStatus = this.orderPersistencePort
                    .findAllByRestaurantEntityIdRestaurantAndStatusOrder(PageRequest.of(pageNumber, sizeItems), employeeFromRestaurantFound.getIdRestaurant(), statusOrder);
        if (ordersPaginatedByFieldStatus.isEmpty())
            throw new NoDataFoundException();
        return ordersPaginatedByFieldStatus;
    }

    private String getEmailFromToken(String tokenWithPrefixBearer) {
        String tokenWithoutPrefix = tokenWithPrefixBearer.replace("Bearer ", "").trim();
        return this.jwtProvider.getAuthentication(tokenWithoutPrefix).getName();
    }

    private EmployeeRestaurantModel getRestaurantFromEmployeeByIdUserEmployeeAndValidateIfExistsTheRestaurant(Long idUserEmployee) {
        EmployeeRestaurantModel restaurantFromEmployeeFound = this.employeeRestaurantPersistencePort.findByIdUserEmployee(idUserEmployee);
        RestaurantModel restaurantModel = this.restaurantPersistencePort.findByIdRestaurant(restaurantFromEmployeeFound.getIdRestaurant());
        if (restaurantModel == null)
            throw new ObjectNotFoundException("Restaurant not Exist");
        return restaurantFromEmployeeFound;
    }
}
