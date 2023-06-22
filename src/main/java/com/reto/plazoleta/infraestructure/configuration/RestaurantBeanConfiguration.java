package com.reto.plazoleta.infraestructure.configuration;

import com.reto.plazoleta.domain.api.IAdminServicePort;
import com.reto.plazoleta.domain.spi.clients.IUserGateway;
import com.reto.plazoleta.domain.spi.persistence.IRestaurantPersistencePort;
import com.reto.plazoleta.domain.usecase.AdminUseCase;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper.IRestaurantEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.persistence.RestaurantJpaAdapter;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RestaurantBeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IUserGateway userGateway;

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IAdminServicePort restaurantUseCase() {
        return new AdminUseCase(restaurantPersistencePort(), userGateway);
    }

}
