package com.reto.plazoleta.domain.api;

import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import com.reto.plazoleta.domain.model.OrderModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IEmployeeRestaurantServicePort {

    EmployeeRestaurantModel saveEmployeeRestaurant(EmployeeRestaurantModel employeeRestaurantModel, String tokenWithBearerPrefix);

    Page<OrderModel> getAllOrdersFilterByStatusAndSizeItemsByPage(Integer sizeItems, Integer pageNumber, String status, String tokenWithPrefixBearer);

    List<OrderModel> assignEmployeeToOrderAndChangeStatusToInPreparation(List<Long> idOrders, String tokenWithPrefixBearer);
}
