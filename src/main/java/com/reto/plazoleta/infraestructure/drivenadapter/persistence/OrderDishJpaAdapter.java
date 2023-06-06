package com.reto.plazoleta.infraestructure.drivenadapter.persistence;

import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.domain.spi.IOrderDishPersistencePort;
import com.reto.plazoleta.infraestructure.drivenadapter.mapper.IOrderDishEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IOrderDishRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderDishJpaAdapter implements IOrderDishPersistencePort {

    private final IOrderDishRepository orderDishRepository;
    private final IOrderDishEntityMapper orderDishEntityMapper;

    @Override
    public void saveAllOrdersDishes(List<OrderDishModel> orderDishModelList) {
        orderDishRepository.saveAll(orderDishModelList.stream().map(this.orderDishEntityMapper::toOrderDishEntity).collect(Collectors.toList()));
    }
}
