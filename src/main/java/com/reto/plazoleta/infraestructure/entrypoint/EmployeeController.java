package com.reto.plazoleta.infraestructure.entrypoint;

import com.reto.plazoleta.application.dto.response.AssignedOrdersResponseDto;
import com.reto.plazoleta.application.dto.response.OrdersPaginatedResponseDto;
import com.reto.plazoleta.application.handler.IEmployeeRestaurantService;
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
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/micro-small-square/")
public class EmployeeController {

    private static final String STATUS_DEFAULT = "PENDIENTE";
    private final IEmployeeRestaurantService employeeRestaurantService;

    @Operation(summary = "List orders paginated by the field status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders filtered by status"),
            @ApiResponse(responseCode = "204", description = "No order found"),
            @ApiResponse(responseCode = "403", description = "Role other than employee"),
            @ApiResponse(responseCode = "404", description = "The Restaurant not exist")
    })
    @PreAuthorize(value = "hasRole('EMPLEADO')")
    @GetMapping(value = "orders")
    public ResponseEntity<Page<OrdersPaginatedResponseDto>> getAllOrdersFilterByStatus(
            @Parameter( description = "Number of orders by page", schema = @Schema(implementation = Integer.class))
            @RequestParam(name = "sizeItems", defaultValue = "10") Integer sizeItems,
            @Parameter( description = "Number of the page in the pagination of the orders", schema = @Schema(implementation = Integer.class))
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @Parameter( description = "order status name", schema = @Schema(implementation = String.class))
            @RequestParam(name = "status", defaultValue = STATUS_DEFAULT) String status,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String tokenWithPrefixBearer) {
        final Page<OrdersPaginatedResponseDto> ordersPaginated = this.employeeRestaurantService.getAllOrdersFilterByStatus(sizeItems, pageNumber,  status, tokenWithPrefixBearer);
        return ResponseEntity.ok(ordersPaginated);
    }
  
    @Operation(summary = "The employee will be assigned to one or more orders at the same time and the order was changed to pending status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The employee was assigned to one or more orders successfully"),
            @ApiResponse(responseCode = "403", description = "Role other than employee"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "404", description = "The restaurant that the employee belongs to does not exist"),
            @ApiResponse(responseCode = "409", description = "The order is already in process with another employee")
    })
    @PatchMapping(value = "restaurant/orders")
    @PreAuthorize(value = "hasRole('EMPLEADO')")
    public ResponseEntity<List<AssignedOrdersResponseDto>> assignOrderAndChangeStatusToInPreparation(
            @Parameter( description = "List of order identifiers to be assigned to the employee", schema = @Schema(implementation = List.class))
            @RequestParam(name = "idOrder") List<Long> idOrders,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String tokenWithPrefixBearer) {
        return new ResponseEntity<>(this.employeeRestaurantService
                .assignOrderAndChangeStatusToInPreparation(idOrders, tokenWithPrefixBearer), HttpStatus.OK);
    }
}
