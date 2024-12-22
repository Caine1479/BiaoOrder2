package com.biaoorder2.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.biaoorder2.adapter.LROrderAdapter;
import com.biaoorder2.adapter.LROrderCategoryAdapter;
import com.biaoorder2.bean.Category;
import com.biaoorder2.manager.CategoryManager;
import com.biaoorder2.manager.OrderManager;
import com.biaoorder2.bean.VegetableInformation;
import com.biaoorder2.databinding.ActivityOrderBinding;
import com.biaoorder2.fragment.ShoppingCarDialogFragment;
import com.biaoorder2.manager.VegetableManager;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.CustomDialog;
import com.biaoorder2.Interface.OnCategoryClickListener;
import com.biaoorder2.Interface.OnOrderAddedListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.List;


public class OrderActivity extends AppCompatActivity implements OnOrderAddedListener, OnCategoryClickListener {
    public LROrderAdapter orderAdapter;
    public LROrderCategoryAdapter orderCategoryAdapter;
    public RecyclerView orderRecyclerView, orderSearchRecyclerView, categoryRecyclerView;
    public FloatingActionButton shoppingCar, placeOrders;
    private TextView tvVegetableCount;
    private ActivityOrderBinding binding;
    public VegetableManager vegetableManager = VegetableManager.getInstance();
    public CategoryManager categoryManager = CategoryManager.getInstance();
    public List<VegetableInformation> vegetableList;
    public List<Category> categoryList;
    public List<VegetableInformation> searchVegetableList = new ArrayList<>();
    private Handler mainHandler;
    public SearchView searchView;
    public String result;
    public MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        binding.refreshLayout2.setOnRefreshListener(this::refreshData);
        setContentView(binding.getRoot());
        initView();

        // 监听 orderRecyclerView 的滑动状态
        isOnScrollListener(orderRecyclerView);
        // 监听 orderRecyclerView 的滑动状态
        isOnScrollListener(orderSearchRecyclerView);

        EditText editText = searchView.getEditText();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                result = "";
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                result = editText.getText().toString();
                searchVegetableList.clear();
                // 匹配订单号
                for (int i = 0; i < vegetableList.size(); i++) {
                    if (String.valueOf(vegetableList.get(i).getName()).contains(result)) {
                        searchVegetableList.add(vegetableList.get(i));
                    }
                }
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable s) {
                orderAdapter.setVegetableList(searchVegetableList);
                orderSearchRecyclerView.setAdapter(orderAdapter);
                orderAdapter.notifyDataSetChanged();
            }
        });

        shoppingCar.setOnClickListener(v -> {
            // 打开购物车
            new ShoppingCarDialogFragment().show(this.getSupportFragmentManager(), "打开购物车");
        });
        //
        placeOrders.setOnClickListener(v -> {
            // 下单按钮
            CustomDialog.showIsOrders(OrderActivity.this, isOrders -> {
                if (isOrders) {
                    // 回到大厅界面
                    startActivity(new Intent(this, HallActivity.class));
                    finish();
                }
            });
        });

        toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed());

    }

    @SuppressLint("NotifyDataSetChanged")
    public void initView() {
        searchView = binding.searchOrderView;
        mainHandler = new Handler(Looper.getMainLooper());
        orderRecyclerView = binding.recyclerViewVegetableMenu;
        orderRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        tvVegetableCount = binding.tvCard;
        toolbar = binding.topVegetableBar;

        // 获得总菜单
        vegetableList = vegetableManager.getVegetableList();
        // 将菜单排序分类
        vegetableManager.classifyList(vegetableList);


        categoryRecyclerView = binding.recyclerViewCategory;
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // 获得菜品栏
        categoryList = categoryManager.getCategoryList();

        orderCategoryAdapter = new LROrderCategoryAdapter(this, categoryList, this);
        categoryRecyclerView.setAdapter(orderCategoryAdapter);

        orderAdapter = new LROrderAdapter(vegetableList, this);
        orderRecyclerView.setAdapter(orderAdapter);
        placeOrders = binding.btnPlaceOrders;
        shoppingCar = binding.btnFloatingAction;
        // 更新菜品数量
        updateVegetableCount(CustomDialog.hallTableNum);

        orderSearchRecyclerView = binding.recyclerViewSearchOrder;
        orderSearchRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

    }

    // 更新指定桌号的订单数量
    public void updateVegetableCount(int tableNo) {
        String orderCount = OrderManager.getInstance().getAllTableNum(tableNo);
        runOnUiThread(() ->
                tvVegetableCount.setText(orderCount));
    }

    private void refreshData() {
        vegetableList = vegetableManager.getVegetableList();

        //
        // 模拟刷新操作，通常是网络请求数据等
        new Handler().postDelayed(() -> {
            // 刷新完成后停止动画
            mainHandler.post(() ->
                    binding.refreshLayout2.setRefreshing(false));
            ReToast.show(this, "刷新完成");
        }, 500); // 延时1秒，模拟网络请求时间
    }

    @Override
    public void onOrderAdded() {
        updateVegetableCount(CustomDialog.hallTableNum);
    }


    public void isOnScrollListener(RecyclerView recyclerView) {
        // 初始化 SwipeRefreshLayout
        SwipeRefreshLayout swipeRefreshLayout = binding.refreshLayout2;
        // 设置下拉刷新监听器
        // 触发刷新操作
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView orderRecyclerView, int dx, int dy) {
                // 只有当滑动到顶部时才启用 SwipeRefreshLayout
                boolean isAtTop = !orderRecyclerView.canScrollVertically(-1);
                swipeRefreshLayout.setEnabled(isAtTop);
            }
        });
    }
    // 菜品栏点击接口，切换list集合
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void OnCategoryClick(String category) {
        orderAdapter.setVegetableList(vegetableManager.getVegteableList(category));
        orderAdapter.notifyDataSetChanged();
    }
}