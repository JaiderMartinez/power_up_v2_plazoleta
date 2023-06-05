package com.reto.plazoleta.application.handler;

import com.reto.plazoleta.application.dto.request.RequestToCreateRestaurantDto;
import com.reto.plazoleta.application.dto.response.RestaurantCreatedResponseDto;

public interface IAdminService {

    RestaurantCreatedResponseDto saveRestaurant(RequestToCreateRestaurantDto requestToCreateRestaurantDto, String tokenWithBearerPrefix);
}
