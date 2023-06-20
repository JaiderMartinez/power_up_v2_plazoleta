package com.reto.plazoleta.domain.model;

public class MessageSms {

    private Long pin;
    private String restaurantName;
    private String customerName;
    private String customerCellPhone;

    public MessageSms() {
    }

    public MessageSms(Long pin, String restaurantName, String customerName, String customerCellPhone) {
        this.pin = pin;
        this.restaurantName = restaurantName;
        this.customerName = customerName;
        this.customerCellPhone = customerCellPhone;
    }

    public Long getPin() {
        return pin;
    }

    public void setPin(Long pin) {
        this.pin = pin;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCellPhone() {
        return customerCellPhone;
    }

    public void setCustomerCellPhone(String customerCellPhone) {
        this.customerCellPhone = customerCellPhone;
    }
}
