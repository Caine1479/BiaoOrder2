package com.biaoorder2.bean;

import androidx.annotation.NonNull;

public class VegetableInformation {
    private int id;
    private String name;
    private String price;
    private String imageLink;
    private int sales;
    private String category;

    public VegetableInformation(int id, String name, String price, String imageLink, int sales, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageLink = imageLink;
        this.sales = sales;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
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

    @NonNull
    @Override
    public String toString() {
        return "VegetableInformation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", sales=" + sales +
                ", category='" + category + '\'' +
                '}';
    }
}
