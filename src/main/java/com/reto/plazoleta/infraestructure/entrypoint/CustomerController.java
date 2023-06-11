package com.reto.plazoleta.infraestructure.entrypoint;

import com.reto.plazoleta.application.dto.response.DishesOfRestaurantPaginatedResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantResponsePageableDto;
import com.reto.plazoleta.application.handler.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<Page<RestaurantResponsePageableDto>> getAllRestaurantsByOrderByNameAsc(
            @Parameter(
                    description = "Number of restaurant items by page",
                    schema = @Schema(implementation = Integer.class))
            @RequestParam(name = "sizeItemsByPages", required = true, defaultValue = "5") Integer sizeItemsByPages) {
        int numberPage = 0;
        return ResponseEntity.ok(customerService.getAllRestaurantsByOrderByNameAsc(numberPage, sizeItemsByPages));
    }

    @Operation(summary = "get dishes paginated by a number of elements and grouped by category from a restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list from dishes from a restaurant grouped by category"),
            @ApiResponse(responseCode = "400", description = "The value of the idRestaurant request param is empty"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "404", description = "No data found")
    })
    @GetMapping(value = "/get-dishes-paginated")
    @PreAuthorize(value = "hasRole('CLIENTE')")
    public ResponseEntity<DishesOfRestaurantPaginatedResponseDto> getDishesFromARestaurantAndGroupedByCategoryPaginated(@RequestParam(name = "size", defaultValue = "5") Integer sizeItems,
                                                                                                                        @RequestParam(name = "idRestaurant", defaultValue = "0") Long idRestaurant) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
