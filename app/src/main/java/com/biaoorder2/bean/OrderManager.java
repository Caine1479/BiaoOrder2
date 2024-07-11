package com.biaoorder2.bean;

import com.biaoorder2.util.CustomDialog;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderManager {
    // 存储订单的 HashMap
    private Map<Integer, List<Orders>> orderMap;

    private HashMap<Integer, String> savedOrders;

    // 只能存放保存的总价
    private HashMap<Integer, Integer> total;

    private HashMap<Integer, Integer> temporaryTotal;


    // 内部静态类用于实现单例模式
    private static class SingletonHelper {
        private static final OrderManager INSTANCE = new OrderManager();
    }

    // 私有的构造函数确保无法从外部实例化
    private OrderManager() {
        orderMap = new HashMap<>();
        savedOrders = new HashMap<>();
        total = new HashMap<>();
        temporaryTotal = new HashMap<>();
    }

    // 获取单例实例的方法
    public static OrderManager getInstance() {
        return SingletonHelper.INSTANCE;
    }

    // 保存订单
    public synchronized void saveOrder(int no, String orderInformation) {
        savedOrders.put(no, orderInformation);
    }

    // 获得已下单的订单
    public synchronized String getSavedOrder(int no) {
        return savedOrders.get(no);
    }

    // 添加订单
    public synchronized void addOrder(int no, Orders order) {
        // 如果 no 对应的订单列表不存在，则创建一个新的订单列表
        orderMap.computeIfAbsent(no, k -> new ArrayList<>()).add(order);
    }

    // 删除指定桌号的指定订单
    public synchronized void removeOrder(int no, Orders order) {
        List<Orders> ordersList = orderMap.get(no);
        if (ordersList != null) {
            ordersList.remove(order);
            if (ordersList.isEmpty()) {
                orderMap.remove(no);
            }
        }
    }


    // 取所有餐桌中菜品的数量
    public synchronized String getAllTableNum(int no) {
        List<Orders> ordersList = orderMap.get(no);
        int sum = 0;
        if (ordersList != null) {
            for (int i = 0; i < ordersList.size(); i++) {
                int count = ordersList.get(i).getVegetableNum();
                sum += count;
            }
        }
        return String.valueOf(sum);
    }

    // 取指定餐桌的菜单
    public synchronized String getOrderInformation(int no) {
        List<Orders> ordersList = getOrders(no);
        StringBuilder result = new StringBuilder();
        Formatter formatter = new Formatter(result, Locale.US);

        // 格式化输出，使每列对齐
        if (ordersList != null) {
            int totalAmount = 0;
            String format = "%-10s%-10s%-10s%-12s%n";

            for (Orders orders : ordersList) {
                int count = orders.getVegetableNum();
                String taste = orders.getTaste();
                VegetableInformation vegetableInfo = orders.getVegetableInformation();
                String price = vegetableInfo.getPrice();
                String name = vegetableInfo.getName();
                totalAmount += count * Integer.parseInt(price);
                // 使用 formatter 来格式化输出
                formatter.format(format, name, price + "元", count + "份", taste);
                setTemporaryTotal(no,totalAmount);
            }
        }
        return result.toString();
    }

    // 获取订单列表
    public synchronized List<Orders> getOrders(int no) {
        return orderMap.getOrDefault(no, new ArrayList<>());
    }

    // 删除某个 no 的所有订单
    public synchronized void removeOrders(int no) {
        orderMap.remove(no);
    }

    // 删除某个 no 保存的订单
    public synchronized void removeSavedOrders(int no) {
        savedOrders.remove(no);
    }

    // 删除某个 no 保存的总价
    public synchronized void removeTotal(int no) {
        total.remove(no);
    }




    // 修改总价
    public synchronized void updateTotal(int no, int price) {
        Integer i = temporaryTotal.get(no);
        if (i != null) {
            i -= price;
            temporaryTotal.put(no, i);
        }
    }

    // 保存临时的总价
    public void setTemporaryTotal(int no, Integer total) {
        temporaryTotal.put(no,total);
    }

    // 获得临时的总价
    public Integer getTemporaryTotal(int no) {
        Integer i = temporaryTotal.get(no);
        if (i != null) {
            return i;
        }
        return 0;
    }

    // 获得保存后的总价
    public synchronized Integer getTotal(int no) {
        Integer i = total.get(no);
        if (i != null) {
            return i;
        }
        return 0;
    }

    // 保存总价 = 获得临时总价+历史总价
    public synchronized void setTotal(int no, int nowTotal) {
        total.put(no, nowTotal);
    }

    // 获取整个订单映射
    public synchronized Map<Integer, List<Orders>> getOrderMap() {
        return new HashMap<>(orderMap); // 返回副本以防止外部修改
    }

    // 清空所有订单
    public synchronized void clearOrders() {
        orderMap.clear();
    }
}
