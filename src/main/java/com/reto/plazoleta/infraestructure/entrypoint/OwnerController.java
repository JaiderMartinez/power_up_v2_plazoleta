package com.reto.plazoleta.infraestructure.entrypoint;

import com.reto.plazoleta.application.dto.request.DishCreateRequestDto;
import com.reto.plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.reto.plazoleta.application.dto.response.DishCreatedResponseDto;
import com.reto.plazoleta.application.dto.response.DishStatusResponseDto;
import com.reto.plazoleta.application.dto.response.RestaurantEmployeeResponseDto;
import com.reto.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.reto.plazoleta.application.dto.response.UpdateDishResponseDto;
import com.reto.plazoleta.application.handler.IOwnerRestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/micro-small-square/")
public class OwnerController {

    private final IOwnerRestaurantService ownerRestaurantService;

    @PreAuthorize(value = "hasRole('PROPIETARIO')")
    @Operation(summary = "Add a new dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish created"),
            @ApiResponse(responseCode = "400", description = "The format in the fields is invalid", content = @Content),
            @ApiResponse(responseCode = "409", description = "There are empty fields", content = @Content)
    })
    @PostMapping(value = "restaurant/dish")
    public ResponseEntity<DishCreatedResponseDto> createDishFromARestaurant(@RequestBody DishCreateRequestDto dishCreateRequestDto) {
        final DishCreatedResponseDto dishCreatedResponseDto = ownerRestaurantService.saveDish(dishCreateRequestDto);
        return new ResponseEntity<>(dishCreatedResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Update dish price and description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Price and description update"),
            @ApiResponse(responseCode = "401", description = "The format in the fields is invalid", content = @Content),
            @ApiResponse(responseCode = "403", description = "no access allowed", content = @Content)
    })
    @PatchMapping(value = "restaurant/dish/update")
    @PreAuthorize(value = "hasRole('PROPIETARIO')")
    public ResponseEntity<UpdateDishResponseDto> updateDishPriceAndDescription(@RequestBody UpdateDishRequestDto updateDishRequestDto) {
        final UpdateDishResponseDto dishResponseDto = ownerRestaurantService.updateDish(updateDishRequestDto);
        return new ResponseEntity<>(dishResponseDto,HttpStatus.OK);
    }
  
    @Operation(summary = "Add a new user employee in a restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "restaurant employee created"),
            @ApiResponse(responseCode = "403", description = "The user does not have the owner role", content = @Content),
            @ApiResponse(responseCode = "404", description = "The restaurant not found", content = @Content)
    })
    @PreAuthorize(value = "hasRole('PROPIETARIO')")
    @PostMapping(value = "restaurant/employee")
    public ResponseEntity<RestaurantEmployeeResponseDto> saveUserEmployeeInARestaurant(@RequestBody RestaurantEmployeeRequestDto restaurantEmployeeRequestDto,
                                                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String tokenWithBearerPrefix) {
        return new ResponseEntity<>(this.ownerRestaurantService.saveUserEmployeeInTheRestaurant(restaurantEmployeeRequestDto, tokenWithBearerPrefix), HttpStatus.CREATED);
    }

    @Operation(summary = "Update the active variable of the dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The field from dish isActive updated"),
            @ApiResponse(responseCode = "400", description = "The user is not the owner of this restaurant", content = @Content),
            @ApiResponse(responseCode = "400", description = "The value of field idRestaurant does not match the dish's idRestaurant", content = @Content),
            @ApiResponse(responseCode = "404", description = "The restaurant not exist", content = @Content),
            @ApiResponse(responseCode = "404", description = "The dish does not exist", content = @Content)
    })
    @PatchMapping(value = "restaurant/{idRestaurant}/dish/{idDish}/update/status/{active}")
    @PreAuthorize(value = "hasRole('PROPIETARIO')")
    public ResponseEntity<DishStatusResponseDto> enableOrDisableDishByFieldStatus(@PathVariable(name = "active") boolean active,
                                                                                  @PathVariable(name = "idRestaurant") Long idRestaurant,
                                                                                  @PathVariable(name = "idDish") Long idDish,
                                                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String tokenWithBearerPrefix) {
        final DishStatusResponseDto dishUpdatedTheVariable = this.ownerRestaurantService.enableOrDisableDishByFieldStatus(idDish, idRestaurant, active, tokenWithBearerPrefix);
        return new ResponseEntity<>(dishUpdatedTheVariable, HttpStatus.OK);
    }
}