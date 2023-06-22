package com.reto.plazoleta.infraestructure.configuration;


import com.reto.plazoleta.domain.spi.persistence.ICategoryPersistencePort;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.mapper.ICategoryEntityMapper;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.persistence.CategoryJpaAdapter;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository.ICategoryRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CategoryBeanConfiguration {

    private final ICategoryRepository categoryRepository;
    private final ICategoryEntityMapper categoryEntityMapper;

    @Bean
    public ICategoryPersistencePort categoryPersistencePort() {
        return new CategoryJpaAdapter(categoryRepository, categoryEntityMapper );
    }
}