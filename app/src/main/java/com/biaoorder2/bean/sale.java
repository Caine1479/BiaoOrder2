package com.biaoorder2.bean;

import androidx.annotation.NonNull;

public class sale {
    private String name;
    private int sales;

    public sale(String name, int sales) {
        this.name = name;
        this.sales = sales;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }
}
