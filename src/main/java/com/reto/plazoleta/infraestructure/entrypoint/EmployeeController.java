package com.reto.plazoleta.infraestructure.entrypoint;

import com.reto.plazoleta.application.dto.response.OrdersPaginatedResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/micro-small-square/")
public class EmployeeController {

    private static final String STATUS_DEFAULT = "PENDIENTE";

    @Operation(summary = "List orders paginated by the field status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders filtered by status"),
            @ApiResponse(responseCode = "403", description = "Role other than employee"),
            @ApiResponse(responseCode = "204", description = "No order found")
    })
    @PreAuthorize(value = "hasRole('EMPLEADO')")
    @GetMapping(value = "orders")
    public ResponseEntity<Page<OrdersPaginatedResponseDto>> getAllOrdersFilterByStatus(
            @Parameter( description = "Number of orders by page", schema = @Schema(implementation = String.class))
            @RequestParam(name = "sizeItems", defaultValue = "10") Integer sizeItems,
            @Parameter( description = "order status name", schema = @Schema(implementation = String.class))
            @RequestParam(name = "status", defaultValue = STATUS_DEFAULT) String status,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String tokenWithPrefixBearer) {
        return ResponseEntity.ok(null);
    }
}
