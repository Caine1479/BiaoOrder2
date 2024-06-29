package com.biaoorder2.adapter;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.biaoorder2.bean.VegetableInformation;
import com.biaoorder2.databinding.AdapterOrderVegetableBinding;
import com.biaoorder2.util.OnOrderAddedListener;
import com.biaoorder2.util.setOnClickListener;
import com.bumptech.glide.Glide;

import java.util.List;

public class LROrderAdapter extends RecyclerView.Adapter<LROrderAdapter.HallViewHolder> {


    public setOnClickListener listener;
    public Context mContext;
    public OnOrderAddedListener onOrderAddedListener;

    public List<VegetableInformation> vegetableList;
    public LROrderAdapter(List<VegetableInformation> vegetableList,setOnClickListener listener,OnOrderAddedListener onOrderAddedListener) {
        this.listener = listener;
        this.vegetableList = vegetableList;
        this.onOrderAddedListener = onOrderAddedListener;
    }

    @NonNull
    @Override
    // 加载 item_fragment.xml 布局来创建 ViewHolder。
    public HallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        AdapterOrderVegetableBinding binding = AdapterOrderVegetableBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HallViewHolder(binding);
    }

    @Override
    //指定位置的 fragment 绑定到 ViewHolder 中
    public void onBindViewHolder(@NonNull HallViewHolder holder, int position) {
        holder.bind(vegetableList.get(position),mContext);
        holder.binding.btnDishAdd.setOnClickListener(v -> {
            int positions = holder.getBindingAdapterPosition();
            if (positions != RecyclerView.NO_POSITION && listener != null) {
                listener.onClick(positions);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vegetableList.size();
    }

    public static class HallViewHolder extends RecyclerView.ViewHolder {

        private final AdapterOrderVegetableBinding binding;

        public HallViewHolder(AdapterOrderVegetableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(VegetableInformation vi,Context mContext) {
            // 在这里可以通过 binding 对象访问布局文件中的控件并设置数据
            binding.tvPrice.setText(vi.getPrice());
            binding.tvName.setText(vi.getName());
            Glide.with(mContext).load(vi.getImageLink()).into(binding.imgDish);
        }

    }
}
