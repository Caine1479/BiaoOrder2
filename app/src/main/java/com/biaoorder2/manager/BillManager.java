package com.biaoorder2.manager;

import static com.biaoorder2.pool.ConstantPools.URL_GET_HISTORY_BILL;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.biaoorder2.bean.HistoryBill;
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

public class BillManager {
    private static BillManager instance;
    private List<HistoryBill> historyBillList;

    private BillManager() {
        historyBillList = new ArrayList<>();
    }

    public static synchronized BillManager getInstance() {
        if (instance == null) {
            instance = new BillManager();
        }
        return instance;
    }

    public List<HistoryBill> getHistoryBillList() {
        return historyBillList;
    }

    public void setHistoryBillList(List<HistoryBill> historyBillList) {
        this.historyBillList = historyBillList;
    }

    public void getHistoryBillList_Http(Context context) {
        RequestUtils.get1(URL_GET_HISTORY_BILL, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                RequestUtils.handleError(context);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                List<HistoryBill> historyBills = new ArrayList<>();
                // 获取handleGetResponse返回的结果
                String result = RequestUtils.handleGetResponse(response, context);
                if (result != null) {
                    try {
                        JSONObject data = new JSONObject(result);
                        JSONArray jsonArray = data.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int no = jsonObject.getInt("no");
                            String vegetables = jsonObject.getString("vegetables");
                            String price = jsonObject.getString("price");
                            String date = jsonObject.getString("date");
                            String payment = jsonObject.getString("payment");
                            int orderNo = jsonObject.getInt("orderNo");
                            historyBills.add(new HistoryBill(no, vegetables, price, date, payment, orderNo));
                        }
                        historyBillList.clear();
                        setHistoryBillList(historyBills);
                    } catch (JSONException e) {
                        ReToast.show((Activity) context, "服务器出错,请联系管理员...");
                    }
                }
            }
        });
    }
}
