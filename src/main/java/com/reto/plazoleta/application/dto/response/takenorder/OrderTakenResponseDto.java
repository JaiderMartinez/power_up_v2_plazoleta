package com.reto.plazoleta.application.dto.response.takenorder;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderTakenResponseDto {

    private Long idOrder;
    private Long idUserCustomer;
    private LocalDate date;
    private String status;
    private Long idChef;
    private List<DishTypeOrderedResponseDto> dishes;
}
