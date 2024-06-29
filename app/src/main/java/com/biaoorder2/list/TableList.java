package com.biaoorder2.list;

import static com.biaoorder2.pool.ConstantPools.URL_GET_TABLES;

import com.biaoorder2.bean.Table;
import com.biaoorder2.util.RequestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class TableList {
    public List<Table> tableList;
    private static TableList instance;

    // 私有构造函数，防止外部实例化
    private TableList() {
        tableList = new ArrayList<>();
    }

    // 公共的静态方法用于获取单例实例
    public static synchronized TableList getInstance() {
        if (instance == null) {
            instance = new TableList();
        }
        return instance;
    }

    // 增加一个Table到集合中
    public void addTable(Table table) {
        tableList.add(table);
    }

}
