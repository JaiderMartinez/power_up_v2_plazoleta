package com.reto.plazoleta.domain.api;

import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;

public interface IEmployeeRestaurantServicePort {

    EmployeeRestaurantModel saveEmployeeRestaurant(EmployeeRestaurantModel employeeRestaurantModel, String tokenWithBearerPrefix);
}
