package com.reto.plazoleta.domain.spi;

import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;

public interface IEmployeeRestaurantPersistencePort {

    EmployeeRestaurantModel saveEmployeeRestaurant(EmployeeRestaurantModel employeeRestaurantModel);

    EmployeeRestaurantModel findByIdUserEmployee(Long idUserEmployee);
}
