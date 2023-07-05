package com.reto.plazoleta.infraestructure.entrypoint;

import com.reto.plazoleta.application.dto.request.OrderDishTypeRequestDto;
import com.reto.plazoleta.application.dto.request.SingleDishOrderRequestDto;
import com.reto.plazoleta.application.dto.response.OrderDishTypeDtoResponse;
import com.reto.plazoleta.application.dto.response.SingleDishOrderResponseDto;
import com.reto.plazoleta.application.dto.response.pending_orders.PendingOrderResponseDto;
import com.reto.plazoleta.application.dto.response.takenorder.OrderTakenResponseDto;
import com.reto.plazoleta.application.handler.IOrderHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Operation(summary = "Pending orders sorted by low priority")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending orders sorted by low priority"),
            @ApiResponse(responseCode = "204", description = "No orders found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Role other than employee", content = @Content)
    })
    @GetMapping(value = "pending-orders")
    @PreAuthorize(value = "hasRole('EMPLEADO')")
    public ResponseEntity<List<PendingOrderResponseDto>> pendingOrdersWithLowPriority() {
        return ResponseEntity.ok(this.orderHandler.pendingOrdersWithLowPriority());
    }

    @Operation(summary = "Add single dish order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registered single dish order"),
            @ApiResponse(responseCode = "403", description = "Role other than customer", content = @Content),
            @ApiResponse(responseCode = "404", description = "The dish not exist", content = @Content),
            @ApiResponse(responseCode = "404", description = "The restaurant not exist", content = @Content),
            @ApiResponse(responseCode = "404", description = "Dish type meat grams its range is different between 250 to 750", content = @Content)
    })
    @PostMapping(value = "{idRestaurant}/add-order")
    @PreAuthorize(value = "hasRole('CLIENTE')")
    public ResponseEntity<SingleDishOrderResponseDto> addSingleDishOrder(@Parameter(
            description = "Object to add details of an order as a customer",required = true,
            schema = @Schema(implementation = SingleDishOrderRequestDto.class))
            @RequestBody SingleDishOrderRequestDto singleDishOrderRequestDto,
            @PathVariable(name = "idRestaurant") Long idRestaurant) {
        final SingleDishOrderResponseDto registeredSingleDishOrder = this.orderHandler.addSingleDishOrder(singleDishOrderRequestDto, idRestaurant);
        return new ResponseEntity<>(registeredSingleDishOrder, HttpStatus.CREATED);
    }

    @Operation(summary = "Add list of dishes in an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order registered with your list of dishes"),
            @ApiResponse(responseCode = "403", description = "Role other than customer", content = @Content),
            @ApiResponse(responseCode = "404", description = "The dish not exist", content = @Content),
            @ApiResponse(responseCode = "404", description = "The restaurant not exist", content = @Content),
            @ApiResponse(responseCode = "404", description = "Dish type meat grams its range is different between 250 to 750", content = @Content)
    })
    @PostMapping(value = "{idRestaurant}/add-orders")
    @PreAuthorize(value = "hasRole('CLIENTE')")
    public ResponseEntity<List<OrderDishTypeDtoResponse>> addDishesToOrderWithMultipleDishesType(@Parameter(
            description = "Dto for types of dishes in an order",required = true,
            schema = @Schema(implementation = OrderDishTypeRequestDto.class))
            @RequestBody List<OrderDishTypeRequestDto> orderDishTypeRequestDto,
            @PathVariable(name = "idRestaurant") Long idRestaurant) {
        final List<OrderDishTypeDtoResponse> ordersDishesTypeResponse = this.orderHandler.addDishesToOrderWithMultipleDishesType(orderDishTypeRequestDto, idRestaurant);
        return ResponseEntity.ok(ordersDishesTypeResponse);
    }
}
