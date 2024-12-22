package com.biaoorder2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.bean.Category;
import com.biaoorder2.databinding.AdapterOrdercategoryBinding;
import com.biaoorder2.Interface.OnCategoryClickListener;

import java.util.List;

public class LROrderCategoryAdapter extends RecyclerView.Adapter<LROrderCategoryAdapter.OrderCategoryHolder> {
    public Context mContext;

    public OnCategoryClickListener onCategoryClickListener;
    private final List<Category> categoryList;
    private int selectedPosition = 0; //设置选中项的索引 默认为首项

    public LROrderCategoryAdapter(Context mContext, List<Category> categoryList, OnCategoryClickListener onCategoryClickListener) {
        this.mContext = mContext;
        this.categoryList = categoryList;
        this.onCategoryClickListener = onCategoryClickListener;
    }

    @NonNull
    @Override
    public OrderCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        AdapterOrdercategoryBinding binding = AdapterOrdercategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderCategoryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderCategoryHolder holder, int position) {
        holder.setBinding(categoryList.get(position));
        holder.binding.linearCategory.setSelected(selectedPosition == holder.getBindingAdapterPosition());

        holder.binding.linearCategory.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();

            // 刷新前一个和当前选中项
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            // 点击后传递当前的菜品类别给需要者
            if (onCategoryClickListener != null) {
                onCategoryClickListener.OnCategoryClick(categoryList.get(position).getName());
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class OrderCategoryHolder extends RecyclerView.ViewHolder {

        private final AdapterOrdercategoryBinding binding;

        public OrderCategoryHolder(AdapterOrdercategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setBinding(Category category) {
            binding.tvCategory.setText(category.getName());
        }
    }
}
