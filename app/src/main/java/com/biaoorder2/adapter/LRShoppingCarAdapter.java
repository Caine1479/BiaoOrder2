package com.biaoorder2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.bean.Orders;
import com.biaoorder2.databinding.AdapterShoppingcarBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class LRShoppingCarAdapter extends RecyclerView.Adapter<LRShoppingCarAdapter.LinearViewHolder> {

    public Context mContext;
    public List<Orders> ordersList;

    public LRShoppingCarAdapter(Context mContext, List<Orders> ordersList) {
        this.mContext = mContext;
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(AdapterShoppingcarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
        holder.bind(ordersList.get(position), mContext);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    static class LinearViewHolder extends RecyclerView.ViewHolder {
        public AdapterShoppingcarBinding binding;

        public LinearViewHolder(@NonNull AdapterShoppingcarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bind(Orders orders, Context mContext) {
            // 在这里可以通过 binding 对象访问布局文件中的控件并设置数据
            binding.tvShoppingName.setText(orders.getVegetableInformation().getName());
            Glide.with(mContext).load(orders.getVegetableInformation().getImageLink()).into(binding.imgShoppingTable);
        }
    }

}
