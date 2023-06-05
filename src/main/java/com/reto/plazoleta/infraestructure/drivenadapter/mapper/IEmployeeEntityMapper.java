package com.reto.plazoleta.infraestructure.drivenadapter.mapper;

import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.EmployeeRestaurantEntity;
import org.mapstruct.Mapper;

@Mapper
public interface IEmployeeEntityMapper {

    EmployeeRestaurantEntity toEmployeeRestaurantEntity(EmployeeRestaurantModel employeeRestaurantModel);

    EmployeeRestaurantModel toEmployeeRestaurantModel(EmployeeRestaurantEntity employeeRestaurantEntity);
}
