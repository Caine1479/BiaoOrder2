package com.biaoorder2.bean;

import androidx.annotation.NonNull;

public class HistoryBill {
    private int no;
    private String vegetables;
    private String price;
    private String date;
    private String payment;
    private int orderNo;

    public HistoryBill(int no, String vegetables, String price, String date, String payment, int orderNo) {
        this.no = no;
        this.vegetables = vegetables;
        this.price = price;
        this.date = date;
        this.payment = payment;
        this.orderNo = orderNo;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getVegetables() {
        return vegetables;
    }

    public void setVegetables(String vegetables) {
        this.vegetables = vegetables;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    @NonNull
    @Override
    public String toString() {
        return "桌号: " + no + "\n" + "菜品: \n" + vegetables + "总价: " + price + "元"+"\n" + date + "\n" + payment + "\n\n";
    }
}
