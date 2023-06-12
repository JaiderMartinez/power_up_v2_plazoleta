package com.reto.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdersPaginatedResponseDto {

    private Long idOrder;
    private LocalDate date;
    private String status;
    private List<OrdersDishesPaginatedResponseDto> ordersDishes;
}
