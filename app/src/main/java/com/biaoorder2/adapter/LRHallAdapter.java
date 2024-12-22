package com.biaoorder2.adapter;

import static com.biaoorder2.pool.ConstantPools.URL_DELETE_TABLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.manager.OrderManager;
import com.biaoorder2.bean.Table;
import com.biaoorder2.databinding.AdapterFramelayoutTableBinding;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.CustomDialog;
import com.biaoorder2.util.RequestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LRHallAdapter extends RecyclerView.Adapter<LRHallAdapter.HallViewHolder> {
    public List<Table> tableList;
    public Context mContext;
    public AdapterFramelayoutTableBinding binding;

    public LRHallAdapter(List<Table> tableList, Context mContext) {
        this.tableList = tableList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    // 加载 item_fragment.xml 布局来创建 ViewHolder。
    public HallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = AdapterFramelayoutTableBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HallViewHolder(binding);
    }

    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }

    @Override
    //指定位置的 fragment 绑定到 ViewHolder 中
    public void onBindViewHolder(@NonNull HallViewHolder holder, int position) {
        holder.bind(tableList.get(position));
        setTableInformation(tableList);
        holder.binding.imgTable.setOnClickListener(v -> {
            int positions = holder.getBindingAdapterPosition();
            if (positions != RecyclerView.NO_POSITION) {
                showDialog(tableList.get(position).getState(), tableList.get(position).getNo());
            }
        });
        holder.binding.imgTable.setOnLongClickListener(v -> {
            int positions = holder.getBindingAdapterPosition();
            if (positions != RecyclerView.NO_POSITION) {
                // 显示自定义对话框，确认删除
                CustomDialog.showIs(mContext, "是否确认删除此桌?", () -> {
                    // 删除操作，确保在主线程上更新 UI
                    deleteTable(tableList.get(positions).getNo(), mContext, positions);  // 传递位置
                });
            }
            return true;  // 返回 true，表示事件已处理
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

    // 删桌子
    public void deleteTable(int no, Context mContext, int position) {
        try {
            RequestUtils.delete(URL_DELETE_TABLE, "no", no, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    RequestUtils.handleError(mContext);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    tableList.remove(position);
                    ReToast.show(mContext, "此桌已删除...");
                    RequestUtils.handleResponse(response, mContext, () ->
                            ((Activity) mContext).runOnUiThread(() -> {
                                // 通知RecyclerView，删除了指定位置的项
                                notifyItemRemoved(position);
                                // 如果需要更新后续项的位置，调用此方法
                                notifyItemRangeChanged(position, tableList.size());
                            }));
                }
            });
        } catch (IOException e) {
            ReToast.show((Activity) mContext, "服务器出错qwq...");
        }
    }

    // 根据点击位置获得餐桌信息的状态显示dialog
    @SuppressLint("NotifyDataSetChanged")
    public void showDialog(String state, int pos) {
        CustomDialog.showDialog(mContext, state, pos, tableList);
    }

    // 获得保存的餐品信息
    public void setTableInformation(List<Table> tableList) {
        OrderManager orderManager = OrderManager.getInstance();
        if (tableList != null) {
            for (int i = 0; i < tableList.size(); i++) {
                Table table = tableList.get(i);
                orderManager.saveOrder(table.getNo(), table.getCurrentOrder());
                orderManager.setTotal(table.getNo(), table.getCurrentTotal());
            }
        }
    }
}
