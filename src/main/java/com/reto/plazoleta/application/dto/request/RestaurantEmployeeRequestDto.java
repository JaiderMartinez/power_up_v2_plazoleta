package com.reto.plazoleta.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantEmployeeRequestDto {

    private Long idOwnerRestaurant;
    private Long idUserEmployee;
}
