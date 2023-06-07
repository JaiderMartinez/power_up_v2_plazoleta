package com.reto.plazoleta.infraestructure.configuration;

import com.reto.plazoleta.domain.api.IOrderServicePort;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.spi.IDishPersistencePort;
import com.reto.plazoleta.domain.spi.IOrderDishPersistencePort;
import com.reto.plazoleta.domain.spi.IOrderPersistencePort;
import com.reto.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.reto.plazoleta.domain.usecase.OrderUseCase;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.mapper.IOrderDishEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.mapper.IOrderEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.persistence.OrderDishJpaAdapter;
import com.reto.plazoleta.infraestructure.drivenadapter.persistence.OrderJpaAdapter;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IOrderDishRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class OrderBeanConfiguration {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;
    private final IUserGateway userGateway;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IOrderDishRepository orderDishRepository;
    private final IOrderDishEntityMapper orderDishEntityMapper;
    private final JwtProvider jwtProvider;

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(this.orderRepository, this.orderEntityMapper);
    }

    @Bean
    public IOrderDishPersistencePort orderDishPersistencePort() {
        return new OrderDishJpaAdapter(this.orderDishRepository, this.orderDishEntityMapper);
    }

    @Bean
    public IOrderServicePort orderServicePort() {
        return new OrderUseCase(this.orderPersistencePort(), this.restaurantPersistencePort, this.dishPersistencePort,
                this.userGateway, orderDishPersistencePort(), this.jwtProvider);
    }
}
