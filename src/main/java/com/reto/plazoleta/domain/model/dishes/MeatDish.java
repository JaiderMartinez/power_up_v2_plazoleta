package com.reto.plazoleta.domain.model.dishes;

import com.reto.plazoleta.domain.model.CategoryModel;
import com.reto.plazoleta.domain.model.RestaurantModel;

public class MeatDish extends DishModel {

    private Integer grams;

    public MeatDish() {
    }

    public MeatDish(Integer grams) {
        this.grams = grams;
    }

    public MeatDish(Long idDish, Integer grams) {
        super(idDish);
        this.grams = grams;
    }

    public MeatDish(Long idDish, String name, String descriptionDish, Double price,
                    String imageDish, Boolean stateDish, RestaurantModel restaurantModel,
                    CategoryModel categoryModel) {
        super(idDish, name, descriptionDish, price, imageDish, stateDish, restaurantModel, categoryModel);
    }

    public MeatDish(Long idDish, String name, String descriptionDish, Double price,
                    String imageDish, Boolean stateDish, RestaurantModel restaurantModel,
                    CategoryModel categoryModel, Integer grams) {
        super(idDish, name, descriptionDish, price, imageDish, stateDish, restaurantModel, categoryModel);
        this.grams = grams;
    }

    public Integer getGrams() {
        return grams;
    }

    public void setGrams(Integer grams) {
        this.grams = grams;
    }
}
