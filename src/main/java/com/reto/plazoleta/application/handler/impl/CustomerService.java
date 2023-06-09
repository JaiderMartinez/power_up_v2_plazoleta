package com.reto.plazoleta.application.handler.impl;

import com.reto.plazoleta.application.dto.request.OrderRequestDto;
import com.reto.plazoleta.application.dto.response.CategoryFromDishesPaginatedResponseDto;
import com.reto.plazoleta.application.dto.response.OrderCanceledResponseDto;
import com.reto.plazoleta.application.dto.response.OrderCreatedResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantResponsePageableDto;
import com.reto.plazoleta.application.handler.ICustomerService;
import com.reto.plazoleta.application.mapper.requestmapper.ICustomerRequestMapper;
import com.reto.plazoleta.application.mapper.responsemapper.ICustomerResponseMapper;
import com.reto.plazoleta.domain.api.ICustomerServicePort;
import com.reto.plazoleta.domain.model.orders.OrderDishModel;
import com.reto.plazoleta.domain.model.orders.OrderModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CustomerService implements ICustomerService {

    private final ICustomerServicePort customerServicePort;
    private final ICustomerRequestMapper customerRequestMapper;
    private final ICustomerResponseMapper customerResponseMapper;

    @Override
    public Page<RestaurantResponsePageableDto> getAllRestaurantsByOrderByNameAsc(Integer numberPage, Integer sizeItems) {
        return this.customerServicePort.findAllByOrderByNameAsc(numberPage, sizeItems).map(this.customerResponseMapper::toRestaurantResponse);
    }

    @Override
    public Page<CategoryFromDishesPaginatedResponseDto> getDishesFromARestaurantAndGroupedByCategoryPaginated(Integer numberPage, Integer sizeItems, Long idRestaurant) {
        return this.customerServicePort.getAllDishesActivePaginatedFromARestaurantOrderByCategoryAscending(numberPage, sizeItems, idRestaurant)
                .stream()
                .collect(Collectors.groupingBy(
                        dishModelResponse -> dishModelResponse.getCategoryModel().getIdCategory()
                ))
                .entrySet()
                .stream()
                .map(this.customerResponseMapper::mapEntryToCategoryFromDishesPaginatedResponseDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), content -> new PageImpl<>(content, PageRequest.of(numberPage, sizeItems), content.size())));
    }

    @Override
    public OrderCreatedResponseDto saveOrder(OrderRequestDto createOrderRequestDto, String tokenWithPrefixBearer) {
        OrderModel orderRequestModel = this.customerRequestMapper.orderRequestDtoToOrderModel(createOrderRequestDto);
        final List<OrderDishModel> ordersDishesRequest = createOrderRequestDto.getDishes().stream().map(this.customerRequestMapper::dishFromOrderRequestDtoToOrderDishModel).collect(Collectors.toList());
        orderRequestModel.setOrdersDishesModel(ordersDishesRequest);
        return this.customerResponseMapper.toCreateOrderResponseDto(this.customerServicePort.saveOrder(orderRequestModel, tokenWithPrefixBearer));
    }

    @Override
    public OrderCanceledResponseDto cancelOrder(Long idOrder, String tokenWithPrefixBearer) {
        final OrderModel orderCanceledModel = this.customerServicePort.cancelOrder(idOrder, tokenWithPrefixBearer);
        return this.customerResponseMapper.orderModelToOrderCanceledResponseDto(orderCanceledModel);
    }
}
