package com.biaoorder2.activity;

import static com.biaoorder2.pool.ConstantPools.URL_INSERT_ENROLL;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.biaoorder2.databinding.ActivityEnrollBinding;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.RequestUtils;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EnrollActivity extends AppCompatActivity {

    public ActivityEnrollBinding binding;
    public MaterialToolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnrollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // 返回上一级
        binding.topEnrollBar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed());

        binding.btnOkEnroll.setOnClickListener(v -> {
            String id = String.valueOf(binding.etId.getText());
            String password = String.valueOf(binding.etPassword.getText());
            String username = String.valueOf(binding.etUsername.getText());
            String headIcon = String.valueOf(binding.etHeadIcon.getText());
            if (id.isEmpty() || password.isEmpty() || username.isEmpty()) {
                ReToast.show(this, "请输入完整信息!");
            } else {
                postEnroll(id, password, username, headIcon);
                finish();
            }

        });
    }


    // 注册请求
    public void postEnroll(String id, String password, String username, String headIcon) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("password", password);
            jsonObject.put("username", username);
            if (!headIcon.isEmpty()) {
                jsonObject.put("headIcon", headIcon);
            }
            RequestUtils.post(URL_INSERT_ENROLL, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    RequestUtils.handleError(EnrollActivity.this);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    RequestUtils.handleResponse(response, EnrollActivity.this, () ->
                            ReToast.show(EnrollActivity.this, "申请已发起,请等待管理员确认..."));
                }
            });
        } catch (JSONException e) {
            ReToast.show(EnrollActivity.this, "服务器出错qwq...");
        }
    }
}
