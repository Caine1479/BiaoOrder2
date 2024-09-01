package com.biaoorder2.activity;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.biaoorder2.R;
import android.os.Handler;
import com.biaoorder2.fragment.HallFragment;
import com.biaoorder2.fragment.PersonalFragment;
import com.biaoorder2.databinding.ActivityHallBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HallActivity extends AppCompatActivity {
    Fragment selectedFragment = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHallBinding binding = ActivityHallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        // 设置默认界面
        replaceNavigation(R.id.hall_frameLayout, new HallFragment());
        bottomNavigationView.setOnItemSelectedListener(item -> {
            SelectNavigation(item);
            return true;  // This should be true to mark the item as selected
        });

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
    }

    // 点击底部导航栏时选择activity_hall.xml切换为相应的界面
    public void SelectNavigation(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.hall_item) {
            selectedFragment = new HallFragment();
        } else if (itemId == R.id.personalCenter_item) {
            selectedFragment = new PersonalFragment();
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

    private void refreshData() {
        // 模拟刷新操作，通常是网络请求数据等
        new Handler().postDelayed(() -> {
            // 刷新完成后停止动画
            swipeRefreshLayout.setRefreshing(false);
            // 更新数据
            // 例如：adapter.notifyDataSetChanged();
        }, 1000); // 延时2秒，模拟网络请求时间
    }
}
