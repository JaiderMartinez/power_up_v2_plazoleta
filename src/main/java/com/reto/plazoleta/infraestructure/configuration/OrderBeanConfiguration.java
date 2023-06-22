package com.reto.plazoleta.infraestructure.configuration;

import com.reto.plazoleta.domain.api.ICustomerServicePort;
import com.reto.plazoleta.domain.spi.clients.IUserGateway;
import com.reto.plazoleta.domain.spi.persistence.IDishPersistencePort;
import com.reto.plazoleta.domain.spi.persistence.IOrderDishPersistencePort;
import com.reto.plazoleta.domain.spi.persistence.IOrderPersistencePort;
import com.reto.plazoleta.domain.spi.persistence.IRestaurantPersistencePort;
import com.reto.plazoleta.domain.usecase.CustomerUseCase;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper.IOrderEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.persistence.OrderDishJpaAdapter;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.persistence.OrderJpaAdapter;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.IOrderDishRepository;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.IOrderRepository;
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
    private final JwtProvider jwtProvider;
    private final IOrderDishRepository orderDishRepository;

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(this.orderRepository, this.orderEntityMapper);
    }

    @Bean
    public IOrderDishPersistencePort orderDishPersistencePort() {
        return new OrderDishJpaAdapter(this.orderDishRepository, this.orderEntityMapper);
    }

    @Bean
    public ICustomerServicePort orderServicePort() {
        return new CustomerUseCase(this.orderPersistencePort(), this.restaurantPersistencePort, this.dishPersistencePort,
                this.userGateway, this.jwtProvider, orderDishPersistencePort());
    }
}
