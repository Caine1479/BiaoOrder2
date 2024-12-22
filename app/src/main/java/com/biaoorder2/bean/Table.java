package com.biaoorder2.bean;

public class Table {
    private int no;
    private String name;
    private String phone;
    private String state;
    private String currentOrder;
    private int currentTotal;

    public Table(int no, String name, String phone, String state, String currentOrder, int currentTotal) {
        this.no = no;
        this.name = name;
        this.phone = phone;
        this.state = state;
        this.currentOrder = currentOrder;
        this.currentTotal = currentTotal;
    }

    public int getCurrentTotal() {
        return currentTotal;
    }

    public void setCurrentTotal(int currentTotal) {
        this.currentTotal = currentTotal;
    }

    public String getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(String currentOrder) {
        this.currentOrder = currentOrder;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
