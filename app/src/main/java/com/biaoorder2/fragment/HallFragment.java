package com.biaoorder2.fragment;

import static com.biaoorder2.pool.ConstantPools.URL_GET_TABLES;

import android.annotation.SuppressLint;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.R;
import com.biaoorder2.adapter.LRHallAdapter;
import com.biaoorder2.bean.Table;
import com.biaoorder2.util.CustomDialog;
import com.biaoorder2.util.RequestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class HallFragment extends Fragment {

    private LRHallAdapter hallAdapter;

    private Handler mainHandler;
    private final List<Table> tableList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hall,container,false);
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        getAllTables();
    }

    // 绑定项目
    private void initView(@NonNull View view) {
        mainHandler = new Handler(Looper.getMainLooper());
        RecyclerView hallrecyclerView = view.findViewById(R.id.recyclerView_hall);
        // GridLayoutManager 是 RecyclerView 提供的一种布局管理器，可以按照网格方式排列项目。
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        hallrecyclerView.setLayoutManager(gridLayoutManager);
        hallAdapter = new LRHallAdapter(tableList,pos ->
                showDialog(hallAdapter.tableList.get(pos).getState(),pos));
        hallrecyclerView.setAdapter(hallAdapter);
    }

    // 获得所有的餐桌信息
    @SuppressLint("NotifyDataSetChanged")
    @SuppressWarnings({"all"})
    public void getAllTables() {
        List<Table> tables = new ArrayList<>();
        new Thread(() -> {
            try {
                Response response = RequestUtils.get(URL_GET_TABLES);
                if (!response.isSuccessful()) {
                    throw new IOException();
                }
                String responseData = "";
                if (response.body() != null) {
                    responseData = response.body().string();
                    response.close();
                }
                JSONObject data = new JSONObject(responseData);
                if (data.getString("code").equals("1")) {
                    JSONArray jsonArray = data.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonTable = jsonArray.getJSONObject(i);
                        int no = jsonTable.getInt("no");
                        String name = jsonTable.getString("name");
                        String phone = jsonTable.getString("phone");
                        String state = jsonTable.getString("state");
                        tables.add(new Table(no, name, phone, state));
                    }
                    mainHandler.post(() -> {
                        if (isAdded()) {
                            tableList.clear();
                            tableList.addAll(tables);
                            hallAdapter.notifyDataSetChanged();
                        }
                    });

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
    // 根据点击位置获得餐桌信息的状态显示dialog
    @SuppressLint("NotifyDataSetChanged")
    public void showDialog(String state, int pos){
        CustomDialog.showDialog(requireContext(),state,"桌号: ",(pos+1));
    }
}
