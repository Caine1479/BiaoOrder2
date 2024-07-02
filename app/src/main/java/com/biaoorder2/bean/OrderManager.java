package com.biaoorder2.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManager {
    // 存储订单的 HashMap
    private Map<Integer, List<Orders>> orderMap;
    // 内部静态类用于实现单例模式
    private static class SingletonHelper {
        private static final OrderManager INSTANCE = new OrderManager();
    }

    // 私有的构造函数确保无法从外部实例化
    private OrderManager() {
        orderMap = new HashMap<>();
    }

    // 获取单例实例的方法
    public static OrderManager getInstance() {
        return SingletonHelper.INSTANCE;
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
    public synchronized String getAllTableNum(int no){
        List<Orders> ordersList = orderMap.get(no);
        int sum = 0;
        if (ordersList != null){
            for (int i = 0; i < ordersList.size(); i++) {
                int count = ordersList.get(i).getVegetableNum();
                sum += count;
            }
        }
       return String.valueOf(sum);
    }

    // 添加指定餐桌的指定菜品的数量加一
    public synchronized void addOrderCount(int no){

    }

    // 获取订单列表
    public synchronized List<Orders> getOrders(int no) {
        return orderMap.getOrDefault(no, new ArrayList<>());
    }


    // 删除某个 no 的所有订单
    public synchronized void removeOrders(int no) {
        orderMap.remove(no);
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
