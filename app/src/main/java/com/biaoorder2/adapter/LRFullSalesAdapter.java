package com.biaoorder2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.bean.VegetableInformation;
import com.biaoorder2.databinding.AdapterFullsalesBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class LRFullSalesAdapter extends RecyclerView.Adapter<LRFullSalesAdapter.FullSalesHolder> {

    public AdapterFullsalesBinding binding;
    public List<VegetableInformation> vegetableList;
    public Context mContext;

    public LRFullSalesAdapter(Context mContext, List<VegetableInformation> vegetableList) {
        this.mContext = mContext;
        this.vegetableList = vegetableList;
    }

    @NonNull
    @Override
    public FullSalesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = AdapterFullsalesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FullSalesHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FullSalesHolder holder, int position) {
        holder.binding(mContext, vegetableList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return vegetableList.size();
    }

    public static class FullSalesHolder extends RecyclerView.ViewHolder {
        public AdapterFullsalesBinding binding;

        public FullSalesHolder(@NonNull AdapterFullsalesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void binding(Context mContext, VegetableInformation vegetableInformation, int position) {
            binding.tvRank.setText(String.valueOf(position + 1));
            Glide.with(mContext).load(vegetableInformation.getImageLink()).into(binding.imgVegetable);
            binding.tvVegetableName.setText(vegetableInformation.getName());
            binding.tvVegetableSales.setText(String.valueOf(vegetableInformation.getSales()));
        }
    }
}
