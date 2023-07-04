package com.reto.plazoleta.domain.model.dishes;

import com.reto.plazoleta.domain.model.CategoryModel;
import com.reto.plazoleta.domain.model.RestaurantModel;

public class SoupDish extends DishModel {

    private String sideDish;

    public SoupDish() {
    }

    public SoupDish(String sideDish) {
        this.sideDish = sideDish;
    }

    public SoupDish(Long idDish, String sideDish) {
        super(idDish);
        this.sideDish = sideDish;
    }

    public SoupDish(Long idDish, String name, String descriptionDish, Double price,
                    String imageDish, Boolean stateDish, RestaurantModel restaurantModel,
                    CategoryModel categoryModel) {
        super(idDish, name, descriptionDish, price, imageDish, stateDish, restaurantModel, categoryModel);
    }

    public SoupDish(Long idDish, String name, String descriptionDish, Double price,
                    String imageDish, Boolean stateDish, RestaurantModel restaurantModel,
                    CategoryModel categoryModel, String sideDish) {
        super(idDish, name, descriptionDish, price, imageDish, stateDish, restaurantModel, categoryModel);
        this.sideDish = sideDish;
    }

    public String getSideDish() {
        return sideDish;
    }

    public void setSideDish(String sideDish) {
        this.sideDish = sideDish;
    }
}
