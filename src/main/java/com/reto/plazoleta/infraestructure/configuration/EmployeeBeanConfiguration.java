package com.reto.plazoleta.infraestructure.configuration;

import com.reto.plazoleta.domain.api.IEmployeeServicePort;
import com.reto.plazoleta.domain.gateways.IUserGateway;
import com.reto.plazoleta.domain.spi.IEmployeeRestaurantPersistencePort;
import com.reto.plazoleta.domain.spi.clients.IMessengerServiceProviderPort;
import com.reto.plazoleta.domain.spi.IOrderPersistencePort;
import com.reto.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.reto.plazoleta.domain.spi.token.ITokenServiceProviderPort;
import com.reto.plazoleta.domain.usecase.EmployeeRestaurantUseCase;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.JwtProvider;
import com.reto.plazoleta.infraestructure.drivenadapter.mapper.IEmployeeEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.persistence.EmployeeRestaurantJpaAdapter;
import com.reto.plazoleta.infraestructure.drivenadapter.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EmployeeBeanConfiguration {

    private final IEmployeeRepository employeeRepository;
    private final IEmployeeEntityMapper employeeEntityMapper;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserGateway userGateway;
    private final JwtProvider jwtProvider;
    private final IOrderPersistencePort orderPersistencePort;
    private final ITokenServiceProviderPort tokenServiceProviderPort;
    private final IMessengerServiceProviderPort messengerServiceProviderPort;

    @Bean
    public IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort() {
        return new EmployeeRestaurantJpaAdapter(this.employeeRepository, this.employeeEntityMapper);
    }

    @Bean
    public IEmployeeServicePort employeeRestaurantServicePort() {
        return new EmployeeRestaurantUseCase(employeeRestaurantPersistencePort(), this.restaurantPersistencePort, this.userGateway,
                this.jwtProvider, this.tokenServiceProviderPort, this.orderPersistencePort, this.messengerServiceProviderPort);
    }
}
