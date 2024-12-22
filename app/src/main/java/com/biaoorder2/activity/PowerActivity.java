package com.biaoorder2.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.biaoorder2.databinding.ActivityPowerBinding;
import com.google.android.material.appbar.MaterialToolbar;

public class PowerActivity extends AppCompatActivity {

    public ActivityPowerBinding binding;
    public MaterialToolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPowerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();

        // 返回上一级
        toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed());
    }

    public void initView() {
        toolbar = binding.topPowerBar;
    }

}
