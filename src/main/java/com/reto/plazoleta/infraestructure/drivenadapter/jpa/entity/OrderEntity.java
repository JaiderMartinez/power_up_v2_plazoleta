package com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
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

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDishEntity> ordersDishesEntity;
}
