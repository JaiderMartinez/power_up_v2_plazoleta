package com.reto.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishCreateRequestDto {

    private String name;
    private Double price;
    private String description;
    private String urlImageDish;
    private Long idCategory;
    private Long idRestaurant;
}
