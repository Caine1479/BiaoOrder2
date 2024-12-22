package com.biaoorder2.activity;

import static com.biaoorder2.pool.ConstantPools.URL_LOGIN_SKIP;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.biaoorder2.bean.OrderUser;
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
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        setContentView(binding.getRoot());
        skipLogin();

        binding.btnLogin.setOnClickListener(v -> login());

        //注册
        binding.tvEnroll.setOnClickListener(v ->
                startActivity(new Intent(this, EnrollActivity.class)));
    }

    private void login() {
        String id = String.valueOf(binding.etId.getText());
        String password = String.valueOf(binding.etPassword.getText());
        if (id.isEmpty() || password.isEmpty()) {
            ReToast.show(LoginActivity.this, "用户名或密码不能为空!");
            return;
        }
        new Thread(() -> {
            try {
                //将请求体封装进JSON格式对象里面
                JSONObject jsonData = new JSONObject();
                jsonData.put("id", id);
                jsonData.put("password", password);
                Response response = RequestUtils.post(ConstantPools.URL_LOGIN, jsonData.toString());
                if (!response.isSuccessful()) {
                    ReToast.show(LoginActivity.this, "服务器未响应,请联系管理员!");
                }
                String result = "";
                if (response.body() != null) {
                    result = response.body().string();
                }
                response.close();
                JSONObject data = new JSONObject(result);

                JSONObject dataObject = data.getJSONObject("data");
                String code = data.getString("code");


                String jwt = dataObject.getString("jwt");
                JSONObject orderUserString = dataObject.getJSONObject("userInformation");

                OrderUser orderUser = parseOrder(orderUserString);

                // 保存JWT到SharedPreferences,登录或重新登录时会保存jwt到客户端
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("jwt", jwt); // jwtToken是从服务器返回的JWT
                editor.putString("id", orderUser.getId());
                Log.d("id", "login: " + orderUser.getId());
                editor.putString("headIcon", orderUser.getHeadIcon());
                editor.putString("username", orderUser.getUsername());
                editor.putString("post", orderUser.getPost());
                editor.apply();

                if (!code.equals("1")) {
                    ReToast.show(LoginActivity.this, "用户名或密码错误!");
                    return;
                }

                ReToast.show(LoginActivity.this, "登录成功!");
                // 进入大厅界面
                startActivity(new Intent(LoginActivity.this, HallActivity.class));
                finish();
            } catch (IOException | JSONException e) {
                ReToast.show(LoginActivity.this, "网络错误，请刷新重新试...");
            }
        }).start();
    }

    // 校验登录jwt令牌
    public void skipLogin() {
        String savedJwt;
        savedJwt = preferences.getString("jwt", null);
        if (savedJwt != null) {
            new Thread(() -> {
                try {
                    Response response = RequestUtils.get(URL_LOGIN_SKIP, "jwt", savedJwt);
                    String result = "";
                    if (response.body() != null) {
                        result = response.body().string();
                        response.close();
                    }
                    JSONObject data = new JSONObject(result);
                    if (data.getString("code").equals("1")) {
                        // 进入大厅界面
                        startActivity(new Intent(LoginActivity.this, HallActivity.class));
                        ReToast.show(this, "欢迎回来点餐系统!");
                        finish();
                    } else {
                        ReToast.show(this, "登录超时，请重新登录...");
                    }
                } catch (IOException | JSONException e) {
                    ReToast.show(this, "服务器异常...");
                }
            }).start();
        }
    }

    //解析orderUserString
    public OrderUser parseOrder(JSONObject parseOrder) throws JSONException {
        OrderUser orderUser = new OrderUser();
        // 解析userInformation
        String id = parseOrder.getString("id");
        String password = parseOrder.getString("password");
        String headIcon = parseOrder.getString("headIcon");
        String username = parseOrder.getString("username");
        String post = parseOrder.getString("post");
        orderUser.setId(id);
        orderUser.setPassword(password);
        orderUser.setHeadIcon(headIcon);
        orderUser.setUsername(username);
        orderUser.setPost(post);
        return orderUser;
    }
}