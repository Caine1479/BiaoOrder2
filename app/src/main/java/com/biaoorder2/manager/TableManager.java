package com.biaoorder2.manager;

import static com.biaoorder2.pool.ConstantPools.URL_GET_TABLES;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.biaoorder2.bean.Table;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.RequestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TableManager {
    private static TableManager instance;
    private List<Table> tableList;

    private TableManager() {
        tableList = new ArrayList<>();
    }

    public static synchronized TableManager getInstance() {
        if (instance == null) {
            instance = new TableManager();
        }
        return instance;
    }

    public List<Table> getTableList() {
        return tableList;
    }

    // 更新数据
    public void updateTable(Table table){
        for (int i = 0; i <tableList.size(); i++) {
            if (tableList.get(i).getNo() == table.getNo()){
                tableList.set(i,table);
                break;
            }
        }
    }

    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }

    public void getTableList_Http(Context context) {
        RequestUtils.get1(URL_GET_TABLES, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                RequestUtils.handleError(context);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                List<Table> tables = new ArrayList<>();
                // 获取handleGetResponse返回的结果
                String result = RequestUtils.handleGetResponse(response, context);
                if (result != null) {
                    try {
                        JSONObject data = new JSONObject(result);
                        JSONArray jsonArray = data.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonTable = jsonArray.getJSONObject(i);
                            int no = jsonTable.getInt("no");
                            String name = jsonTable.getString("name");
                            String phone = jsonTable.getString("phone");
                            String state = jsonTable.getString("state");
                            String currentOrder = jsonTable.getString("currentOrder");
                            int currentTotal = jsonTable.getInt("currentTotal");
                            tables.add(new Table(no, name, phone, state, currentOrder, currentTotal));
                        }
                        // 确保 UI 更新在主线程
                        tableList.clear();
                        setTableList(tables);

                    } catch (JSONException e) {
                        ReToast.show((Activity) context, "服务器出错,请联系管理员...");
                    }
                }
            }
        });

    }
}
