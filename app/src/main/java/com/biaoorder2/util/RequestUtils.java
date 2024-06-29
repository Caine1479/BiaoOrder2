package com.biaoorder2.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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

    // 发送json格式的异步post请求
    public static Response post(String url, String json) throws IOException {
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return getOkHttpClient().newCall(request).execute();
    }

    // 发送异步get请求
    public static Response get(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();
        return getOkHttpClient().newCall(request).execute();
    }
    // 发送异步get请求
    public static Response get(String url,int no) throws IOException{
        String fullUrl = url + "?no=" + no;
        Request request = new Request.Builder()
                .url(fullUrl)
                .build();
        return getOkHttpClient().newCall(request).execute();
    }
}
