package com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository;

import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.EmployeeRestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEmployeeRepository extends JpaRepository<EmployeeRestaurantEntity, Long> {

    EmployeeRestaurantEntity findByIdUserEmployee(Long idUserEmployee);
}
