package com.biaoorder2.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.biaoorder2.R;
import com.biaoorder2.databinding.ActivitySplashBinding;
import com.biaoorder2.manager.BillManager;
import com.biaoorder2.manager.TableManager;
import com.biaoorder2.manager.VegetableManager;

/**
 * 应用打开动画
 */

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    public VegetableManager vegetableManager = VegetableManager.getInstance();
    public BillManager billManager = BillManager.getInstance();
    public TableManager tableManager = TableManager.getInstance();
    private static final int SPLASH_DISPLAY_LENGTH = 2000; // 显示时间（毫秒）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getAllData();
        if (vegetableManager.getVegetableList() != null && billManager.getHistoryBillList() != null && tableManager.getTableList() != null) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                binding.tvLoading.setText("加载完成!");
                binding.progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }, SPLASH_DISPLAY_LENGTH);


        }
    }

    // 用户登录成功后获得数据
    public void getAllData() {
        vegetableManager.getVegetableList_Http(this);
        billManager.getHistoryBillList_Http(this);
        tableManager.getTableList_Http(this);
    }
}