package com.biaoorder2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.biaoorder2.databinding.ActivityLoginBinding;
import com.biaoorder2.pool.ConstantPools;
import com.biaoorder2.util.RequestUtils;
import com.biaoorder2.ui.ReToast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v -> login());
    }
    private void login(){
        String username = String.valueOf(binding.etUsername.getText());
        String password = String.valueOf(binding.etPassword.getText());
        if (username.isEmpty() || password.isEmpty()) {
            ReToast.show(LoginActivity.this,"用户名或密码不能为空!");
            return;
        }

        new Thread(() -> {
            try {
                //将请求体封装进JSON格式对象里面
                JSONObject jsonData = new JSONObject();
                jsonData.put("name", username);
                jsonData.put("password", password);
                Response response = RequestUtils.post(ConstantPools.URL_LOGIN, jsonData.toString());
                if (!response.isSuccessful()){
                    ReToast.show(LoginActivity.this,"服务器未响应,请联系管理员!");
                }
                String result = "";
                if (response.body() != null) {
                    result = response.body().string();
                }
                response.close();
                Log.i("JSONObject", "login: "+ result);
                JSONObject data = new JSONObject(result);
                String code = data.getString("code");
                if (!code.equals("1")){
                    ReToast.show(LoginActivity.this,"用户名或密码错误!");
                    return;
                }
                ReToast.show(LoginActivity.this,"登录成功!");
                // 进入大厅界面
                startActivity(new Intent(LoginActivity.this, HallActivity.class));
                finish();
            } catch (IOException e) {
                ReToast.show(LoginActivity.this,"网络错误，请刷新重新试...");
            } catch (JSONException e) {
                ReToast.show(LoginActivity.this,"服务器出错qwq...");
            }
        }).start();
    }
}