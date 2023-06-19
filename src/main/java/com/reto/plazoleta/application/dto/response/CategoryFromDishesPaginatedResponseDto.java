package com.reto.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFromDishesPaginatedResponseDto {

    private Long idCategory;
    private String categoryName;
    private List<DishResponseDto> dishes;
}
