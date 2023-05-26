package com.reto.plazoleta.application.handler.impl;

import com.reto.plazoleta.application.dto.request.CreateDishRequestDto;
import com.reto.plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.reto.plazoleta.application.dto.response.CreateDishResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantEmployeeResponseDto;
import com.reto.plazoleta.application.handler.IOwnerRestaurantService;
import com.reto.plazoleta.application.mapper.requestmapper.IDishRequestMapper;
import com.reto.plazoleta.application.mapper.requestmapper.IEmployeeRestaurantRequestMapper;
import com.reto.plazoleta.application.mapper.responsemapper.IDishResponseMapper;
import com.reto.plazoleta.application.mapper.responsemapper.IEmployeeResponseMapper;
import com.reto.plazoleta.domain.api.IEmployeeRestaurantServicePort;
import com.reto.plazoleta.domain.api.IOwnerRestaurantServicePort;
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
    public RestaurantEmployeeResponseDto saveUserEmployeeInTheRestaurant(RestaurantEmployeeRequestDto restaurantEmployeeRequestDto) {
        final EmployeeRestaurantModel employeeRestaurantRequestModel = this.employeeRestaurantRequestMapper.toEmployeeRestaurantModel(restaurantEmployeeRequestDto);
        final EmployeeRestaurantModel employeeRestaurantSavedModel = this.employeeRestaurantServicePort.saveEmployeeRestaurant(
                                                    employeeRestaurantRequestModel, restaurantEmployeeRequestDto.getIdOwnerRestaurant());
        return this.employeeResponseMapper.toRestaurantEmployeeResponseDto(employeeRestaurantSavedModel);
    }
}
