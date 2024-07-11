package com.biaoorder2.activity;

import static com.biaoorder2.pool.ConstantPools.URL_GET_VEGETABLE;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.R;
import com.biaoorder2.adapter.LROrderAdapter;
import com.biaoorder2.bean.OrderManager;
import com.biaoorder2.bean.VegetableInformation;
import com.biaoorder2.databinding.ActivityOrderBinding;
import com.biaoorder2.fragment.ShoppingCarDialogFragment;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.CustomDialog;
import com.biaoorder2.util.OnOrderAddedListener;
import com.biaoorder2.util.RequestUtils;
import com.biaoorder2.util.setOnClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Response;


public class OrderActivity extends AppCompatActivity implements OnOrderAddedListener {

    public LROrderAdapter adapter;
    public RecyclerView recyclerView;
    public FloatingActionButton shoppingCar;
    public FloatingActionButton placeOrders;
    private TextView tvVegetableCount;
    public List<VegetableInformation> vegetableList = new ArrayList<>();
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void initView() {
        recyclerView = findViewById(R.id.recyclerView_vegetableMenu);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        tvVegetableCount = findViewById(R.id.tv_card);
        // 创建 setOnClickListener 实例
        setOnClickListener clickListener = pos -> CustomDialog.showVegetable(this, vegetableList.get(pos), this);
        adapter = new LROrderAdapter(vegetableList, clickListener, this);
        recyclerView.setAdapter(adapter);
        placeOrders = findViewById(R.id.btn_placeOrders);
        shoppingCar = findViewById(R.id.btn_floatingAction);
        shoppingCar.setOnClickListener(v -> {
            // 打开购物车
            new ShoppingCarDialogFragment().show(this.getSupportFragmentManager(), "打开购物车");
        });
        //
        placeOrders.setOnClickListener(v -> {
            // 下单按钮
            CustomDialog.showIsOrders(OrderActivity.this);
        });
        getVegetableList();
        updateVegetableCount(CustomDialog.hallTableNum);
    }

    // 更新指定桌号的订单数量
    public void updateVegetableCount(int tableNo) {
        String orderCount = OrderManager.getInstance().getAllTableNum(tableNo);
        runOnUiThread(() ->
                tvVegetableCount.setText(orderCount));
    }

    public void getVegetableList() {
        new Thread(() -> {
            try {
                Response response = RequestUtils.get(URL_GET_VEGETABLE);
                if (!response.isSuccessful()) {
                    mainHandler.post(() -> ReToast.show(this, "请求失败,请检查网络..."));
                    return;
                }
                String result = "";
                if (response.body() != null) {
                    result = response.body().string();
                    response.close();
                }
                JSONObject data = new JSONObject(result);
                if (data.getString("code").equals("1")) {
                    JSONArray jsonArray = data.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        String price = jsonObject.getString("price");
                        String imageLink = jsonObject.getString("imageLink");
                        vegetableList.add(new VegetableInformation(id, name, price, imageLink));
                    }
                } else {
                    mainHandler.post(() -> ReToast.show(this, "数据获取失败..."));
                }
            } catch (IOException | JSONException e) {
                mainHandler.post(() -> ReToast.show(this, "请求失败,请检查网络..."));
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void onOrderAdded() {
        updateVegetableCount(CustomDialog.hallTableNum);
    }
}