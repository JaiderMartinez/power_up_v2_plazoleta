package com.reto.plazoleta.application.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantResponsePageableDto {

    private Long idRestaurant;
    private String name;
    private String urlLogo;
}
