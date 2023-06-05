package com.reto.plazoleta.application.handler.impl;

import com.reto.plazoleta.application.dto.request.RequestToCreateRestaurantDto;
import com.reto.plazoleta.application.dto.response.RestaurantCreatedResponseDto;
import com.reto.plazoleta.application.handler.IAdminService;
import com.reto.plazoleta.application.mapper.requestmapper.IRestaurantRequestMapper;
import com.reto.plazoleta.application.mapper.responsemapper.IRestaurantResponseMapper;
import com.reto.plazoleta.domain.api.IRestaurantServicePort;
import com.reto.plazoleta.domain.model.RestaurantModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AdminService implements IAdminService {

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;

    @Override
    public RestaurantCreatedResponseDto saveRestaurant(RequestToCreateRestaurantDto requestToCreateRestaurantDto, String tokenWithBearerPrefix) {
        RestaurantModel restaurantRequestModel = restaurantRequestMapper.toRestaurantModel(requestToCreateRestaurantDto);
        RestaurantModel restaurantCreatedModel = restaurantServicePort.saveRestaurant(restaurantRequestModel, tokenWithBearerPrefix);
        return restaurantResponseMapper.toRestaurantCreatedResponseDto(restaurantCreatedModel);
    }
}
