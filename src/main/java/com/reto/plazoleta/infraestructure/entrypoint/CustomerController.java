package com.reto.plazoleta.infraestructure.entrypoint;

import com.reto.plazoleta.application.dto.request.OrderRequestDto;
import com.reto.plazoleta.application.dto.response.OrderCreatedResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantResponsePageableDto;
import com.reto.plazoleta.application.handler.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/micro-small-square/")
public class CustomerController {

    private final ICustomerService customerService;

    @Operation(summary = "List restaurants paginated by a field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of names and url logo of paginated restaurants"),
            @ApiResponse(responseCode = "403", description = "Role other than customer")
    })
    @PreAuthorize(value = "hasRole('CLIENTE')")
    @GetMapping(value = "restaurants")
    public ResponseEntity<Page<RestaurantResponsePageableDto>> getAllRestaurantsOrderByNameAsc(
            @Parameter( description = "Number of restaurant items by page", schema = @Schema(implementation = Integer.class))
            @RequestParam(name = "sizePage", defaultValue = "5") Integer sizeItemsByPages,
            @Parameter( description = "Page number in the pagination of the restaurants", schema = @Schema(implementation = Integer.class))
            @RequestParam(name = "numberPage", defaultValue = "0") Integer numberPage) {
        return ResponseEntity.ok(customerService.getAllRestaurantsByOrderByNameAsc(numberPage, sizeItemsByPages));
    }

    @Operation(summary = "Make an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registered order"),
            @ApiResponse(responseCode = "403", description = "Role other than customer"),
            @ApiResponse(responseCode = "404", description = "The Dish not exists"),
            @ApiResponse(responseCode = "404", description = "The Restaurant not exists"),
            @ApiResponse(responseCode = "409", description = "The customer has a order in process")
    })
    @PreAuthorize(value = "hasRole('CLIENTE')")
    @PostMapping(value = "order")
    public ResponseEntity<OrderCreatedResponseDto> registerOrderFromCustomer(@Parameter(
            description = "Object to make an order",required = true,
            schema = @Schema(implementation = OrderRequestDto.class))
            @RequestBody OrderRequestDto orderRequestDto, @Parameter(
            description = "The authentication token with Bearer prefix for search the idUserCustomer",
            required = true, schema = @Schema(type = "String", format = "jwt"))
            @RequestHeader(HttpHeaders.AUTHORIZATION) String tokenWithBearerPrefix) {
        final OrderCreatedResponseDto orderRegistered = this.customerService.saveOrder(orderRequestDto, tokenWithBearerPrefix);
        return new ResponseEntity<>(orderRegistered, HttpStatus.CREATED);
    }
}
