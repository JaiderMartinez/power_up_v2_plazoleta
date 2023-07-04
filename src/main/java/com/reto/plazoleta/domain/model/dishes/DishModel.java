package com.reto.plazoleta.domain.model.dishes;

import com.reto.plazoleta.domain.model.CategoryModel;
import com.reto.plazoleta.domain.model.RestaurantModel;

public class DishModel {

    private Long idDish;
    private String name;
    private String description;
    private Double price;
    private String urlImageDish;
    private Boolean state;
    private RestaurantModel restaurantModel;
    private CategoryModel categoryModel;

    public DishModel() {
    }

    public DishModel(Long idDish) {
        this.idDish = idDish;
    }

    public DishModel(Long idDish, String name, String descriptionDish, Double price, String imageDish, Boolean stateDish, RestaurantModel restaurantModel, CategoryModel categoryModel) {
        this.idDish = idDish;
        this.name = name;
        this.description = descriptionDish;
        this.price = price;
        this.urlImageDish = imageDish;
        this.state = stateDish;
        this.restaurantModel = restaurantModel;
        this.categoryModel = categoryModel;
    }

    public Long getIdDish() {
        return idDish;
    }

    public void setIdDish(Long idDish) {
        this.idDish = idDish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUrlImageDish() {
        return urlImageDish;
    }

    public void setUrlImageDish(String urlImageDish) {
        this.urlImageDish = urlImageDish;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public RestaurantModel getRestaurantModel() {
        return restaurantModel;
    }

    public void setRestaurantModel(RestaurantModel restaurantModel) {
        this.restaurantModel = restaurantModel;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }

    public void updateAllDataFromAllFieldsFromDishModel(DishModel dishModel) {
        this.name = dishModel.getName();
        this.description = dishModel.getDescription();
        this.price = dishModel.getPrice();
        this.urlImageDish = dishModel.getUrlImageDish();
        this.state = dishModel.getState();
        this.restaurantModel = dishModel.getRestaurantModel();
        this.categoryModel = dishModel.getCategoryModel();
    }
}
