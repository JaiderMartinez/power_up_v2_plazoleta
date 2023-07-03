package com.reto.plazoleta.infraestructure.entrypoint;

import com.reto.plazoleta.application.dto.response.takenorder.OrderTakenResponseDto;
import com.reto.plazoleta.application.handler.IOrderHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/micro-small-square/restaurant/")
public class OrderController {

    private final IOrderHandler orderHandler;

    @Operation(summary = "The employee takes an order by priority in pending status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order taken by priority and removed from the list"),
            @ApiResponse(responseCode = "204", description = "No order found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Role other than employee", content = @Content)
    })
    @GetMapping(value = "take-order")
    @PreAuthorize(value = "hasRole('EMPLEADO')")
    public ResponseEntity<OrderTakenResponseDto> takeOrderInPendingStatus() {
        final OrderTakenResponseDto orderTakenWithHigherPriority = this.orderHandler.getOrderByPriority();
        return ResponseEntity.ok(orderTakenWithHigherPriority);
    }
}
