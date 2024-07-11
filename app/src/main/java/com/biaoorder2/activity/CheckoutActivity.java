package com.biaoorder2.activity;

import static com.biaoorder2.pool.ConstantPools.URL_INSERT_HISTORY;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.biaoorder2.R;
import com.biaoorder2.bean.OrderManager;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.CustomDialog;
import com.biaoorder2.util.RequestUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import okhttp3.Response;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        initView();
    }

    public void initView() {
        int hallTableNum = CustomDialog.hallTableNum;
        OrderManager orderManager = OrderManager.getInstance();
        TextView tvOrderList = findViewById(R.id.tv_orderList);
        TextView tvTotal = findViewById(R.id.tv_total);
        Button pay = findViewById(R.id.btn_pay);

        RadioGroup radioGroup = findViewById(R.id.r_payment);

        tvOrderList.setText(orderManager.getSavedOrder(hallTableNum));
        tvTotal.setText(String.valueOf(orderManager.getTotal(hallTableNum)));
        String savedOrder = OrderManager.getInstance().getSavedOrder(hallTableNum);
        // 提交订单跳转支付页面
        pay.setOnClickListener(v -> {
            RadioButton selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
            String payment = selectedRadioButton.getText().toString();
            // 将订单写入历史订单，包括所有的历史信息，更改餐桌状态，清空餐桌的信息例如，餐桌点餐的菜品，orders
            saveHistoryOrders(hallTableNum, savedOrder, tvTotal.getText().toString(), payment);
            CustomDialog.updateState(hallTableNum, "null", "null", "0", this, "支付成功");
            orderManager.removeOrders(hallTableNum);
            orderManager.removeSavedOrders(hallTableNum);
            orderManager.removeTotal(hallTableNum);
        });
    }

    // 结账后将菜单写入历史菜单
    public void saveHistoryOrders(int no, String savedOrder, String price, String payment) {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("no", no);
                jsonObject.put("vegetables", savedOrder);
                jsonObject.put("price", price);
                jsonObject.put("payment", payment);
                Response response = RequestUtils.post(URL_INSERT_HISTORY, jsonObject.toString());
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
                    return;
                }
                // 回到大厅界面
                startActivity(new Intent(this, HallActivity.class));
                finish();
            } catch (IOException e) {
                ReToast.show(this, "网络错误，请刷新重新试...");
            } catch (JSONException e) {
                ReToast.show(this, "服务器出错qwq...");
            }
        }).start();
    }
}