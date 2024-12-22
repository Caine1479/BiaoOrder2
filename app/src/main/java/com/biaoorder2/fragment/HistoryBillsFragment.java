package com.biaoorder2.fragment;

import static com.biaoorder2.pool.ConstantPools.URL_GET_HISTORY_BILL;
import static com.biaoorder2.util.CustomDialog.showHistoryBill;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.biaoorder2.adapter.LRHistoryBillsAdapter;
import com.biaoorder2.bean.HistoryBill;
import com.biaoorder2.databinding.FragmentHistoryBillsBinding;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.RequestUtils;
import com.google.android.material.search.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class HistoryBillsFragment extends Fragment {

    public FragmentHistoryBillsBinding binding;

    public LRHistoryBillsAdapter adapter;
    public SearchView searchView;
    public String result;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    public List<HistoryBill> historyBillList = new ArrayList<>();
    public List<HistoryBill> searchBillList = new ArrayList<>();
    private RecyclerView recyclerViewSearchBill, recyclerViewHistoryBill;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getHistoryBill();
        binding = FragmentHistoryBillsBinding.inflate(getLayoutInflater());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView();
        return binding.getRoot();
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editText = searchView.getEditText();
        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        result = "";
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        result = editText.getText().toString();
                        searchBillList.clear();
                        // 匹配订单号
                        for (int i = 0; i < historyBillList.size(); i++) {
                            if (String.valueOf(historyBillList.get(i).getOrderNo()).contains(result)) {
                                searchBillList.add(historyBillList.get(i));
                            }
                        }
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void afterTextChanged(Editable s) {
                        // 显示查询列表
                        adapter = new LRHistoryBillsAdapter(searchBillList, pos ->
                                showHistoryBill(requireContext(), searchBillList, searchBillList.get(pos).getOrderNo()), requireContext());
                        recyclerViewSearchBill.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });

        binding.btnTop.setOnClickListener(v -> recyclerViewHistoryBill.smoothScrollToPosition(0));
    }

    // 绑定项目
    private void initView() {
        // 总历史订单
        recyclerViewHistoryBill = binding.recyclerViewHistoryBill;
        // GridLayoutManager 是 RecyclerView 提供的一种布局管理器，可以按照网格方式排列项目。
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1);
        recyclerViewHistoryBill.setLayoutManager(gridLayoutManager);

        adapter = new LRHistoryBillsAdapter(historyBillList, pos ->
                showHistoryBill(requireContext(), historyBillList, historyBillList.get(pos).getOrderNo()), requireContext());
        recyclerViewHistoryBill.setAdapter(adapter);

        // 查询订单
        recyclerViewSearchBill = binding.recyclerViewSearchBill;
        recyclerViewSearchBill.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        searchView = binding.searchView;
    }

    // 请求历史订单数据
    @SuppressLint("NotifyDataSetChanged")
    public void getHistoryBill() {
        List<HistoryBill> list = new ArrayList<>();
        new Thread(() -> {
            try {
                Response response = RequestUtils.get(URL_GET_HISTORY_BILL);
                if (!response.isSuccessful()) {
                    ReToast.show(requireActivity(), "请求失败,请检查网络...");
                    return;
                }
                String result = "";
                if (response.body() != null) {
                    result = response.body().string();
                    response.close();
                }
                JSONObject data = new JSONObject(result);
                if (data.getString("code").equals("1")) {
                    JSONArray jsonArray = data.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int no = jsonObject.getInt("no");
                        String vegetables = jsonObject.getString("vegetables");
                        String price = jsonObject.getString("price");
                        String date = jsonObject.getString("date");
                        String payment = jsonObject.getString("payment");
                        int orderNo = jsonObject.getInt("orderNo");
                        list.add(new HistoryBill(no, vegetables, price, date, payment, orderNo));
                    }
                    mainHandler.post(() -> {
                        historyBillList.clear();
                        historyBillList.addAll(list);
                        adapter.notifyDataSetChanged();
                    });

                } else {
                    ReToast.show(requireActivity(), "数据获取失败...");
                }
            } catch (IOException | JSONException e) {
                ReToast.show(requireActivity(), "响应失败,请稍后再试...");
            }
        }).start();
    }
}