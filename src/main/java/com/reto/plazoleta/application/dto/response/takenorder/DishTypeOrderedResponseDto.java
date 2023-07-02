package com.reto.plazoleta.application.dto.response.takenorder;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DishTypeOrderedResponseDto {

    private Long idDish;
    private String typeDish;
    private String typeDessert;
    private String sideDish;
    private String flavor;
    private Integer grams;
}
