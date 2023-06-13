package com.reto.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdersDishesPaginatedResponseDto {

    private Long idOrderDish;
    private DishesFromOrderResponseDto dish;
    private Integer amount;
}
