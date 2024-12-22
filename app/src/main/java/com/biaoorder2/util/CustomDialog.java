package com.biaoorder2.util;

import static androidx.core.content.ContextCompat.startActivity;

import static com.biaoorder2.pool.ConstantPools.URL_GET_TABLE_NO;
import static com.biaoorder2.pool.ConstantPools.URL_INSERT_TABLE;
import static com.biaoorder2.pool.ConstantPools.URL_INSERT_VEGETABLE;
import static com.biaoorder2.pool.ConstantPools.URL_SELECT_BOOKING_PHONE;
import static com.biaoorder2.pool.ConstantPools.URL_UPDATE_DINNER;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.R;
import com.biaoorder2.activity.CheckoutActivity;
import com.biaoorder2.activity.OrderActivity;
import com.biaoorder2.adapter.LRTodoAdapter;
import com.biaoorder2.bean.EnrollInformation;
import com.biaoorder2.bean.HistoryBill;
import com.biaoorder2.databinding.AdapterTodoBinding;
import com.biaoorder2.manager.OrderManager;
import com.biaoorder2.bean.Orders;
import com.biaoorder2.bean.Table;
import com.biaoorder2.bean.VegetableInformation;
import com.biaoorder2.manager.TableManager;
import com.biaoorder2.manager.VegetableManager;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.Interface.OnOrderAddedListener;
import com.biaoorder2.Interface.OnOrderChangedListener;
import com.biaoorder2.Interface.OnResponseListener;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

import okhttp3.Response;

public class CustomDialog {
    // 大厅桌号
    public static int hallTableNum;
    public static VegetableManager vegetableManager = VegetableManager.getInstance();
    public static TableManager tableManager = TableManager.getInstance();

    public static AlertDialog.Builder builder;

    // 初始化AlertDialog.Builder
    public static AlertDialog.Builder initBuilder(Context mContext) {
        return new AlertDialog.Builder(mContext);
    }

    public static void showDialog(Context mContext, String state, int pos, List<Table> tableList) {
        hallTableNum = pos;
        builder = initBuilder(mContext);
        int total = tableList.get(hallTableNum - 1).getCurrentTotal();
        String currentOrder = tableList.get(hallTableNum - 1).getCurrentOrder();
        switch (state) {
            case "0":
                free(builder, pos, mContext);
                break;
            case "1":
                ordered(builder, pos, mContext);
                break;
            case "2":
                dinnering(builder, pos, mContext, currentOrder, total);
                break;
            default:
                break;
        }
    }

    public static void free(AlertDialog.Builder builder, int pos, Context mContext) {
        builder.setTitle("桌号: " + pos + " 空闲中...");
        builder.setPositiveButton("取消", (dialog, which) ->
                dialog.dismiss());
        builder.setNegativeButton("预定", (dialog, which) ->
                showBookingDialog(mContext, pos));
        builder.setNeutralButton("点餐", (dialog, which) ->
                startActivity(mContext, new Intent(mContext, OrderActivity.class), null));
        builder.create().show();
    }

