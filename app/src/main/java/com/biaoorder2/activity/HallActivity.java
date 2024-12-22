package com.biaoorder2.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.biaoorder2.R;
import com.biaoorder2.fragment.HallFragment;
import com.biaoorder2.fragment.HistoryBillsFragment;
import com.biaoorder2.fragment.PersonalFragment;
import com.biaoorder2.databinding.ActivityHallBinding;
import com.biaoorder2.manager.BillManager;
import com.biaoorder2.manager.TableManager;
import com.biaoorder2.manager.VegetableManager;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.CustomDialog;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HallActivity extends AppCompatActivity {
    public Fragment selectedFragment = null;

    public ActivityHallBinding binding;
    public int itemId;

    private long lastBackPressedTime = 0; // 记录上次按下返回键的时间
    private static final int EXIT_INTERVAL = 2000; // 两次按返回键的间隔时间（毫秒）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        itemId = R.id.hall_item;

        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        MaterialToolbar toolbar = binding.topAppBar;

        // 设置默认界面
        replaceNavigation(R.id.hall_frameLayout, new HallFragment());
        // 点击切换fragment
        bottomNavigationView.setOnItemSelectedListener(item -> {
            SelectNavigation(item);
            return true;  // This should be true to mark the item as selected
        });

        // 设置toolbar处理菜单项的点击事件。它通常用于 Toolbar、PopupMenu、BottomNavigationView 等组件事件
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.edit_set) {
                // 获取MenuItem对应的View
                showPopupMenu(findViewById(item.getItemId()));
                return true;
            }
            return false;
        });

        // 添加返回键回调
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                long currentTime = System.currentTimeMillis();
                // 当前时间-记录上次按下返回键的时间 < 间隔时间2秒
                if (currentTime - lastBackPressedTime < EXIT_INTERVAL) {
                    finish(); // 退出应用
                } else {
                    ReToast.show(HallActivity.this, "再次返回退出程序");
                    lastBackPressedTime = currentTime;
                }
            }
        });

    }

    // 点击底部导航栏时选择activity_hall.xml切换为相应的界面
    public void SelectNavigation(@NonNull MenuItem item) {
        itemId = item.getItemId();
        if (itemId == R.id.hall_item) {
            selectedFragment = new HallFragment();
            binding.topAppBar.setTitle("大厅");

        } else if (itemId == R.id.personalCenter_item) {
            selectedFragment = new PersonalFragment();
            binding.topAppBar.setTitle("个人中心");

        } else if (itemId == R.id.orderList_item) {
            selectedFragment = new HistoryBillsFragment();
            binding.topAppBar.setTitle("历史订单");
        }
        if (selectedFragment != null) {
            replaceNavigation(R.id.hall_frameLayout, selectedFragment);
        }
    }

    // 替换activity_hall.xml切换为相应的界面
    public void replaceNavigation(int containerViewId, Fragment selectedFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerViewId, selectedFragment);
        transaction.commit();
    }

    // 编辑框
    private void showPopupMenu(View anchorView) {
        // 创建 PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        // 绑定菜单资源
        if (itemId == R.id.hall_item) {
            popupMenu.getMenuInflater().inflate(R.menu.overflow, popupMenu.getMenu());
        } else {
            popupMenu.getMenuInflater().inflate(R.menu.overflow3, popupMenu.getMenu());
        }

        // 设置菜单项点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            // 删除餐桌事件
            if (item.getItemId() == R.id.delete_table) {
                ReToast.show(this, "长按删除桌子...");
                return true;
            }
            // 添加餐桌事件
            if (item.getItemId() == R.id.add_table) {
                CustomDialog.editTableDialog(this, "添加餐桌", () -> {
                });
                return true;
            }
            return false;
        });
        // 显示 PopupMenu
        popupMenu.show();
    }
}
