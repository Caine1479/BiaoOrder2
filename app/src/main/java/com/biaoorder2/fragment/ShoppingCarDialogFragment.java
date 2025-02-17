package com.biaoorder2.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.activity.OrderActivity;
import com.biaoorder2.adapter.LRShoppingCarAdapter;
import com.biaoorder2.manager.OrderManager;
import com.biaoorder2.bean.Orders;
import com.biaoorder2.databinding.FragmentShoppingcarBinding;
import com.biaoorder2.util.CustomDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class ShoppingCarDialogFragment extends BottomSheetDialogFragment {
    public List<Orders> ordersList;
    public RecyclerView recyclerView;
    public LRShoppingCarAdapter adapter;

    public FragmentShoppingcarBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentShoppingcarBinding.inflate(getLayoutInflater());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = binding.recyclerViewShopping;
        recyclerView.addItemDecoration
                (new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        OrderManager orderManager = OrderManager.getInstance();
        ordersList = orderManager.getOrders(CustomDialog.hallTableNum); // 获取订单列表
        adapter = new LRShoppingCarAdapter(requireContext(), ordersList, pos -> {
        });
        recyclerView.setAdapter(adapter);
    }

    // fragment关闭的时候
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // 获取宿主Activity并调用刷新方法
        if (getActivity() instanceof OrderActivity) {
            ((OrderActivity) getActivity()).updateVegetableCount(CustomDialog.hallTableNum);
        }
    }

}
