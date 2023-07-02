package com.reto.plazoleta.application.handler.impl;

import com.reto.plazoleta.application.dto.request.DishCreateRequestDto;
import com.reto.plazoleta.application.dto.response.DishStatusResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantEmployeeResponseDto;
import com.reto.plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.reto.plazoleta.application.dto.response.DishCreatedResponseDto;
import com.reto.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.reto.plazoleta.application.dto.response.UpdateDishResponseDto;
import com.reto.plazoleta.application.handler.IOwnerRestaurantService;
import com.reto.plazoleta.application.mapper.requestmapper.IOwnerRequestMapper;
import com.reto.plazoleta.application.mapper.requestmapper.IEmployeeRequestMapper;
import com.reto.plazoleta.application.mapper.responsemapper.IOwnerResponseMapper;
import com.reto.plazoleta.application.mapper.responsemapper.IEmployeeResponseMapper;
import com.reto.plazoleta.domain.api.IEmployeeServicePort;
import com.reto.plazoleta.domain.api.IOwnerRestaurantServicePort;
import com.reto.plazoleta.domain.model.dishes.DishModel;
import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class OwnerRestaurantService implements IOwnerRestaurantService {

    private final IOwnerRestaurantServicePort ownerRestaurantServicePort;
    private final IOwnerRequestMapper ownerRequestMapper;
    private final IOwnerResponseMapper ownerResponseMapper;
    private final IEmployeeServicePort employeeRestaurantServicePort;
    private final IEmployeeRequestMapper employeeRequestMapper;
    private final IEmployeeResponseMapper employeeResponseMapper;

    @Override
    public DishCreatedResponseDto saveDish(DishCreateRequestDto createDishRequestDto) {
        return this.ownerResponseMapper.dishModelToDishResponse(this.ownerRestaurantServicePort
                .saveDish(this.ownerRequestMapper
                        .updateDishRequestDtoToDishModel(createDishRequestDto)));
    }

    @Override
    public UpdateDishResponseDto updateDish(UpdateDishRequestDto updateDishRequestDto) {
        return  this.ownerResponseMapper.dishModelToDishUpdateResponse(this.ownerRestaurantServicePort
                .updateDish(this.ownerRequestMapper
                        .updateDishRequestDtoToDishModel(updateDishRequestDto)));
    }

    @Override
    public RestaurantEmployeeResponseDto saveUserEmployeeInTheRestaurant(RestaurantEmployeeRequestDto restaurantEmployeeRequestDto, String tokenWithBearerPrefix) {
        final EmployeeRestaurantModel employeeRestaurantRequestModel = this.employeeRequestMapper.restaurantEmployeeRequestDtoToEmployeeRestaurantModel(restaurantEmployeeRequestDto);
        final EmployeeRestaurantModel employeeRestaurantSavedModel = this.employeeRestaurantServicePort.saveEmployeeRestaurant(employeeRestaurantRequestModel, tokenWithBearerPrefix);
        return this.employeeResponseMapper.toRestaurantEmployeeResponseDto(employeeRestaurantSavedModel);
    }

    @Override
    public DishStatusResponseDto enableOrDisableDishByFieldStatus(Long idDish, Long idRestaurant, boolean active, String tokenWithBearerPrefix) {
        final DishModel dishModelUpdatedTheFieldIsActive = this.ownerRestaurantServicePort.enableOrDisableDishByFieldStatusAndIdRestaurantAndIdDish(idDish, idRestaurant, active, tokenWithBearerPrefix);
        return this.ownerResponseMapper.dishModelToDishStatusResponseDto(dishModelUpdatedTheFieldIsActive);
    }
}
