package com.reto.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishesFromOrderResponseDto {

    private Long idDish;
    private String name;
    private String dishImageUrl;
    private String description;
}
