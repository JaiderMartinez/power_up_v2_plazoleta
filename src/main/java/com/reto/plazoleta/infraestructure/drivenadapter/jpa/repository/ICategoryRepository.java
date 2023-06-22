package com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository;


import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long> {

}
