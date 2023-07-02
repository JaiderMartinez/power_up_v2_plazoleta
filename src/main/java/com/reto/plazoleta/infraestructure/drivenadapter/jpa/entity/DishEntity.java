package com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "platos")
@Data
public class DishEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDish;
    @Column(name = "nombre")
    private String name;
    @Column(name = "descripcion")
    private String description;
    @Column(name = "precio")
    private Double price;
    @Column(name = "url_imagen")
    private String urlImageDish;
    @Column(name = "activo")
    private Boolean state;
    @Column(name = "tipo_plato")
    @Enumerated(EnumType.STRING)
    private TypeDish typeDish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurante", referencedColumnName = "idRestaurant")
    private RestaurantEntity restaurantEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", referencedColumnName = "idCategory")
    private CategoryEntity categoryEntity;

    public DishEntity(Long idDish, String name, String description, Double price, String urlImageDish,
                      Boolean state, RestaurantEntity restaurantEntity, CategoryEntity categoryEntity) {
        this.idDish = idDish;
        this.name = name;
        this.description = description;
        this.price = price;
        this.urlImageDish = urlImageDish;
        this.state = state;
        this.restaurantEntity = restaurantEntity;
        this.categoryEntity = categoryEntity;
    }
}