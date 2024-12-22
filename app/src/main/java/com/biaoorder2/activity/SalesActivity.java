package com.biaoorder2.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.biaoorder2.R;
import com.biaoorder2.bean.HistoryBill;
import com.biaoorder2.bean.VegetableInformation;
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

public class SalesActivity extends AppCompatActivity {
    public ActivitySalesBinding binding;
    public MaterialToolbar toolbar;
    public PieChart pieChart;

    public List<VegetableInformation> vegetableList;
    public List<HistoryBill> billList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySalesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();

        // 打开所有订单
        binding.tvAllSales.setOnClickListener(v ->
                startActivity(new Intent(this, FullSalesActivity.class)));

        // 返回上一级
        toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed());


    }

    public void initView() {
        toolbar = binding.topVegetableBar;
        pieChart = binding.pieChart;

        vegetableList = get_Top5(VegetableManager.getInstance().getVegetableList());
        billList = BillManager.getInstance().getHistoryBillList();

        //获得销售总额
        binding.tvTurnover.setText(String.valueOf(get_TotalSales(vegetableList)));
        binding.tvOrdersCount.setText(String.valueOf(billList.size()));

        setupPieChat();
    }

    // 设置饼图数据
    public void setupPieChat() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        Description description = new Description();
        description.setText("数据表示");


        int no1 = getResources().getColor(R.color.no_1, null);
        int no2 = getResources().getColor(R.color.no_2, null);
        int no3 = getResources().getColor(R.color.no_3, null);
        int no4 = getResources().getColor(R.color.no_4, null);
        int no5 = getResources().getColor(R.color.no_5, null);


        // 遍历数据，填充 PieEntry
        for (int i = 0; i < vegetableList.size(); i++) {
            entries.add(new PieEntry(vegetableList.get(i).getSales(), vegetableList.get(i).getName()));
        }

        // 创建数据集并设置样式
        PieDataSet dataSet = new PieDataSet(entries, "");

        // 设置 "Value Line" 样式
        dataSet.setDrawValues(true);  // 显示数值
        dataSet.setValueTextSize(8f);  // 数值文字大小
        dataSet.setValueTextColor(Color.BLACK);  // 数值颜色
        dataSet.setSliceSpace(3);   // 每块之间的距离


        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);  // 标签在饼块外
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);  // 标签在饼块外
        dataSet.setValueLinePart1OffsetPercentage(80f);  // 指向线的起点
        dataSet.setValueLinePart1Length(0.25f);  // 指向线的第一段长度
        dataSet.setValueLinePart2Length(0.35f);  // 指向线的第二段长度
        dataSet.setValueLineColor(Color.BLACK);  // 指向线颜色

        // 使用 ValueFormatter 来自定义标签显示
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                String name = pieEntry.getLabel();
                return name + " " + Math.round(value) + "份"; // 确保返回的值是整数
            }
        });


        dataSet.setColors(no1, no2, no3, no4, no5);

        // 创建 PieData 对象
        PieData data = new PieData(dataSet);

        // 设置数据并刷新图表
        pieChart.setData(data);
        pieChart.setDrawEntryLabels(false);  // 隐藏标签
        pieChart.setUsePercentValues(false);    // 表内数据用百分比替代，而不是原先的值。并且ValueFormatter中提供的值也是该百分比的。默认false
        pieChart.setCenterText("TOP5"); // 圆环中心的文字，会自动适配不会被覆盖
        pieChart.setCenterTextRadiusPercent(150f); // 中心文本边界框矩形半径比例，默认是100%.
        pieChart.setHoleRadius(72f);  // 设置中心圆半径占整个饼形图圆半径（图表半径）的百分比。默认50%
        pieChart.setTransparentCircleRadius(300f);   // 设置环形与中心圆之间的透明圆环半径占图表半径的百分比。默认55%（比如，中心圆为50%占比，而透明环设置为55%占比，要去掉中心圆的占比，也就是环只有5%的占比）
        pieChart.setTransparentCircleAlpha(0);    // 上述透明圆环的透明度[0-255]，默认100
        pieChart.setMaxAngle(360);    // 设置整个饼形图的角度，默认是360°即一个整圆，也可以设置为弧，这样现实的值也会重新计算
        pieChart.setRotationEnabled(false); // 禁止旋转
        pieChart.setDescription(description);

        pieChart.animateY(500); // 动画效果
        pieChart.invalidate();  // 刷新图表
    }

    // 获得销售前Top5
    public List<VegetableInformation> get_Top5(List<VegetableInformation> list) {
        list.sort((v1, v2) -> {
            if (v1.getSales() == 0 && v2.getSales() == 0) {
                return 0;
            }
            return v2.getSales() - v1.getSales();
        });

        List<VegetableInformation> top5Vegetables = new ArrayList<>();
        int size = Math.min(list.size(), 5); // 取集合的前5个元素

        for (int i = 0; i < size; i++) {
            top5Vegetables.add(list.get(i));
        }

        // 返回长度为5的集合
        return top5Vegetables;
    }

    // 获得销售总额
    public int get_TotalSales(List<VegetableInformation> vegetableList) {
        int total = 0;
        if (vegetableList != null) {
            for (int i = 0; i < vegetableList.size(); i++) {
                VegetableInformation vegetableInformation = vegetableList.get(i);
                total += (Integer.parseInt(vegetableInformation.getPrice()) * vegetableInformation.getSales());
            }
            return total;
        }
        return 0;
    }
}
