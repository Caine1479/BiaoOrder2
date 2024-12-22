package com.biaoorder2.util;

import android.app.Activity;
import android.content.Context;

import com.biaoorder2.ui.ReToast;
import com.biaoorder2.Interface.OnResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestUtils {

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // 发送json格式的同步post请求
    public static Response post(String url, String json) throws IOException {
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return getOkHttpClient().newCall(request).execute();
    }

    // 发送同步get请求
    public static Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return getOkHttpClient().newCall(request).execute();
    }

    // 发送同步get请求
    public static Response get(String url, String key, String value) throws IOException {
        String fullUrl = url + "?" + key + "=" + value;
        Request request = new Request.Builder()
                .url(fullUrl)
                .build();
        return getOkHttpClient().newCall(request).execute();
    }


    // 发送异步post请求
    public static void post(String url, String json, Callback callback) {
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        getOkHttpClient().newCall(request).enqueue(callback);
    }

    // 发送异步get请求
    public static void get1(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        getOkHttpClient().newCall(request).enqueue(callback);
    }

    // 发送异步 GET 请求RequestParam
    public static void get(String url, String key, String value, Callback callback) {
        String fullUrl = url + "?" + key + "=" + value;
        Request request = new Request.Builder()
                .url(fullUrl)
                .build();
        getOkHttpClient().newCall(request).enqueue(callback);
    }


    // 处理get请求成功的操作
    public static String handleGetResponse(Response response, Context context) {
        String result = "";
        try {
            if (response.body() != null) {
                result = response.body().string();
            }
            return result;
        } catch (IOException e) {
            ReToast.show((Activity) context, "服务器出错qwq...");
        }
        return null;
    }

    // 发送异步delete请求
    public static void delete(String url, String key, int value, Callback callback) throws IOException {
        String fullUrl = url + "?" + key + "=" + value;
        Request request = new Request.Builder()
                .url(fullUrl)
                .delete()  // 这里是 DELETE 请求
                .build();
        getOkHttpClient().newCall(request).enqueue(callback);  // 异步执行，使用OkHttp的enqueue方法
    }

    // 发送异步delete请求
    public static void delete(String url, String key, String value, Callback callback) throws IOException {
        String fullUrl = url + "?" + key + "=" + value;
        Request request = new Request.Builder()
                .url(fullUrl)
                .delete()  // 这里是 DELETE 请求
                .build();
        getOkHttpClient().newCall(request).enqueue(callback);  // 异步执行，使用OkHttp的enqueue方法
    }

    // 处理请求失败的逻辑
    public static void handleError(Context mContext) {
        ReToast.show((Activity) mContext, "请检查网络，稍后重试...");
    }

    // 处理请求失败的逻辑
    public static void handleError(Activity activity) {
        ReToast.show(activity, "请检查网络，稍后重试...");
    }

    // 处理请求的响应
    public static void handleResponse(Response response, Context mContext, OnResponseListener onResponseListener) {
        String result = "";
        try {
            if (response.body() != null) {
                result = response.body().string();
                response.close();
            }
            JSONObject data = new JSONObject(result);
            if (data.getString("code").equals("1")) {
                // 接口回调
                if (onResponseListener != null) {
                    onResponseListener.onConfirmDelete();
                }
            }
        } catch (JSONException | IOException e) {
            ReToast.show(mContext, "服务器出错qwq...");
        }
    }
}
