package com.reto.plazoleta.infraestructure.drivenadapter.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor // para que funcione las validaciones
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "EmployeeRestaurant")
public class EmployeeRestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmployeeRestaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Restaurant", referencedColumnName = "idRestaurant")
    private RestaurantEntity restaurantEntity;

    private Long idUser;


}