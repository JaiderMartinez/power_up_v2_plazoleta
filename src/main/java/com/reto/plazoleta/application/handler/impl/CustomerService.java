package com.reto.plazoleta.application.handler.impl;

import com.reto.plazoleta.application.dto.request.OrderRequestDto;
import com.reto.plazoleta.application.dto.response.OrderCreatedResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantResponsePageableDto;
import com.reto.plazoleta.application.handler.ICustomerService;
import com.reto.plazoleta.application.mapper.requestmapper.ICustomerRequestMapper;
import com.reto.plazoleta.application.mapper.requestmapper.IRestaurantRequestMapper;
import com.reto.plazoleta.application.mapper.responsemapper.ICustomerResponseMapper;
import com.reto.plazoleta.domain.api.ICustomerServicePort;
import com.reto.plazoleta.domain.api.IRestaurantServicePort;
import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.domain.model.OrderModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CustomerService implements ICustomerService {

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final ICustomerServicePort customerServicePort;
    private final ICustomerRequestMapper customerRequestMapper;
    private final ICustomerResponseMapper customerResponseMapper;

    @Override
    public Page<RestaurantResponsePageableDto> getAllRestaurantsByOrderByNameAsc(int numberPage, int sizeItems) {
        return restaurantServicePort.findAllByOrderByNameAsc(numberPage, sizeItems).map(restaurantRequestMapper::toRestaurantResponse);
    }

    @Override
    public OrderCreatedResponseDto saveOrder(OrderRequestDto createOrderRequestDto, String tokenWithPrefixBearer) {
        OrderModel orderRequestModel = this.customerRequestMapper.toOrderModel(createOrderRequestDto);
        final List<OrderDishModel> ordersDishesRequest = createOrderRequestDto.getDishes().stream().map(this.customerRequestMapper::toOrderDishModel).collect(Collectors.toList());
        orderRequestModel.setOrdersDishesModel(ordersDishesRequest);
        return this.customerResponseMapper.toCreateOrderResponseDto(customerServicePort.saveOrder(orderRequestModel, tokenWithPrefixBearer));
    }
}
