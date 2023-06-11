package com.reto.plazoleta.application.handler;

import com.reto.plazoleta.application.dto.request.CreateDishRequestDto;
import com.reto.plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.reto.plazoleta.application.dto.request.DishUpdateStatusRequestDto;
import com.reto.plazoleta.application.dto.response.CreateDishResponseDto;
import com.reto.plazoleta.application.dto.response.DishStatusResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantEmployeeResponseDto;
import com.reto.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.reto.plazoleta.application.dto.response.UpdateDishResponseDto;

public interface IOwnerRestaurantService {

    CreateDishResponseDto saveDish(CreateDishRequestDto createDishRequestDto);
  
    UpdateDishResponseDto updateDish(UpdateDishRequestDto updateDishRequestDto);
  
    RestaurantEmployeeResponseDto saveUserEmployeeInTheRestaurant(RestaurantEmployeeRequestDto restaurantEmployeeRequestDto, String tokenWithBearerPrefix);

    DishStatusResponseDto enableOrDisableDishByFieldStatus(DishUpdateStatusRequestDto updateDishStatusRequest, String tokenWithBearerPrefix);
}
