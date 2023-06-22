package com.reto.plazoleta.infraestructure.drivenadapter.jpa.persistence;

import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.domain.spi.persistence.IOrderDishPersistencePort;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.OrderDishEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper.IOrderEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.IOrderDishRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderDishJpaAdapter implements IOrderDishPersistencePort {

    private final IOrderDishRepository orderDishRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public List<OrderDishModel> saveAllOrdersDishes(List<OrderDishModel> orderDishModelList) {
        List<OrderDishEntity> orderDishEntitiesToSave = orderDishModelList.stream().map(this.orderEntityMapper::toOrderDishEntity).collect(Collectors.toList());
        List<OrderDishEntity> orderDishEntitiesSaved = this.orderDishRepository.saveAll(orderDishEntitiesToSave);
        return orderDishEntitiesSaved.stream().map(this.orderEntityMapper::toOrderDishModel).collect(Collectors.toList());
    }
}
