package com.reto.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DishesResponseDto {

    private Long idDish;
    private String name;
    private String description;
    private Double price;
    private String urlImage;
}
