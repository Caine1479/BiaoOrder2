package com.biaoorder2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.bean.OrderManager;
import com.biaoorder2.bean.Orders;
import com.biaoorder2.databinding.AdapterShoppingcarBinding;
import com.biaoorder2.util.CustomDialog;
import com.biaoorder2.util.setOnClickListener;
import com.bumptech.glide.Glide;

import java.util.List;

public class LRShoppingCarAdapter extends RecyclerView.Adapter<LRShoppingCarAdapter.LinearViewHolder> {

    public Context mContext;
    public List<Orders> ordersList;

    public setOnClickListener listener;

    public OrderManager orderManager = OrderManager.getInstance();



    public LRShoppingCarAdapter(Context mContext, List<Orders> ordersList, setOnClickListener listener) {
        this.mContext = mContext;
        this.ordersList = ordersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(AdapterShoppingcarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
        holder.bind(ordersList.get(position), mContext);
        Orders orders = OrderManager.getInstance().getOrders(CustomDialog.hallTableNum).get(position);
        holder.binding.imgAdd.setOnClickListener(v -> {
            orders.setVegetableNum(orders.getVegetableNum() + 1);
            holder.binding.tvCount.setText(String.valueOf(Integer.parseInt(holder.binding.tvCount.getText().toString())+1));
            notifyDataSetChanged();
        });
        holder.binding.imgMinus.setOnClickListener(v -> {
            int removedPosition = holder.getLayoutPosition(); // 获取删除项的位置
            Orders orders1 = OrderManager.getInstance().getOrders(CustomDialog.hallTableNum).get(removedPosition);
            orders1.setVegetableNum(orders1.getVegetableNum() - 1);
            holder.binding.tvCount.setText(String.valueOf(Integer.parseInt(holder.binding.tvCount.getText().toString())-1));
            if (Integer.parseInt(holder.binding.tvCount.getText().toString()) <= 0) {
                removeItem(removedPosition);
                orderManager.removeOrder(CustomDialog.hallTableNum, orders1);
                orderManager.updateTotal(CustomDialog.hallTableNum,Integer.parseInt(orders1.vegetableInformation.getPrice()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class LinearViewHolder extends RecyclerView.ViewHolder {
        public AdapterShoppingcarBinding binding;

        public LinearViewHolder(@NonNull AdapterShoppingcarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bind(Orders orders, Context mContext) {
            // 在这里可以通过 binding 对象访问布局文件中的控件并设置数据
            binding.tvShoppingName.setText(orders.getVegetableInformation().getName());
            binding.tvShoppingTaste.setText(orders.getTaste());
            binding.tvCount.setText(String.valueOf(orders.getVegetableNum()));
            Glide.with(mContext).load(orders.getVegetableInformation().getImageLink()).into(binding.imgShoppingTable);
        }
    }
    private void removeItem(int position) {
        if (ordersList != null && position < ordersList.size()) {
            ordersList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, ordersList.size());
        } else {
            Log.e("AdapterError", "Invalid position or empty list");
        }
    }
}