package com.biaoorder2.util;

import static androidx.core.content.ContextCompat.startActivity;
import static com.biaoorder2.pool.ConstantPools.URL_SELECT_BOOKING_PHONE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.biaoorder2.R;
import com.biaoorder2.activity.OrderActivity;
import com.biaoorder2.bean.OrderCountManager;
import com.biaoorder2.bean.OrderManager;
import com.biaoorder2.bean.Orders;
import com.biaoorder2.bean.VegetableInformation;
import com.biaoorder2.pool.ConstantPools;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class CustomDialog {

    public static String phone = null;

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        CustomDialog.phone = phone;
    }

    // 大厅桌号
    public static int hallTableNum;

    public static void showDialog(Context mContext, String state, String title, int pos) {
        hallTableNum = pos;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title + pos);
        switch (state) {
            case "0":
                builder.setMessage("餐桌空闲中...");
                builder.setPositiveButton("取消", (dialog, which) ->
                        dialog.dismiss());
                builder.setNegativeButton("预定", (dialog, which) ->
                        showBookingDialog(mContext, pos));
                builder.setNeutralButton("点餐", (dialog, which) -> {
                    Intent intent = new Intent(mContext, OrderActivity.class);
                    startActivity(mContext, intent, null);
                    //((Activity)mContext).finish();

                });
                builder.create().show();
                break;
            case "1":
                builder.setMessage("餐桌已预定...");
                final EditText nameInput = new EditText(mContext);
                nameInput.setHint("预订人手机号:");
                nameInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                builder.setView(nameInput);

                builder.setPositiveButton("取消", (dialog, which) ->
                        dialog.dismiss());
                builder.setNeutralButton("确定", (dialog, which) -> {
                    String phone = nameInput.getText().toString();
                    // 判断是否能跳转至点餐的界面
                    getIsBooking(pos);
                    if (phone.equals(CustomDialog.getPhone())) {
                        Intent intent = new Intent(mContext, OrderActivity.class);
                        startActivity(mContext, intent, null);
                    } else {
                        Toast.makeText(mContext, "该号码未预定,请重新输入!", Toast.LENGTH_SHORT).show();
                    }

                });
                builder.create().show();
                break;
            case "2":
                builder.setMessage("该餐桌用餐中...");
                builder.setPositiveButton("取消", (dialog, which) ->
                        dialog.dismiss());
                builder.setNegativeButton("结账", (dialog, which) -> {
                });
                builder.setNeutralButton("加菜", (dialog, which) -> {
                    // 跳转至点餐的界面
                });
                builder.create().show();
                break;
            default:
                break;
        }
    }

    public static void showBookingDialog(Context mContext, int pos) {
        hallTableNum = pos;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("预定中...");
        final EditText phoneInput = new EditText(mContext);
        phoneInput.setHint("请输入您的手机号:");
        phoneInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(phoneInput);
        builder.setPositiveButton("取消", (dialog, which) ->
                dialog.dismiss());
        builder.setNeutralButton("确定", null);

        AlertDialog dialog = builder.create();
        dialog.show();
        // 获取PositiveButton并设置点击事件
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(v -> {
            String phone = phoneInput.getText().toString().trim(); // 获取输入框中的手机号码
            // 检查输入是否为数字
            if (!phone.isEmpty()) {
                Booking(mContext, pos, phone);
                // 发送请求，update餐桌数据
                dialog.dismiss();

            } else {
                Toast.makeText(mContext, "请输入手机号!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void Booking(Context mContext, int no, String phone) {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("no", no);
                jsonObject.put("name", "null");
                jsonObject.put("phone", phone);
                jsonObject.put("state", "1");
                Response response = RequestUtils.post(ConstantPools.URL_UPDATE_DINNER, jsonObject.toString());
                if (!response.isSuccessful()) {
                    throw new IOException();
                }
                String result = "";
                if (response.body() != null) {
                    result = response.body().string();
                }
                response.close();
                JSONObject data = new JSONObject(result);
                String code = data.getString("code");
                if (!code.equals("1")) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(mContext, "服务器异常,请联系管理员!", Toast.LENGTH_SHORT).show());
                    return;
                }
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(mContext, "预订成功!", Toast.LENGTH_SHORT).show());
            } catch (JSONException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(mContext, "请求有误,请重试!", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(mContext, "服务器异常,请联系管理员!", Toast.LENGTH_SHORT).show());
            }
        }).start();

    }

    public static void getIsBooking(int no) {
        new Thread(() -> {
            try {
                Response response = RequestUtils.get(URL_SELECT_BOOKING_PHONE, no);
                String result = "";
                String phone;
                if (response.body() != null) {
                    result = response.body().string();
                    response.close();
                }
                JSONObject data = new JSONObject(result);
                if (data.getString("code").equals("1")) {
                    phone = data.getString("data");
                    setPhone(phone);
                    Log.d("BookingPhone", "getIsBooking: " + phone);
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }

    public static void showVegetable(Context mContext, VegetableInformation vegetable, OnOrderAddedListener listener) {
        // 创建对话框构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // 加载自定义布局
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_vegetableinformation, null);
        // 设置自定义布局到对话框
        builder.setView(dialogView);

        ImageView imgVegetable = dialogView.findViewById(R.id.img_vegetable);
        TextView tvVegetable = dialogView.findViewById(R.id.tv_VegetableName);
        RadioGroup radioGroup = dialogView.findViewById(R.id.r_group);

        Glide.with(mContext).load(vegetable.getImageLink()).into(imgVegetable);
        tvVegetable.setText(vegetable.getName());

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
        Button cancel = dialogView.findViewById(R.id.btn_cancel);
        Button add = dialogView.findViewById(R.id.btn_add);
        cancel.setOnClickListener(v -> dialog.dismiss());

        // 添加菜品的操作
        add.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
            String selectedText = selectedRadioButton.getText().toString();
            // 将菜品加入购物车
            OrderManager orderManager = OrderManager.getInstance();
            orderManager.addOrder(hallTableNum, new Orders(hallTableNum, vegetable, 1, selectedText));
            OrderCountManager.getInstance().addOrderCount(CustomDialog.hallTableNum,1);

            // 通知UI更新
            listener.onOrderAdded();
            dialog.dismiss();
            Toast.makeText(mContext, "已加入购物车", Toast.LENGTH_SHORT).show();

                }
        );
    }
}
