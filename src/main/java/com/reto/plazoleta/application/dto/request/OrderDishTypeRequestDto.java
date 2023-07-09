package com.reto.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDishTypeRequestDto {

    private Long idDish;
    private String typeDish;
    private String typeDessert;
    private String sideDish;
    private String flavor;
    private Integer grams;
}
