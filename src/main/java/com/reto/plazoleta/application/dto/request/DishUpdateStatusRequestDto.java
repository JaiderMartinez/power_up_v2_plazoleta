package com.reto.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishUpdateStatusRequestDto {

    private Long idDish;
    private Long idRestaurant;
    private boolean active;
}
