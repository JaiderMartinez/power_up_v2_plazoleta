package com.reto.plazoleta.application.dto.response.pending_orders;

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
public class PendingOrderResponseDto {

    private Long idOrder;
    private Long idUserCustomer;
    private LocalDate date;
    private String status;
    private List<PendingDishResponseDto> dishes;
}
