package com.reto.plazoleta.infraestructure.drivenadapter.repository;

import com.reto.plazoleta.infraestructure.drivenadapter.entity.EmployeeRestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEmployeeRepository extends JpaRepository<EmployeeRestaurantEntity, Long> {
}
