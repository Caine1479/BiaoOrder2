package com.biaoorder2.adapter;

import static com.biaoorder2.pool.ConstantPools.URL_DELETE_ORDER;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.bean.HistoryBill;
import com.biaoorder2.databinding.AdapterHistorybillsBinding;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.CustomDialog;
import com.biaoorder2.util.RequestUtils;
import com.biaoorder2.Interface.setOnClickListener;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LRHistoryBillsAdapter extends RecyclerView.Adapter<LRHistoryBillsAdapter.HistoryBillsHolder> {
    public AdapterHistorybillsBinding binding;
    public List<HistoryBill> historyBillList;
    public setOnClickListener listener;
    public Context mContext;

    public LRHistoryBillsAdapter(List<HistoryBill> historyBillList, setOnClickListener listener, Context mContext) {
        this.historyBillList = historyBillList;
        this.listener = listener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public HistoryBillsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = AdapterHistorybillsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryBillsHolder(binding);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull HistoryBillsHolder holder, int position) {
        holder.bind(historyBillList.get(position));
        int orderNo = historyBillList.get(position).getOrderNo();
        holder.itemView.setOnClickListener(v -> {
            int positions = holder.getBindingAdapterPosition();
            if (positions != RecyclerView.NO_POSITION && listener != null) {
                listener.onClick(positions);
            }
        });
        holder.binding.imgDeleteOrder.setOnClickListener(v -> {
            // 执行删除的操作，提示框
            CustomDialog.showIs(mContext, "是否确认删除此订单?", () ->
                    deleteOrder(orderNo, mContext, position));
        });
    }

    @Override
    public int getItemCount() {
        return historyBillList.size();
    }

    public static class HistoryBillsHolder extends RecyclerView.ViewHolder {
        public final AdapterHistorybillsBinding binding;

        public HistoryBillsHolder(AdapterHistorybillsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HistoryBill historyBill) {
            binding.tvOrderNo.setText(String.valueOf(historyBill.getOrderNo() + 20240000));
            binding.tvPayTime.setText(historyBill.getDate());
            binding.tvTotalPrice.setText(historyBill.getPrice());
            binding.tvPayment.setText(historyBill.getPayment());
        }
    }

    // 删除历史订单
    public void deleteOrder(int orderNo, Context mContext, int position) {
        try {
            RequestUtils.delete(URL_DELETE_ORDER, "orderNo", orderNo, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    RequestUtils.handleError(mContext);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    RequestUtils.handleResponse(response, mContext, () -> {
                        historyBillList.remove(position);
                        ReToast.show((Activity) mContext, "订单已删除...");
                        ((Activity) mContext).runOnUiThread(() -> {
                            // 通知RecyclerView，删除了指定位置的项
                            notifyItemRemoved(position);
                            // 如果需要更新后续项的位置，调用此方法
                            notifyItemRangeChanged(position, historyBillList.size());
                        });
                    });
                }
            });
        } catch (IOException e) {
            ReToast.show((Activity) mContext, "服务器出错qwq...");
        }
    }
}