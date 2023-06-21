package com.reto.plazoleta.infraestructure.drivenadapter.webclients.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageSmsRequestDto {

    private Long orderPin;
    private String restaurantName;
    private String customerName;
    private String customerCellPhone;
}
