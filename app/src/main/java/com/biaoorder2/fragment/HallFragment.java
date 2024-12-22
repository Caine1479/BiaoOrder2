package com.biaoorder2.fragment;

import static com.biaoorder2.pool.ConstantPools.URL_GET_TABLES;
import static com.biaoorder2.pool.ConstantPools.bad;
import static com.biaoorder2.pool.ConstantPools.dinnering;
import static com.biaoorder2.pool.ConstantPools.free;
import static com.biaoorder2.pool.ConstantPools.ordered;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Context;
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

import com.biaoorder2.adapter.LRHallAdapter;
import com.biaoorder2.bean.Table;
import com.biaoorder2.databinding.FragmentHallBinding;
import com.biaoorder2.manager.TableManager;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.RequestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HallFragment extends Fragment {

    private LRHallAdapter hallAdapter;

    private Handler mainHandler;
    public List<Table> tableList;
    public TableManager tableManager = TableManager.getInstance();
    private FragmentHallBinding binding;
    private RecyclerView hallrecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHallBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.refreshLayout.setOnRefreshListener(this::refreshData);
        binding.btnTop.setOnClickListener(v -> hallrecyclerView.smoothScrollToPosition(0));
        initView();
        binding.tvFree.setText(String.valueOf(free));
        binding.tvOrdered.setText(String.valueOf(ordered));
        binding.tvDinnering.setText(String.valueOf(dinnering));
        binding.tvBad.setText(String.valueOf(bad));

    }

    // 绑定项目
    private void initView() {
        mainHandler = new Handler(Looper.getMainLooper());
        hallrecyclerView = binding.recyclerViewHall;
        // GridLayoutManager 是 RecyclerView 提供的一种布局管理器，可以按照网格方式排列项目。
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        hallrecyclerView.setLayoutManager(gridLayoutManager);
        tableList = tableManager.getTableList();

        hallAdapter = new LRHallAdapter(tableList, requireContext());
        hallrecyclerView.setAdapter(hallAdapter);
        setState(tableList);
    }

    public void setState(List<Table> tableList) {
        // 数据初始化
        free = 0;
        ordered = 0;
        dinnering = 0;
        bad = 0;
        for (int i = 0; i < tableList.size(); i++) {
            String state = tableList.get(i).getState();
            switch (state) {
                case "0":
                    free += 1;
                    break;
                case "1":
                    ordered += 1;
                    break;
                case "2":
                    dinnering += 1;
                    break;
                default:
                    bad += 1;
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshData() {
        if (isAdded()) {
            // Fragment 已附加，安全地调用 requireActivity() 或 getActivity()
            new Handler().postDelayed(() -> {
                if (isAdded()) { // 重新检查以确保 Fragment 仍然附加
                    mainHandler.post(() -> {
                        binding.refreshLayout.setRefreshing(false);
                        hallAdapter.setTableList(tableManager.getTableList());
                        hallAdapter.notifyDataSetChanged();
                    });
                    ReToast.show(requireActivity(), "数据已更新");
                }
            }, 500); // 延时1秒，模拟网络请求时间
        }
        // 模拟刷新操作，通常是网络请求数据等
    }
}
