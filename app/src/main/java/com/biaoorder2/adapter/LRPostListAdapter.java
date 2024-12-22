package com.biaoorder2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.R;
import com.biaoorder2.bean.OrderUser;
import com.biaoorder2.databinding.AdapterPowerBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class LRPostListAdapter extends RecyclerView.Adapter<LRPostListAdapter.powerListHolder> {

    public List<OrderUser> orderUserList;

    public Context mContext;

    public LRPostListAdapter(List<OrderUser> orderUserList, Context mContext) {
        this.orderUserList = orderUserList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public powerListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterPowerBinding binding = AdapterPowerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new powerListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull powerListHolder powerListHolder, int position) {
        OrderUser orderUser = orderUserList.get(position);
        powerListHolder.setBinding(orderUser, mContext);
    }

    @Override
    public int getItemCount() {
        return orderUserList.size();
    }

    public static class powerListHolder extends RecyclerView.ViewHolder {

        public AdapterPowerBinding binding;

        public powerListHolder(@NonNull AdapterPowerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setBinding(OrderUser orderUser, Context mContext) {
            binding.tvName.setText(orderUser.getUsername());
            binding.tvPower.setText(orderUser.getPost());
            Glide.with(mContext)
                    .load(orderUser.getHeadIcon())
                    .into(binding.imgHeadIcon);
            if (orderUser.getOnline() == 1) {
                binding.imgIsOnline.setImageResource(R.drawable.online);
            } else {
                binding.imgIsOnline.setImageResource(R.drawable.offline);
            }
        }
    }
}
