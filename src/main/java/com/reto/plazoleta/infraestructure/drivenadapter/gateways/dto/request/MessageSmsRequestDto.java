package com.reto.plazoleta.infraestructure.drivenadapter.gateways.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageSmsRequestDto {

    private Long pin;
    private String restaurantName;
    private String customerName;
    private String customerCellPhone;
}
