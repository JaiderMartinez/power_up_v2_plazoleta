package com.reto.plazoleta.domain.usecase;

import com.reto.plazoleta.domain.api.IEmployeeServicePort;
import com.reto.plazoleta.domain.exception.RestaurantNotExistException;
import com.reto.plazoleta.domain.exception.OrderInProcessException;
import com.reto.plazoleta.domain.exception.OrderNotExistsException;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import com.reto.plazoleta.domain.model.MessageSmsModel;
import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.IEmployeeRestaurantPersistencePort;
import com.reto.plazoleta.domain.spi.clients.IMessengerServiceProviderPort;
import com.reto.plazoleta.domain.spi.IOrderPersistencePort;
import com.reto.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.StatusOrder;
import com.reto.plazoleta.infraestructure.drivenadapter.webclients.dto.request.User;
import com.reto.plazoleta.domain.exception.NoDataFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

public class EmployeeRestaurantUseCase implements IEmployeeServicePort {

    private final IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserGateway userGateway;
    private final JwtProvider jwtProvider;
    private final IOrderPersistencePort orderPersistencePort;
    private final IMessengerServiceProviderPort messengerServiceProviderPort;

    public EmployeeRestaurantUseCase(IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort, IRestaurantPersistencePort restaurantPersistencePort,
                                     IUserGateway userGateway, JwtProvider jwtProvider, IOrderPersistencePort orderPersistencePort,
                                     IMessengerServiceProviderPort messengerServiceProviderPort) {
        this.employeeRestaurantPersistencePort = employeeRestaurantPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userGateway = userGateway;
        this.jwtProvider = jwtProvider;
        this.orderPersistencePort = orderPersistencePort;
        this.messengerServiceProviderPort = messengerServiceProviderPort;
    }

