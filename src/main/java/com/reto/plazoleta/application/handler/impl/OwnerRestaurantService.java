package com.reto.plazoleta.application.handler.impl;

import com.reto.plazoleta.application.dto.request.CreateDishRequestDto;
import com.reto.plazoleta.application.dto.request.DishUpdateStatusRequestDto;
import com.reto.plazoleta.application.dto.response.DishStatusResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantEmployeeResponseDto;
import com.reto.plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.reto.plazoleta.application.dto.response.CreateDishResponseDto;
import com.reto.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.reto.plazoleta.application.dto.response.UpdateDishResponseDto;
import com.reto.plazoleta.application.handler.IOwnerRestaurantService;
import com.reto.plazoleta.application.mapper.requestmapper.IDishRequestMapper;
import com.reto.plazoleta.application.mapper.requestmapper.IEmployeeRestaurantRequestMapper;
import com.reto.plazoleta.application.mapper.responsemapper.IDishResponseMapper;
import com.reto.plazoleta.application.mapper.responsemapper.IEmployeeResponseMapper;
import com.reto.plazoleta.domain.api.IEmployeeRestaurantServicePort;
import com.reto.plazoleta.domain.api.IOwnerRestaurantServicePort;
import com.reto.plazoleta.domain.model.DishModel;
import com.reto.plazoleta.domain.model.EmployeeRestaurantModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class OwnerRestaurantService implements IOwnerRestaurantService {

    private final IOwnerRestaurantServicePort ownerRestaurantServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;
    private final IEmployeeRestaurantServicePort employeeRestaurantServicePort;
    private final IEmployeeRestaurantRequestMapper employeeRestaurantRequestMapper;
    private final IEmployeeResponseMapper employeeResponseMapper;

    @Override
    public CreateDishResponseDto saveDish(CreateDishRequestDto createDishRequestDto) {
        return dishResponseMapper.toDishResponse(ownerRestaurantServicePort.saveDish(dishRequestMapper.toDishModel(createDishRequestDto)));
    }

    @Override
    public UpdateDishResponseDto updateDish(UpdateDishRequestDto updateDishRequestDto) {
        return  dishResponseMapper.toDishUpdateResponse(ownerRestaurantServicePort
                .updateDish(dishRequestMapper
                        .toDishModel(updateDishRequestDto)));
    }

    @Override
    public RestaurantEmployeeResponseDto saveUserEmployeeInTheRestaurant(RestaurantEmployeeRequestDto restaurantEmployeeRequestDto, String tokenWithBearerPrefix) {
        final EmployeeRestaurantModel employeeRestaurantRequestModel = this.employeeRestaurantRequestMapper.toEmployeeRestaurantModel(restaurantEmployeeRequestDto);
        final EmployeeRestaurantModel employeeRestaurantSavedModel = this.employeeRestaurantServicePort.saveEmployeeRestaurant(
                                                    employeeRestaurantRequestModel, tokenWithBearerPrefix);
        return this.employeeResponseMapper.toRestaurantEmployeeResponseDto(employeeRestaurantSavedModel);
    }

    @Override
    public DishStatusResponseDto enableOrDisableDishByFieldStatus(DishUpdateStatusRequestDto updateDishStatusRequest, String tokenWithBearerPrefix) {
        final DishModel dishModelRequest = this.dishRequestMapper.toDishModelWithValueInFieldsIdRestaurantAndIsActive(updateDishStatusRequest);
        final DishModel dishModelUpdatedTheFieldIsActive = this.ownerRestaurantServicePort.enableOrDisableDishByFieldStatusAndIdRestaurantAndIdDish(dishModelRequest, tokenWithBearerPrefix);
        return this.dishResponseMapper.toDishStatusResponseDto(dishModelUpdatedTheFieldIsActive);
    }
}
