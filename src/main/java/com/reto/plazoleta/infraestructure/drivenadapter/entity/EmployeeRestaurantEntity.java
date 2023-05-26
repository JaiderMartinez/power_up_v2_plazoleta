package com.reto.plazoleta.infraestructure.drivenadapter.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "EmployeeRestaurant")
public class EmployeeRestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRestaurantEmployee;
    private Long idUserEmployee;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurant", referencedColumnName = "idRestaurant")
    private RestaurantEntity restaurantEntity;
}
