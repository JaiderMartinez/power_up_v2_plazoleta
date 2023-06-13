package com.reto.plazoleta.infraestructure.entrypoint;

import com.reto.plazoleta.application.dto.response.AssignedOrdersResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/micro-small-square/")
public class EmployeeController {

    @Operation(summary = "The employee will be assigned to one or more orders at the same time and the order was changed to pending status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The employee was assigned to one or more orders successfully"),
            @ApiResponse(responseCode = "403", description = "Role other than employee"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "404", description = "The restaurant that the employee belongs to does not exist"),
            @ApiResponse(responseCode = "409", description = "The order is already in process with another employee")
    })
    @PatchMapping(value = "assign-orders")
    @PreAuthorize(value = "hasRole('EMPLEADO')")
    public ResponseEntity<AssignedOrdersResponseDto> assignOrderAndChangeStatusToPending(
            @Parameter( description = "List of order identifiers to be assigned to the employee", schema = @Schema(implementation = List.class))
            @RequestParam(name = "idOrder") List<Long> idOrders,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String tokenWithPrefixBearer) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