    public static void ordered(AlertDialog.Builder builder, int pos, Context mContext) {
        builder.setTitle("桌号: " + pos + " 已预定...");
        final EditText nameInput = new EditText(mContext);
        nameInput.setHint("预订人手机号:");
        nameInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(nameInput);

        builder.setPositiveButton("取消", (dialog, which) ->
                dialog.dismiss());
        builder.setNeutralButton("确定", (dialog, which) -> {
            String phone = nameInput.getText().toString();

            // 判断是否能跳转至点餐的界面
            IsBookingTask isBookingTask = new IsBookingTask(pos);
            FutureTask<String> futureTask = new FutureTask<>(isBookingTask);
            new Thread(futureTask).start();

            // 在主线程中获取结果
            try {
                String isBookedPhone = futureTask.get(); // 这将会阻塞直到任务完成
                if (phone.equals(isBookedPhone)) {
                    Intent intent = new Intent(mContext, OrderActivity.class);
                    startActivity(mContext, intent, null);
                } else {
                    Toast.makeText(mContext, "该号码未预定,请重新输入!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(mContext, "请求超时...", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    public static void dinnering(AlertDialog.Builder builder, int pos, Context mContext, String currentOrder, int total) {
        builder.setTitle("桌号: " + pos + " 用餐中...");
        if (currentOrder != null) {
            currentOrder = currentOrder.replace("null", "");
        }
        builder.setMessage(currentOrder + "\n" + "总价格为: " + total + " 元");

        builder.setPositiveButton("取消", (dialog, which) ->
                dialog.dismiss());
        builder.setNegativeButton("结账", (dialog, which) -> {
            // 结账的时间，下单的列表，总价，支付方式，桌号，写入数据库
            // 设置餐桌为空闲状态，清空临时总价和订单
            startActivity(mContext, new Intent(mContext, CheckoutActivity.class), null);
        });
        builder.setNeutralButton("加菜", (dialog, which) -> {
            // 跳转至点餐的界面
            startActivity(mContext, new Intent(mContext, OrderActivity.class), null);
        });
        builder.create().show();
    }

    public static void showBookingDialog(Context mContext, int pos) {
        hallTableNum = pos;
        builder = initBuilder(mContext);
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
                jsonObject.put("currentTotal", 0);
                Response response = RequestUtils.post(URL_UPDATE_DINNER, jsonObject.toString());
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
                    ReToast.show((Activity) mContext, "服务器异常,请联系管理员!");
                    return;
                }
                ReToast.show((Activity) mContext, "预订成功!");
            } catch (JSONException | IOException e) {
                ReToast.show((Activity) mContext, "请求有误,请重试!");
            }
        }).start();

    }

    public static String getIsBooking(int no) {
        try {
            Response response = RequestUtils.get(URL_SELECT_BOOKING_PHONE, "no", String.valueOf(no));
            String result = "";
            String phone = null;
            if (response.body() != null) {
                result = response.body().string();
                response.close();
            }
            JSONObject data = new JSONObject(result);
            if (data.getString("code").equals("1")) {
                phone = data.getString("data");
                Log.d("BookingPhone", "getIsBooking: " + phone);
            }
            return phone;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    static class IsBookingTask implements Callable<String> {
        private final int pos;

        public IsBookingTask(int pos) {
            this.pos = pos;
        }

        @Override
        public String call() {
            return getIsBooking(pos);
        }
    }

    public static void showVegetable(Context mContext, VegetableInformation vegetable, OnOrderAddedListener onOrderAddedListener) {
        // 创建对话框构造器
        builder = initBuilder(mContext);
        // 加载自定义布局
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_vegetableinformation, null);
        // 设置自定义布局到对话框
        builder.setView(dialogView);

        ImageView imgVegetable = dialogView.findViewById(R.id.img_vegetable);
        TextView tvVegetable = dialogView.findViewById(R.id.tv_VegetableName);

        Glide.with(mContext).load(vegetable.getImageLink()).into(imgVegetable);
        tvVegetable.setText(vegetable.getName());
        RadioGroup radioGroup = dialogView.findViewById(R.id.r_group);
        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
        Button cancel = dialogView.findViewById(R.id.btn_cancel);
        Button add = dialogView.findViewById(R.id.btn_add);
        cancel.setOnClickListener(v -> dialog.dismiss());

        //点菜品的操作
        add.setOnClickListener(v -> {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                    String selectedText = selectedRadioButton.getText().toString();
                    // 将菜品加入购物车
                    OrderManager orderManager = OrderManager.getInstance();
                    orderManager.addOrder(hallTableNum, new Orders(hallTableNum, vegetable, 1, selectedText));

                    // 通知UI更新
                    if (onOrderAddedListener != null) {
                        onOrderAddedListener.onOrderAdded();
                    }
                    dialog.dismiss();
                    Toast.makeText(mContext, "已加入购物车", Toast.LENGTH_SHORT).show();
                }
        );
    }

    // 显示是否下单
    public static void showIsOrders(Context mContext, Consumer<Boolean> callback) {
        // 回调函数，将结果传递回调用者
        builder = initBuilder(mContext);
        builder.setTitle("桌号:" + hallTableNum + " 订单列表:\n");

        OrderManager orderManager = OrderManager.getInstance();

        // orderedInformation 加菜前的订单
        // orderInformation 当前的订单
        //saveOrder 保存当前的订单

        String orderedInformation = orderManager.getSavedOrder(hallTableNum);
        String orderInformation = orderManager.getOrderInformation(hallTableNum);
        Integer total = orderManager.getTemporaryTotal(hallTableNum);

        builder.setMessage(orderInformation + "\n" + "总价格为: " + total + " 元");

        builder.setPositiveButton("取消", (dialog, which) ->
                dialog.dismiss());
        builder.setNegativeButton("下单", (dialog, which) -> {
                    // 保存订单，并清空购物车，防止减餐，方便加餐和结账，设置餐桌的状态为用餐中，下单成功后回到大厅
                    orderManager.removeOrders(hallTableNum);
                    orderManager.setTotal(hallTableNum, total + orderManager.getTotal(hallTableNum));

                    // 加菜
                    if (!orderInformation.isEmpty() && orderedInformation != null) {
                        orderManager.saveOrder(hallTableNum, orderedInformation + orderInformation);
                        orderManager.setTemporaryTotal(hallTableNum, 0);
                        updateState(hallTableNum, "", "", "2",
                                (orderedInformation + orderInformation)
                                , orderManager.getTotal(hallTableNum)
                                , mContext, "下单成功!");
                        callback.accept(true);
                    } else if (orderInformation.isEmpty()) {
                        ReToast.show((Activity) mContext, "请先点餐!");
                        callback.accept(false);
                        // 第一次点餐
                    } else {
                        orderManager.saveOrder(hallTableNum, orderInformation);
                        orderManager.setTemporaryTotal(hallTableNum, 0);
                        updateState(hallTableNum, "", "", "2"
                                , orderInformation, orderManager.getTotal(hallTableNum), mContext, "下单成功!");
                        callback.accept(true);
                    }
                }
        );
        builder.create().show();
    }

    public static void updateState(int no, String name, String phone, String state, String currentOrder, int currentTotal, Context mContext, String reason) {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("no", no);
                jsonObject.put("name", name);
                jsonObject.put("phone", phone);
                jsonObject.put("state", state);
                jsonObject.put("currentOrder", currentOrder);
                jsonObject.put("currentTotal", currentTotal);
                Response response = RequestUtils.post(URL_UPDATE_DINNER, jsonObject.toString());
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
                if (code.equals("1")) {
                    tableManager.updateTable(new Table(no, name, phone, state, currentOrder, currentTotal));
                    ReToast.show((Activity) mContext, reason);
                }

            } catch (JSONException | IOException e) {
                ReToast.show((Activity) mContext, "下单失败,服务器异常...");
            }
        }).start();
    }

    // 显示历史订单
    public static void showHistoryBill(Context mContext, List<HistoryBill> historyBillList, int orderNo) {
        builder = initBuilder(mContext);
        builder.setTitle("订单详细信息");
        if (!historyBillList.isEmpty()) {
            for (int i = 0; i < historyBillList.size(); i++) {
                int getOrderNo = historyBillList.get(i).getOrderNo();
                if (getOrderNo == orderNo) {
                    builder.setMessage(historyBillList.get(i).toString());
                    builder.create().show();
                }

            }
        }
    }

    // 编辑餐桌
    public static void editTableDialog(Context mContext, String edit, OnResponseListener onResponseListener) {
        builder = initBuilder(mContext);
        builder.setTitle(edit);
        final EditText idInput = new EditText(mContext);
        idInput.setHint("请输入餐桌号:");
        idInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(idInput);

        builder.setPositiveButton("取消", (dialog, which) ->
                dialog.dismiss());
        builder.setNegativeButton("确定", (dialog, which) -> {
            String no = idInput.getText().toString().trim(); // 获取输入框中的桌号
            // 获得桌号，检查是否重复
            if (!no.isEmpty()) {
                isExistTable(Integer.parseInt(no), mContext, edit, onResponseListener);
                dialog.dismiss();
            } else {
                Toast.makeText(mContext, "请输入内容!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    public static void isExistTable(int no, Context mContext, String edit, OnResponseListener onResponseListener) {
        new Thread(() -> {
            try {
                Response response = RequestUtils.get(URL_GET_TABLE_NO, "no", String.valueOf(no));
                String result = "";
                if (response.body() != null) {
                    result = response.body().string();
                    response.close();
                }
                JSONObject data = new JSONObject(result);
                String code = data.getString("code");
                // 1是餐桌不存在， 0是存在
                // 判断是添加还是删除
                if (edit.equals("添加餐桌")) {
                    if (code.equals("1")) {
                        if (onResponseListener != null) {
                            addTable(no, mContext, onResponseListener);
                        }
                    } else ReToast.show((Activity) mContext, "餐桌已存在...");
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    // 加桌子
    public static void addTable(int no, Context mContext, OnResponseListener onResponseListener) {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("no", no);
                jsonObject.put("name", "null");
                jsonObject.put("phone", "null");
                jsonObject.put("state", "0");
                Response response = RequestUtils.post(URL_INSERT_TABLE, jsonObject.toString());
                String result = "";
                if (response.body() != null) {
                    result = response.body().string();
                    response.close();
                }
                JSONObject data = new JSONObject(result);
                if (data.getString("code").equals("1")) {
                    ReToast.show((Activity) mContext, "添加成功...");
                }
            } catch (IOException | JSONException e) {
                ReToast.show((Activity) mContext, "服务器出错qwq...");
            }
        }).start();
    }


    //添加加菜品弹窗
    public static void editVegetable(Context mContext, String edit, OnOrderChangedListener listener) {
        builder = initBuilder(mContext);
        builder.setTitle(edit);
        // 创建一个 LinearLayout 来包含多个 EditText
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameInput = new EditText(mContext);
        final EditText priceInput = new EditText(mContext);
        final EditText imageLinkInput = new EditText(mContext);
        final EditText categoryInput = new EditText(mContext);

        nameInput.setHint("菜品名:");
        priceInput.setHint("菜品价格(元):");
        imageLinkInput.setHint("菜品图片链接:");
        categoryInput.setHint("菜品类别:");

        // 将 EditText 添加到布局中
        layout.addView(nameInput);
        layout.addView(priceInput);
        layout.addView(imageLinkInput);
        layout.addView(categoryInput);

        // 将布局设置为 AlertDialog 的视图
        builder.setView(layout);

        builder.setPositiveButton("取消", (dialog, which) ->
                dialog.dismiss());
        builder.setNegativeButton("确定", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String price = priceInput.getText().toString().trim();
            String imageLink = imageLinkInput.getText().toString().trim();
            String category = categoryInput.getText().toString().trim();
            // 获得菜品信息，判断菜品信息是否完整，id是否重复
            if (name.isEmpty() || price.isEmpty() || imageLink.isEmpty() || category.isEmpty()) {
                Toast.makeText(mContext, "请输入完整的餐品信息!", Toast.LENGTH_SHORT).show();
            } else {
                addVegetable(name, price, imageLink, category, mContext, listener);
            }
        });

        builder.create().show();
    }


    public static void addVegetable(String name, String price, String imageLink, String category, Context mContext, OnOrderChangedListener listener) {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", name);
                jsonObject.put("price", price);
                jsonObject.put("imageLink", imageLink);
                jsonObject.put("category", category);
                Response response = RequestUtils.post(URL_INSERT_VEGETABLE, jsonObject.toString());
                String result = "";
                if (response.body() != null) {
                    result = response.body().string();
                    response.close();
                }
                JSONObject data = new JSONObject(result);

                if (data.getString("code").equals("1")) {
                    int id = Integer.parseInt(data.getString("data"));
                    ReToast.show((Activity) mContext, "添加成功...");
                    ((Activity) mContext).runOnUiThread(() -> {
                        vegetableManager = VegetableManager.getInstance();
                        // 更新数据
                        VegetableInformation vegetableInformation = new VegetableInformation(id, name, price, imageLink, 0, category);
                        // vegetableManager.addSingleVegetable(category, vegetableInformation);
                        vegetableManager.addVegetable(vegetableInformation);
                        listener.onOrderChanged(vegetableManager.getVegetableList());
                    });
                }
            } catch (JSONException | IOException e) {
                ReToast.show((Activity) mContext, "服务器出错qwq...");
            }
        }).start();
    }

    // 更新菜品信息
    public static void editMenu(Context mContext, VegetableInformation vegetableInformation, OnResponseListener listener) {
        builder = initBuilder(mContext);
        // 创建一个 LinearLayout 来包含多个 EditText
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_updatemenu, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.dialog_backgroud);
        }
        // 获取对话框中所有的 EditText
        EditText edit_name = dialogView.findViewById(R.id.edit_name);
        EditText edit_price = dialogView.findViewById(R.id.edit_pride);
        EditText edit_imageLink = dialogView.findViewById(R.id.edit_imageLink);
        EditText edit_sales = dialogView.findViewById(R.id.edit_sales);
        EditText edit_category = dialogView.findViewById(R.id.edit_category);

        Button btn_ok = dialogView.findViewById(R.id.dialog_ok);
        Button btn_cancel = dialogView.findViewById(R.id.dialog_cancel);

        // 设置初始值
        edit_name.setText(vegetableInformation.getName());
        edit_price.setText(vegetableInformation.getPrice());
        edit_imageLink.setText(vegetableInformation.getImageLink());
        edit_sales.setText(String.valueOf(vegetableInformation.getSales()));
        edit_category.setText(vegetableInformation.getCategory());

        btn_cancel.setOnClickListener(v -> dialog.dismiss());

        btn_ok.setOnClickListener(v -> {
            // 更新数据
            vegetableInformation.setName(edit_name.getText().toString());
            vegetableInformation.setPrice(edit_price.getText().toString());
            vegetableInformation.setImageLink(edit_imageLink.getText().toString());
            vegetableInformation.setSales(Integer.parseInt(edit_sales.getText().toString()));
            vegetableInformation.setCategory(edit_category.getText().toString());
            if (listener != null) {
                listener.onConfirmDelete();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    // 信息请求对话框
    public static void showTodo(Context mContext, List<EnrollInformation> enrollInformationList) {
        builder = initBuilder(mContext);
        // 创建一个 LinearLayout 来包含多个 EditText
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_todolist, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.dialog_backgroud);
        }
        // 提示布局
        LinearLayout liner = dialogView.findViewById(R.id.liner_hint);
        // 检查控件是否为 null
        // 根据列表内容进行处理
        if (enrollInformationList.isEmpty()) {
            liner.setVisibility(View.VISIBLE);
        } else {
            liner.setVisibility(View.GONE);
        }
        // 获取 RecyclerView 并设置布局和适配器
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        LRTodoAdapter lrTodoAdapter = new LRTodoAdapter(enrollInformationList, mContext);
        recyclerView.setAdapter(lrTodoAdapter);

        dialog.show();
    }


    // 信息处理对话框
    public static void showEvents(Context mContext, EnrollInformation enrollInformation, OnResponseListener onResponseListener) {

        builder = initBuilder(mContext);
        // 创建一个 LinearLayout 来包含多个 EditText
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_events, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.dialog_backgroud);
        }
        // 绑定控件
        TextView tvID = dialogView.findViewById(R.id.tv_id);
        TextView tvPassword = dialogView.findViewById(R.id.tv_password);
        TextView tvUsername = dialogView.findViewById(R.id.tv_username);
        TextView tvEventTime = dialogView.findViewById(R.id.tv_eventTime);
        TextView tv_hint = dialogView.findViewById(R.id.tv_hint);

        View view = dialogView.findViewById(R.id.v_view);

        Button btnOk = dialogView.findViewById(R.id.dialog_ok);
        Button btnCancel = dialogView.findViewById(R.id.dialog_cancel);


        //  初始化数值
        tvID.setText(enrollInformation.getId());
        tvPassword.setText(enrollInformation.getPassword());
        tvUsername.setText(enrollInformation.getUsername());
        tvEventTime.setText(enrollInformation.getEventTime());

        if (enrollInformation.getEventIsDone().equals("1")) {
            btnOk.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            tv_hint.setText("申请已通过!");
            tv_hint.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        }
        btnOk.setOnClickListener(v -> {
            // 确认提交
            if (onResponseListener != null) {
                onResponseListener.onConfirmDelete();
            }
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v ->
                dialog.dismiss());

        dialog.show();
    }


    // 确认是否删除对话框
    public static void showIs(Context mContext, String hint, OnResponseListener listener) {
        builder = initBuilder(mContext);
        builder.setTitle(hint);
        builder.setPositiveButton("取消", (dialog, which) ->
                dialog.dismiss());
        builder.setNegativeButton("确定", (dialog, which) -> {
            if (listener != null) {
                listener.onConfirmDelete();
            }
        });
        builder.create().show();
    }

    public static void isExit(Context mContext, Consumer<Boolean> callback) {
        builder = initBuilder(mContext);
        builder.setTitle("是否退出当前账户?");
        builder.setPositiveButton("取消", (dialog, which) ->
                dialog.dismiss());
        builder.setNegativeButton("确定", (dialog, which) ->
                callback.accept(true));
        builder.create().show();
    }
}
