package com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper;

import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.EmployeeRestaurantEntity;
import org.mapstruct.Mapper;

@Mapper
public interface IEmployeeEntityMapper {

    EmployeeRestaurantEntity toEmployeeRestaurantEntity(EmployeeRestaurantModel employeeRestaurantModel);

    EmployeeRestaurantModel toEmployeeRestaurantModel(EmployeeRestaurantEntity employeeRestaurantEntity);
}
