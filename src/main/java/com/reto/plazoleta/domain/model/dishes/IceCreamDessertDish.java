package com.reto.plazoleta.domain.model.dishes;

import com.reto.plazoleta.domain.model.CategoryModel;
import com.reto.plazoleta.domain.model.RestaurantModel;

public class IceCreamDessertDish extends DessertDish {

    private String flavor;

    public IceCreamDessertDish() {
    }

    public IceCreamDessertDish(String flavor) {
        this.flavor = flavor;
    }

    public IceCreamDessertDish(Long idDish, String flavor) {
        super(idDish);
        this.flavor = flavor;
    }

    public IceCreamDessertDish(Long idDish, String name, String descriptionDish, Double price,
                               String imageDish, Boolean stateDish, RestaurantModel restaurantModel,
                               CategoryModel categoryModel) {
        super(idDish, name, descriptionDish, price, imageDish, stateDish, restaurantModel, categoryModel);
    }

    public IceCreamDessertDish(Long idDish, String name, String descriptionDish, Double price,
                               String imageDish, Boolean stateDish, RestaurantModel restaurantModel,
                               CategoryModel categoryModel, String flavor) {
        super(idDish, name, descriptionDish, price, imageDish, stateDish, restaurantModel, categoryModel);
        this.flavor = flavor;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }
}
