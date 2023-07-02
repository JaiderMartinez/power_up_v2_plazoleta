package com.reto.plazoleta.domain.model.dishes;

import com.reto.plazoleta.domain.model.CategoryModel;
import com.reto.plazoleta.domain.model.RestaurantModel;

public class DessertDish extends DishModel {

    public DessertDish() {
    }

    public DessertDish(Long idDish, String name, String descriptionDish, Double price,
                       String imageDish, Boolean stateDish, RestaurantModel restaurantModel,
                       CategoryModel categoryModel) {
        super(idDish, name, descriptionDish, price, imageDish, stateDish, restaurantModel, categoryModel);
    }
}
