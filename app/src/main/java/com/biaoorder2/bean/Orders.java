package com.biaoorder2.bean;

import androidx.annotation.NonNull;

public class Orders {
    public int tableNo;
    public VegetableInformation vegetableInformation;
    public int vegetableNum;
    public String taste;

    public Orders(int tableNo, VegetableInformation vegetableInformation, int vegetableNum, String taste) {
        this.tableNo = tableNo;
        this.vegetableInformation = vegetableInformation;
        this.vegetableNum = vegetableNum;
        this.taste = taste;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
    }

    public VegetableInformation getVegetableInformation() {
        return vegetableInformation;
    }

    public void setVegetableInformation(VegetableInformation vegetableInformation) {
        this.vegetableInformation = vegetableInformation;
    }

    public int getVegetableNum() {
        return vegetableNum;
    }

    public void setVegetableNum(int vegetableNum) {
        this.vegetableNum = vegetableNum;
    }

    public String getTaste() {
        return taste;
    }

    public void setTaste(String taste) {
        this.taste = taste;
    }

    @NonNull
    @Override
    public String toString() {
        return "Orders{" +
                "tableNo=" + tableNo +
                ", vegetableInformation=" + vegetableInformation +
                ", vegetableNum=" + vegetableNum +
                ", taste='" + taste + '\'' +
                '}';
    }
}
