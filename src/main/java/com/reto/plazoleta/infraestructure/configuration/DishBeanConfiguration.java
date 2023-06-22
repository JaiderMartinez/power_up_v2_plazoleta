package com.reto.plazoleta.infraestructure.configuration;

import com.reto.plazoleta.domain.api.IOwnerRestaurantServicePort;
import com.reto.plazoleta.domain.spi.clients.IUserGateway;
import com.reto.plazoleta.domain.spi.persistence.ICategoryPersistencePort;
import com.reto.plazoleta.domain.spi.persistence.IDishPersistencePort;
import com.reto.plazoleta.domain.spi.persistence.IRestaurantPersistencePort;
import com.reto.plazoleta.domain.usecase.OwnerRestaurantUseCase;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper.IDishEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.persistence.DishJpaAdapter;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DishBeanConfiguration {

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;
    private final ICategoryPersistencePort categoryPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserGateway userGateway;
    private final JwtProvider jwtProvider;

    @Bean
    public IDishPersistencePort dishPersistencePort() {
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public IOwnerRestaurantServicePort ownerRestaurantServicePort(){
        return new OwnerRestaurantUseCase(dishPersistencePort(), restaurantPersistencePort, categoryPersistencePort, userGateway, jwtProvider);
    }
}