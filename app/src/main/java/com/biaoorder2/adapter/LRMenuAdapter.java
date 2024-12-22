package com.biaoorder2.adapter;

import static com.biaoorder2.pool.ConstantPools.URL_DELETE_VEGETABLE;
import static com.biaoorder2.pool.ConstantPools.URL_UPDATE_MENU;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.Interface.OnOrderAddedListener;
import com.biaoorder2.bean.VegetableInformation;
import com.biaoorder2.databinding.AdapterMenuBinding;
import com.biaoorder2.manager.VegetableManager;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.CustomDialog;
import com.biaoorder2.util.RequestUtils;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LRMenuAdapter extends RecyclerView.Adapter<LRMenuAdapter.MenuHolder> {
    public List<VegetableInformation> vegetableList;
    public Context mContext;

    public LRMenuAdapter(Context mContext, List<VegetableInformation> vegetableList) {
        this.mContext = mContext;
        this.vegetableList = vegetableList;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterMenuBinding binding = AdapterMenuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MenuHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuHolder holder, int position) {
        VegetableInformation vegetableInformation = vegetableList.get(position);
        holder.binding(mContext, vegetableInformation);

        // 删除菜品
        holder.binding.btnDishDelete.setOnClickListener(v ->
                CustomDialog.showIs(mContext, "是否确认删除此菜品?", () ->
                        deleteVegetable(vegetableInformation.getId(), position, mContext)));

        // 修改菜品信息
        holder.binding.imgEdit.setOnClickListener(v ->
                CustomDialog.editMenu(mContext, vegetableInformation, () ->
                        updateVegetable(vegetableInformation, position, mContext)));

    }

    @Override
    public int getItemCount() {
        return vegetableList.size();
    }

    public void setVegetableList(List<VegetableInformation> vegetableList) {
        this.vegetableList = vegetableList;
    }

    public static class MenuHolder extends RecyclerView.ViewHolder {
        public AdapterMenuBinding binding;

        public MenuHolder(@NonNull AdapterMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void binding(Context mContext, VegetableInformation vegetableInformation) {
            binding.tvName.setText(vegetableInformation.getName());
            binding.tvPrice.setText(vegetableInformation.getPrice());
            binding.tvSales.setText(String.valueOf(vegetableInformation.getSales()));
            Glide.with(mContext).load(vegetableInformation.getImageLink()).into(binding.imgDish);
        }
    }


    // 删除菜品
    public void deleteVegetable(int id, int position, Context mContext) {
        try {
            RequestUtils.delete(URL_DELETE_VEGETABLE, "id", id, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    RequestUtils.handleError(mContext);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    RequestUtils.handleResponse(response, mContext, () -> {
                        VegetableInformation vegetableInformation = vegetableList.get(position);
                        vegetableList.remove(position);
                        VegetableManager.getInstance().deleteVegetable(vegetableInformation.getCategory(), vegetableInformation);
                        ReToast.show((Activity) mContext, "菜品已删除");
                        ((Activity) mContext).runOnUiThread(() -> {
                            // 通知RecyclerView，删除了指定位置的项
                            notifyItemRemoved(position);
                            // 如果需要更新后续项的位置，调用此方法
                            notifyItemRangeChanged(position, vegetableList.size());
                        });
                    });

                }
            });
        } catch (IOException e) {
            ReToast.show(mContext, "服务器出错qwq...");
        }
    }

    // 更新菜品信息
    public void updateVegetable(VegetableInformation vegetableInformation, int position, Context mContext) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", vegetableInformation.getId());
            jsonObject.put("name", vegetableInformation.getName());
            jsonObject.put("price", vegetableInformation.getPrice());
            jsonObject.put("imageLink", vegetableInformation.getImageLink());
            jsonObject.put("sales", vegetableInformation.getSales());
            jsonObject.put("category", vegetableInformation.getCategory());
            RequestUtils.post(URL_UPDATE_MENU, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    RequestUtils.handleError(mContext);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    RequestUtils.handleResponse(response, mContext, () -> {
                        ReToast.show((Activity) mContext, "菜品信息已修改");
                        ((Activity) mContext).runOnUiThread(() -> {
                            // 通知RecyclerView，删除了指定位置的项
                            notifyItemChanged(position);
                        });
                    });
                }
            });
        } catch (JSONException ignored) {
            ReToast.show((Activity) mContext, "服务器出错qwq...");
        }
    }
}
