package com.reto.plazoleta.infraestructure.drivenadapter.persistence;

import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.domain.spi.IOrderPersistencePort;
import com.reto.plazoleta.infraestructure.drivenadapter.mapper.IOrderEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public OrderModel saveOrder(OrderModel orderModel) {
        return orderEntityMapper.toOrderModel(
                orderRepository.save(orderEntityMapper.toOrderEntity(orderModel)));
    }

    @Override
    public List<OrderModel> findByIdUserCustomerAndIdRestaurant(Long idUser, Long idRestaurant) {
        return orderRepository.findByIdUserCustomerAndRestaurantEntityIdRestaurant(idUser, idRestaurant).stream()
                .map(orderEntityMapper::toOrderModel).collect(Collectors.toList());
    }
}
