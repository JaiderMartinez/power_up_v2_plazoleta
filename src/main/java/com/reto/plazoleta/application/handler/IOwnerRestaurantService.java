package com.reto.plazoleta.application.handler;

import com.reto.plazoleta.application.dto.request.DishCreateRequestDto;
import com.reto.plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.reto.plazoleta.application.dto.response.DishCreatedResponseDto;
import com.reto.plazoleta.application.dto.response.DishStatusResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantEmployeeResponseDto;
import com.reto.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.reto.plazoleta.application.dto.response.UpdateDishResponseDto;

public interface IOwnerRestaurantService {

    DishCreatedResponseDto saveDish(DishCreateRequestDto createDishRequestDto);
  
    UpdateDishResponseDto updateDish(UpdateDishRequestDto updateDishRequestDto);
  
    RestaurantEmployeeResponseDto saveUserEmployeeInTheRestaurant(RestaurantEmployeeRequestDto restaurantEmployeeRequestDto, String tokenWithBearerPrefix);

    DishStatusResponseDto enableOrDisableDishByFieldStatus(Long idDish, Long idRestaurant, boolean active, String tokenWithBearerPrefix);
}
