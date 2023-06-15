package com.reto.plazoleta.application.handler.impl;

import com.reto.plazoleta.application.dto.request.RequestToCreateRestaurantDto;
import com.reto.plazoleta.application.dto.response.RestaurantCreatedResponseDto;
import com.reto.plazoleta.application.handler.IAdminService;
import com.reto.plazoleta.application.mapper.requestmapper.IAdminRequestMapper;
import com.reto.plazoleta.application.mapper.responsemapper.IAdminResponseMapper;
import com.reto.plazoleta.domain.api.IAdminServicePort;
import com.reto.plazoleta.domain.model.RestaurantModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AdminService implements IAdminService {

    private final IAdminServicePort restaurantServicePort;
    private final IAdminRequestMapper restaurantRequestMapper;
    private final IAdminResponseMapper restaurantResponseMapper;

    @Override
    public RestaurantCreatedResponseDto saveRestaurant(RequestToCreateRestaurantDto requestToCreateRestaurantDto, String tokenWithBearerPrefix) {
        RestaurantModel restaurantRequestModel = restaurantRequestMapper.requestToCreateRestaurantDtoToRestaurantModel(requestToCreateRestaurantDto);
        RestaurantModel restaurantCreatedModel = restaurantServicePort.saveRestaurant(restaurantRequestModel, tokenWithBearerPrefix);
        return restaurantResponseMapper.restaurantModelToRestaurantCreatedResponseDto(restaurantCreatedModel);
    }
}
