package com.reto.plazoleta.infraestructure.drivenadapter.persistence;

import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import com.reto.plazoleta.domain.spi.IEmployeeRestaurantPersistencePort;
import com.reto.plazoleta.infraestructure.drivenadapter.mapper.IEmployeeEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeRestaurantJpaAdapter implements IEmployeeRestaurantPersistencePort {

    private final IEmployeeRepository employeeRepository;
    private final IEmployeeEntityMapper employeeEntityMapper;

    @Override
    public EmployeeRestaurantModel saveEmployeeRestaurant(EmployeeRestaurantModel employeeRestaurantModel) {
        return employeeEntityMapper.toEmployeeRestaurantModel(employeeRepository.save(
                employeeEntityMapper.toEmployeeRestaurantEntity(employeeRestaurantModel)));
    }

    @Override
    public EmployeeRestaurantModel findByIdUserEmployee(Long idUserEmployee) {
        return this.employeeEntityMapper.toEmployeeRestaurantModel(this.employeeRepository.findByIdUserEmployee(idUserEmployee));
    }
}
