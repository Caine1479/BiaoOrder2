package com.biaoorder2.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.Interface.OnOrderChangedListener;
import com.biaoorder2.R;
import com.biaoorder2.adapter.LRMenuAdapter;
import com.biaoorder2.adapter.SidebarAdapter;
import com.biaoorder2.bean.Category;
import com.biaoorder2.bean.VegetableInformation;
import com.biaoorder2.databinding.ActivityMenuBinding;
import com.biaoorder2.manager.CategoryManager;
import com.biaoorder2.manager.VegetableManager;
import com.biaoorder2.Interface.SectionCallback;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.CustomDialog;
import com.biaoorder2.util.SectionItemDecoration;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MenuActivity extends AppCompatActivity implements SectionCallback, OnOrderChangedListener {

    public ActivityMenuBinding binding;
    public MaterialToolbar toolbar;
    public LRMenuAdapter menuAdapter;
    public SidebarAdapter sidebarAdapter;
    public SearchView searchView;
    public VegetableManager vegetableManager = VegetableManager.getInstance();
    public CategoryManager categoryManager = CategoryManager.getInstance();
    public RecyclerView recyclerViewMenu, recyclerViewSideBar, menuSearchRecyclerView;
    public SectionItemDecoration sectionItemDecoration;
    public List<VegetableInformation> vegetableList;
    public List<Category> categoryList;
    public List<VegetableInformation> searchVegetableList = new ArrayList<>();
    public String result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        // 返回上一级
        toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed());

        // 工具栏
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.edit_set){
                showPopupMenu(findViewById(item.getItemId()));
                return true;
            }
            return false;
        });

        // 菜单的滚动监听
        recyclerViewMenu.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                // 获取第一个可见项的位置和对应的标题 返回第一个可见span的items的位置
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                if (firstVisiblePosition == RecyclerView.NO_POSITION) return;

                String newTopHeader = vegetableList.get(firstVisiblePosition).getCategory();

                Log.d("newTopHeader", "onScrolled: " + newTopHeader);
                // 只有在标题发生变化时，才更新侧边栏
                String currentTopHeader = ""; //当前标题

                if (!newTopHeader.equals(currentTopHeader)) {
                    currentTopHeader = newTopHeader; // 更新当前顶部标题
                    sidebarAdapter.setSelectedPosition(sidebarAdapter.getSelectedPosition(currentTopHeader));
                }
            }
        });

        recyclerViewSideBar.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSideBar.setAdapter(sidebarAdapter);

        // 搜索框
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
                menuAdapter.setVegetableList(searchVegetableList);
                menuSearchRecyclerView.setAdapter(menuAdapter);
                menuAdapter.notifyDataSetChanged();
            }
        });
    }

    // 绑定视图
    public void initView() {
        toolbar = binding.topMenuBar;
        recyclerViewMenu = binding.recyclerviewMenu;
        recyclerViewSideBar = binding.recyclerviewSidebar;
        searchView = binding.searchMenuView;

        vegetableList = vegetableManager.getVegetableList();
        vegetableManager.classifyList(vegetableList);

        menuAdapter = new LRMenuAdapter(this, vegetableList);

        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMenu.setAdapter(menuAdapter);

        initViewItemDecoration();
        recyclerViewMenu.addItemDecoration(sectionItemDecoration);

        // 获得列表
        categoryList = categoryManager.classifyCategory(vegetableList);


        // 设置侧边栏的recyclerViewSideBar
        sidebarAdapter = new SidebarAdapter(categoryList, this, category -> {
            int targetPosition = findPositionForCategory(category);
            if (targetPosition >= 0) {  // 检查目标位置是否有效
                ((LinearLayoutManager) Objects.requireNonNull(recyclerViewMenu.getLayoutManager()))
                        .scrollToPositionWithOffset(targetPosition, 0);
            }
        });

        menuSearchRecyclerView = binding.recyclerViewSearchOrder;
        menuSearchRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
    }

    // 添加自定义的 ItemDecoration
    public void initViewItemDecoration() {
        sectionItemDecoration = new SectionItemDecoration(this, position -> {
            if (position == 0) {
                return true;
            }
            // 获取当前和前一个项的分类
            String currentCategory = vegetableList.get(position).getCategory();
            String previousCategory = vegetableList.get(position - 1).getCategory();
            return !currentCategory.equals(previousCategory);
        }, vegetableList);
    }

    // 返回菜品种类的位置
    private int findPositionForCategory(String category) {
        for (int i = 0; i < vegetableList.size(); i++) {
            if (vegetableList.get(i).getCategory().equals(category)) {
                return i;
            }
        }
        return -1; // 如果未找到则返回 -1
    }

    // 菜单编辑框
    @SuppressLint("NotifyDataSetChanged")
    private void showPopupMenu(View anchorView) {
        AtomicBoolean isDeleteMode = new AtomicBoolean(false);
        // 创建 PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        // 绑定菜单资源
        popupMenu.getMenuInflater().inflate(R.menu.overflow2, popupMenu.getMenu());

        // 设置菜单项点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            // 添加菜品事件
            if (item.getItemId() == R.id.add_vegetable) {
                CustomDialog.editVegetable(this, "添加菜品",this);
                return true;
            }
            if (item.getItemId() == R.id.delete_vegetable) {
                isDeleteMode.set(true);
                ReToast.show(this, "点击按钮删除菜品...");
                return true;
            } else {
                isDeleteMode.set(false);
            }
            return false;
        });
        // 显示 PopupMenu
        popupMenu.show();
    }

    @Override
    public boolean isFirstInSection(int position) {
        return false;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onOrderChanged(List<VegetableInformation> list) {
        menuAdapter.notifyDataSetChanged();
        sidebarAdapter.notifyDataSetChanged();
    }
}
