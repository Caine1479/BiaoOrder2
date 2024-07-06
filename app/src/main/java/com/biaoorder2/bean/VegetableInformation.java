package com.biaoorder2.bean;

import androidx.annotation.NonNull;

public class VegetableInformation {
    private int id;
    private String name;
    private String price;
    private String imageLink;

    public VegetableInformation(int id, String name, String price, String imageLink) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageLink = imageLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

}
