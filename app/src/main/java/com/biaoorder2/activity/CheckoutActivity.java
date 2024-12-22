package com.biaoorder2.activity;

import static com.biaoorder2.pool.ConstantPools.URL_INSERT_HISTORY;
import static com.biaoorder2.pool.ConstantPools.URL_SAVE_SALES;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.biaoorder2.R;
import com.biaoorder2.manager.OrderManager;
import com.biaoorder2.bean.sale;
import com.biaoorder2.databinding.ActivityCheckoutBinding;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.CustomDialog;
import com.biaoorder2.util.RequestUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;


import okhttp3.Response;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    public void initView() {
        int hallTableNum = CustomDialog.hallTableNum;
        OrderManager orderManager = OrderManager.getInstance();
        TextView tvTotal = binding.tvTotal;

        // 获得菜品的数量和名称信息
        String orders = orderManager.getSavedOrder(hallTableNum).replace("null", "");
        binding.tvOrderList.setText(orders);
        tvTotal.setText(String.valueOf(orderManager.getTotal(hallTableNum)));


        // 提交订单跳转支付页面
        binding.btnPay.setOnClickListener(v -> {
            RadioButton selectedRadioButton = findViewById(binding.rPayment.getCheckedRadioButtonId());
            String payment = selectedRadioButton.getText().toString();
            // 将订单写入历史订单，包括所有的历史信息，更改餐桌状态，清空餐桌的信息例如，餐桌点餐的菜品，orders
            saveHistoryOrders(hallTableNum, orders, tvTotal.getText().toString(), payment, isSave -> {
                if (isSave) {
                    CustomDialog.updateState(hallTableNum, "null", "null", "0", null, 0, this, "支付成功");
                    orderManager.removeOrders(hallTableNum);
                    orderManager.removeSavedOrders(hallTableNum);
                    orderManager.removeTotal(hallTableNum);
                    saveSales(orders);
                    // 回到大厅界面
                    startActivity(new Intent(this, HallActivity.class));
                    ReToast.show(this, "结账完成!");
                    finish();
                }
            });
        });
    }


    // 获得菜品的数量和名称信息
    public void saveSales(String saveOrders) {
        // 按换行符分割成组
        String[] groups = saveOrders.split("\n");
        ArrayList<sale> salesList = new ArrayList<>();
        for (String group : groups) {
            String[] parts = group.trim().split("\\s+"); // 去除首尾空格，并根据空格分割

            // 确保数组的长度为4
            if (parts.length == 4) {
                String name = parts[0];
                // 提取数量部分，并去掉“份”字
                String countWithUnit = parts[2];
                String sales = countWithUnit.replace("份", ""); // 移除“份”字

                salesList.add(new sale(name, Integer.parseInt(sales)));
            }
        }
        Log.d("TAG", "saveSales: " + salesList);
        if (!salesList.isEmpty()) {
            String json = new Gson().toJson(salesList);
            new Thread(() -> {
                try {
                    Response response = RequestUtils.post(URL_SAVE_SALES, json);
                    if (!response.isSuccessful()) {
                        ReToast.show(this, "服务器未响应,请联系管理员!");
                    }
                    String result = "";
                    if (response.body() != null) {
                        result = response.body().string();
                    }
                    response.close();
                    JSONObject data = new JSONObject(result);
                    String code = data.getString("code");
                    if (!code.equals("1")) {
                        ReToast.show(this, "");
                    }
                } catch (IOException | JSONException e) {
                    ReToast.show(this, "网络错误，请刷新重新试...");
                }
            }).start();
        }
    }

    // 结账后将菜单写入历史菜单
    public void saveHistoryOrders(int no, String savedOrder, String price, String payment, Consumer<Boolean> callback) {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("no", no);
                jsonObject.put("vegetables", savedOrder);
                jsonObject.put("price", price);
                jsonObject.put("payment", payment);
                Response response = RequestUtils.post(URL_INSERT_HISTORY, jsonObject.toString());
                if (!response.isSuccessful()) {
                    callback.accept(false);
                }
                String result = "";
                if (response.body() != null) {
                    result = response.body().string();
                }
                response.close();
                JSONObject data = new JSONObject(result);
                String code = data.getString("code");
                if (!code.equals("1")) {
                    ReToast.show(this, "");
                    return;
                }
                callback.accept(true);
            } catch (IOException | JSONException e) {
                ReToast.show(this, "网络错误，请刷新重新试...");
            }
        }).start();
    }
}