package com.biaoorder2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.R;
import com.biaoorder2.bean.Category;
import com.biaoorder2.databinding.AdapterSiderbarBinding;

import com.biaoorder2.Interface.OnCategoryClickListener;


import java.util.List;

public class SidebarAdapter extends RecyclerView.Adapter<SidebarAdapter.SidebarHolder> {

    public List<Category> categoryList;

    private final OnCategoryClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION; // 当前选中项的位置
    public Context mContext;

    public SidebarAdapter(List<Category> categoryList, Context mContext, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SidebarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterSiderbarBinding binding = AdapterSiderbarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SidebarHolder(binding);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull SidebarHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.setBinding(category);


        // 设置选中和默认的文本颜色
        holder.binding.tvCategoryName.setBackgroundColor(position == selectedPosition ? Color.WHITE : mContext.getColor(R.color.grayish));

        // 设置点击事件监听器
        holder.itemView.setOnClickListener(v -> {
            setSelectedPosition(position);
            // 通知监听器选项被点击
            listener.OnCategoryClick(category.getName());
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // 设置选中项位置并刷新特定项目
    public void setSelectedPosition(int position) {
        if (position != selectedPosition) {
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition); // 刷新上一个选中的项目
            notifyItemChanged(selectedPosition); // 刷新当前选中项目
        }
    }

    public int getSelectedPosition(String categoryHeader) {
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            if (category.getName().equals(categoryHeader)) {
                Log.d("category.getName()", "setSelectedPosition: " + category.getName());
                return i;
            }
        }
        return -1;
    }


    public static class SidebarHolder extends RecyclerView.ViewHolder {

        public AdapterSiderbarBinding binding;

        public SidebarHolder(AdapterSiderbarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setBinding(Category category) {
            binding.tvCategoryName.setText(category.getName());
        }
    }
}