    @Override
    public EmployeeRestaurantModel saveEmployeeRestaurant(EmployeeRestaurantModel employeeRestaurantModel, String tokenWithBearerPrefix) {
        String emailFromUserOwnerOfARestaurant = jwtProvider.getAuthentication(tokenWithBearerPrefix.replace("Bearer ", "").trim()).getPrincipal().toString();
        User userOwnerFound = userGateway.getUserByEmailInTheToken(emailFromUserOwnerOfARestaurant, tokenWithBearerPrefix);
        final RestaurantModel restaurantFoundModelByIdRestaurant = this.restaurantPersistencePort.findByIdRestaurant(employeeRestaurantModel.getIdRestaurant());
        if(restaurantFoundModelByIdRestaurant == null || !restaurantFoundModelByIdRestaurant.getIdOwner().equals(userOwnerFound.getIdUser())) {
            throw new RestaurantNotExistException("Restaurant not Exist");
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
            throw new RestaurantNotExistException("Restaurant not Exist");
        return restaurantFromEmployeeFound;
    }

    @Override
    public List<OrderModel> assignEmployeeToOrderAndChangeStatusToInPreparation(List<Long> idOrders, String tokenWithPrefixBearer) {
        User userEmployeeFound = this.userGateway.getUserByEmailInTheToken(getEmailFromToken(tokenWithPrefixBearer), tokenWithPrefixBearer);
        EmployeeRestaurantModel employeeFromRestaurantFound = getRestaurantFromEmployeeByIdUserEmployeeAndValidateIfExistsTheRestaurant(userEmployeeFound.getIdUser());
        return getOrdersAssignedEmployeeAndUpdatedStatus(idOrders, employeeFromRestaurantFound);
    }

    private List<OrderModel> getOrdersAssignedEmployeeAndUpdatedStatus(List<Long> idOrders, EmployeeRestaurantModel employeeRestaurantToSave) {
        List<OrderModel> employeeAssignedToOrdersAndStatusUpdated = new ArrayList<>();
        for (Long idOrder : idOrders) {
            OrderModel orderFoundToAssignEmployee = this.orderPersistencePort.findByIdOrder(idOrder);
            validateOrderIfExistsAndIfOrderIsAlreadyInProcessAndIfEmployeeBelongsToRestaurantFromOrder(orderFoundToAssignEmployee, employeeRestaurantToSave);
            orderFoundToAssignEmployee.setEmployeeRestaurantModel(employeeRestaurantToSave);
            orderFoundToAssignEmployee.setStatus(StatusOrder.EN_PREPARACION);
            this.orderPersistencePort.saveOrder(orderFoundToAssignEmployee);
            employeeAssignedToOrdersAndStatusUpdated.add(orderFoundToAssignEmployee);
        }
        return employeeAssignedToOrdersAndStatusUpdated;
    }

    private void validateOrderIfExistsAndIfOrderIsAlreadyInProcessAndIfEmployeeBelongsToRestaurantFromOrder(OrderModel orderModelToValidate, EmployeeRestaurantModel employeeRestaurantToSave) {
        if (orderModelToValidate == null) {
            throw new OrderNotExistsException("The order no exist");
        } else if (orderModelToValidate.getEmployeeRestaurantModel() != null) {
            throw new OrderInProcessException("This order is in process");
        } else if (!employeeRestaurantToSave.getIdRestaurant().equals(orderModelToValidate.getRestaurantModel().getIdRestaurant())) {
            throw new OrderNotExistsException("The employee no belongs to this restaurant");
        }
    }

    @Override
    public OrderModel changeOrderStatusToReadyAndNotifyCustomer(Long idOrder, String tokenWithPrefixBearer) {
        OrderModel orderModelToUpdate = this.orderPersistencePort.findByIdOrder(idOrder);
        User userEmployeeAuthenticated = this.userGateway.getUserByEmailInTheToken(getEmailFromToken(tokenWithPrefixBearer), tokenWithPrefixBearer);

        EmployeeRestaurantModel employeeRestaurant = getRestaurantFromEmployeeByIdUserEmployeeAndValidateIfExistsTheRestaurant(userEmployeeAuthenticated.getIdUser());
        validateOrderAndIfIsInPreparationStatus(orderModelToUpdate, employeeRestaurant.getIdRestaurant());

        User userCustomerToNotifyOfYourOrder = this.userGateway.getUserById( orderModelToUpdate.getIdUserCustomer(), tokenWithPrefixBearer);
        Long pinGenerated = encryptOrderId(orderModelToUpdate.getIdOrder());
        MessageSmsModel messageSmsToSend = new MessageSmsModel(pinGenerated, orderModelToUpdate.getRestaurantModel().getName(),
                userCustomerToNotifyOfYourOrder.getName(), userCustomerToNotifyOfYourOrder.getCellPhone());

        this.messengerServiceProviderPort.notifyCustomerBySmsMessage(messageSmsToSend, tokenWithPrefixBearer);
        orderModelToUpdate.setStatus(StatusOrder.LISTO);
        return this.orderPersistencePort.saveOrder(orderModelToUpdate);
    }

    private void validateOrderAndIfIsInPreparationStatus(OrderModel orderModelToValidate, Long idRestaurantToWhichEmployeeBelongs) {
        if ( orderModelToValidate == null ) {
            throw new OrderNotExistsException("Order not exists");
        } else if ( !orderModelToValidate.getStatus().equals(StatusOrder.EN_PREPARACION) ) {
            throw new OrderInProcessException("The order is in process");
        } else if ( !orderModelToValidate.getRestaurantModel().getIdRestaurant().equals(idRestaurantToWhichEmployeeBelongs) ) {
            throw new OrderNotExistsException("Order not exists");
        }
    }

    private long encryptOrderId(Long idOrder) {
        String addFiveCharacters = String.format("%05d", idOrder);
        StringBuilder orderIdEncryption = new StringBuilder();
        for (int index = 0; index < addFiveCharacters.length(); index++) {
            char digitToEncrypt  = addFiveCharacters.charAt(index);
            int originalCharacterToEncrypt = Character.getNumericValue(digitToEncrypt);
            orderIdEncryption.append((originalCharacterToEncrypt + 3) % 10);
        }
        return Long.parseLong(orderIdEncryption.toString());
    }
}
