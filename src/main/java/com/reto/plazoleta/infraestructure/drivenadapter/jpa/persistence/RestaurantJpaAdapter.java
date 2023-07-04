package com.reto.plazoleta.infraestructure.drivenadapter.jpa.persistence;

import com.reto.plazoleta.domain.model.RestaurantModel;
import com.reto.plazoleta.domain.spi.persistence.IRestaurantPersistencePort;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper.IRestaurantEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Override
    public RestaurantModel saveRestaurant(RestaurantModel restaurantModel) {
        return restaurantEntityMapper.toRestaurantModel(
                restaurantRepository.save(restaurantEntityMapper.toRestaurantEntity(restaurantModel)));
    }

    @Override
    public Page<RestaurantModel> findAllByOrderByNameAsc(Pageable pageable) {
        return restaurantRepository.findAllByOrderByName(pageable).map(restaurantEntityMapper::toRestaurantModel);
    }

    @Override
    public RestaurantModel findByIdRestaurant(Long idRestaurant) {
        return restaurantEntityMapper.toRestaurantModel(
                restaurantRepository.findById(idRestaurant).orElse(null));
    }

    @Override
    public boolean existRestaurantById(Long idRestaurant) {
        return this.restaurantRepository.existsById(idRestaurant);
    }
}
