package com.reto.plazoleta.infraestructure.drivenadapter.entity;

import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pedidos")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idOrder;
    @Column(name = "id_cliente")
    private Long idUserCustomer;
    @Column(name = "fecha")
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private StatusOrder status;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chef", referencedColumnName = "idRestaurantEmployee")
    private EmployeeRestaurantEntity employeeRestaurantEntity;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurante", referencedColumnName = "idRestaurant")
    private RestaurantEntity restaurantEntity;
}
