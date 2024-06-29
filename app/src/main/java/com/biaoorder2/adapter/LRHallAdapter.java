package com.biaoorder2.adapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.bean.Table;
import com.biaoorder2.databinding.AdapterFramelayoutTableBinding;
import com.biaoorder2.util.setOnClickListener;

import java.util.List;

public class LRHallAdapter extends RecyclerView.Adapter<LRHallAdapter.HallViewHolder> {
    public List<Table> tableList;

    public setOnClickListener listener;
    private Context mContext;

    public LRHallAdapter(List<Table> tableList,setOnClickListener listener) {
        this.listener = listener;
        this.tableList = tableList;
    }

    @NonNull
    @Override
    // 加载 item_fragment.xml 布局来创建 ViewHolder。
    public HallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        AdapterFramelayoutTableBinding binding = AdapterFramelayoutTableBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HallViewHolder(binding);
    }

    @Override
    //指定位置的 fragment 绑定到 ViewHolder 中
    public void onBindViewHolder(@NonNull HallViewHolder holder, int position) {
        holder.bind(tableList.get(position));
        holder.itemView.setOnClickListener(v -> {
            int positions = holder.getBindingAdapterPosition();
            if (positions != RecyclerView.NO_POSITION && listener != null) {
                listener.onClick(positions);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public static class HallViewHolder extends RecyclerView.ViewHolder {

        private final AdapterFramelayoutTableBinding binding;

        public HallViewHolder(AdapterFramelayoutTableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Table table) {
            // 在这里可以通过 binding 对象访问布局文件中的控件并设置数据
            binding.tvNo.setText(String.valueOf(table.getNo()));
            binding.tvState.setText(table.getState());
            bindData(table.getState());
        }

        // 根据餐桌状态改变颜色
        public void bindData(String state) {
            switch (state) {
                case "0":
                    binding.imgState.setBackground(new ColorDrawable(Color.GREEN)); // 空闲状态为绿色背景
                    binding.tvState.setText("空闲");
                    break;
                case "1":
                    binding.imgState.setBackground(new ColorDrawable(Color.YELLOW)); // 已预订状态为黄色背景
                    binding.tvState.setText("已预订");
                    break;
                case "2":
                    binding.imgState.setBackground(new ColorDrawable(Color.RED)); // 用餐中状态为红色背景
                    binding.tvState.setText("用餐中");
                    break;
                default:
                    binding.imgState.setBackground(new ColorDrawable(Color.GRAY)); // 其他状态为灰色背景
                    binding.tvState.setText("餐桌异常");
                    break;
            }
        }
    }
}
