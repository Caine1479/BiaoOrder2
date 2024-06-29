package com.biaoorder2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.R;
import com.biaoorder2.adapter.LRShoppingCarAdapter;
import com.biaoorder2.bean.OrderManager;
import com.biaoorder2.bean.Orders;
import com.biaoorder2.util.CustomDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class ShoppingCarDialogFragment extends BottomSheetDialogFragment {
    public List<Orders> ordersList;


    public RecyclerView recyclerView;

    public LRShoppingCarAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shoppingcar,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView_shopping);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        OrderManager orderManager = OrderManager.getInstance();
        ordersList = orderManager.getOrders(CustomDialog.hallTableNum); // 获取订单列表
        adapter = new LRShoppingCarAdapter(requireContext(),ordersList);
        recyclerView.setAdapter(adapter);
    }
}
