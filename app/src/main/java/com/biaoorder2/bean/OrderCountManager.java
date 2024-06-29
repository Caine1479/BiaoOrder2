package com.biaoorder2.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class OrderCountManager {
    // 单例实例
    private static OrderCountManager instance;

    private final CopyOnWriteArrayList<OrderCountListener> listeners;
    // 存放桌号和菜品数量的 HashMap
    private final Map<Integer, Integer> orderCountMap;

    // 私有构造函数，防止外部实例化
    private OrderCountManager() {
        orderCountMap = new HashMap<>();
        listeners = new CopyOnWriteArrayList<>();
    }

    // 获取单例实例的方法
    public static synchronized OrderCountManager getInstance() {
        if (instance == null) {
            instance = new OrderCountManager();
        }
        return instance;
    }

    // 获取指定桌号的菜品数量
    public synchronized int getOrderCount(int no) {
        return orderCountMap.getOrDefault(no, 0);
    }

    // 添加菜品数量
    public synchronized void addOrderCount(int no, int count) {
        int currentCount = orderCountMap.getOrDefault(no, 0);
        orderCountMap.put(no, currentCount + count);
    }

    // 删除菜品数量
    public synchronized void removeOrderCount(int no, int count) {
        int currentCount = orderCountMap.getOrDefault(no, 0);
        int newCount = Math.max(0, currentCount - count);
        orderCountMap.put(no, newCount);
    }

    // 清空指定桌号的菜品数量
    public synchronized void clearOrderCount(int no) {
        orderCountMap.remove(no);
    }

    // 注册监听器 观察者模式
    public void addListener(OrderCountListener listener) {
        listeners.add(listener);
    }

    // 取消注册监听器
    public void removeListener(OrderCountListener listener) {
        listeners.remove(listener);
    }

    // 通知监听器
    private void notifyListeners(int tableNo) {
        for (OrderCountListener listener : listeners) {
            listener.onOrderCountChanged(tableNo);
        }
    }

    // 监听器接口
    public interface OrderCountListener {
        void onOrderCountChanged(int tableNo);
    }
}

