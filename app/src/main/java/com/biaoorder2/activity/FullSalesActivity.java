package com.biaoorder2.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.R;
import com.biaoorder2.adapter.LRFullSalesAdapter;
import com.biaoorder2.bean.HistoryBill;
import com.biaoorder2.bean.VegetableInformation;
import com.biaoorder2.databinding.ActivityFullsalesBinding;
import com.biaoorder2.databinding.ActivitySalesBinding;
import com.biaoorder2.manager.BillManager;
import com.biaoorder2.manager.VegetableManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class FullSalesActivity extends AppCompatActivity {
    public ActivityFullsalesBinding binding;
    public MaterialToolbar toolbar;
    public RecyclerView recyclerView;
    public LRFullSalesAdapter adapter;
    public List<VegetableInformation> vegetableList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullsalesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();

    }

    public void initView() {
        toolbar = binding.topVegetableBar;

        toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed());

        vegetableList = list_Top(VegetableManager.getInstance().getVegetableList());

        recyclerView = binding.recyclerViewFullSales;
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new LRFullSalesAdapter(this, vegetableList);
        recyclerView.setAdapter(adapter);
    }


    // 菜品销量排序
    public List<VegetableInformation> list_Top(List<VegetableInformation> list) {
        if (!list.isEmpty()) {
            list.sort((v1, v2) -> {
                if (v1.getSales() == 0 && v2.getSales() == 0) {
                    return 0;
                }
                return v2.getSales() - v1.getSales();
            });
        }
        return list;
    }
}
