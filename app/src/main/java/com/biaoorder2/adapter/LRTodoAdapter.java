package com.biaoorder2.adapter;

import static com.biaoorder2.pool.ConstantPools.URL_DELETE_TODO;
import static com.biaoorder2.pool.ConstantPools.URL_UPDATE_DONE_ENROLL;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.R;

import com.biaoorder2.bean.EnrollInformation;
import com.biaoorder2.databinding.AdapterTodoBinding;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.CustomDialog;
import com.biaoorder2.util.RequestUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LRTodoAdapter extends RecyclerView.Adapter<LRTodoAdapter.TodoHolder> {

    public List<EnrollInformation> enrollInformationList;

    public Context mContext;

    public LRTodoAdapter(List<EnrollInformation> enrollInformationList, Context mContext) {
        this.enrollInformationList = enrollInformationList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public TodoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterTodoBinding binding = AdapterTodoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TodoHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoHolder todoHolder, int position) {
        EnrollInformation enrollInformation = enrollInformationList.get(position);
        todoHolder.binding(enrollInformation);

        // 点击处理操作
        todoHolder.binding.linerTodo.setOnClickListener(v -> {
            // 打开对话框
            CustomDialog.showEvents(mContext, enrollInformation, () ->
                    doneEnroll(enrollInformation, position));

        });

        // 删除信息
        todoHolder.binding.imgDeleteTodo.setOnClickListener(v -> {
            if (enrollInformation.getEventIsDone().equals("1")) {
                CustomDialog.showIs(mContext, "是否删除已处理的申请?", () ->
                        deleteTodo(enrollInformation.getEventTime(), mContext, position));
            } else {
                CustomDialog.showIs(mContext, "该申请未处理,是否确认删除?", () ->
                        deleteTodo(enrollInformation.getEventTime(), mContext, position));
            }

        });
    }

    @Override
    public int getItemCount() {
        return enrollInformationList.size();
    }

    public static class TodoHolder extends RecyclerView.ViewHolder {

        public AdapterTodoBinding binding;

        public TodoHolder(@NonNull AdapterTodoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void binding(EnrollInformation enrollInformation) {

            if (!enrollInformation.getEventIsDone().equals("0")) {
                binding.tvIsDo.setText("已处理");
                binding.imgIsDo.setBackgroundResource(R.drawable.ok);
            }
        }
    }

    // 确认注册请求
    public void doneEnroll(EnrollInformation enrollInformation, int position) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", enrollInformation.getId());
            jsonObject.put("password", enrollInformation.getPassword());
            jsonObject.put("username", enrollInformation.getUsername());
            jsonObject.put("headIcon", enrollInformation.getHeadIcon());
            RequestUtils.post(URL_UPDATE_DONE_ENROLL, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    RequestUtils.handleError(mContext);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    RequestUtils.handleResponse(response, mContext, () -> {
                        ReToast.show(mContext, "申请已通过!");
                        ((Activity) mContext).runOnUiThread(() -> {
                            // 通知RecyclerView，删除了指定位置的项
                            notifyItemChanged(position);
                        });
                    });

                }
            });
        } catch (JSONException e) {
            ReToast.show(mContext, "服务器出错qwq...");
        }
    }

    // 删除todo信息
    public void deleteTodo(String event_time, Context mContext, int position) {
        try {
            RequestUtils.delete(URL_DELETE_TODO, "event_time", event_time, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    RequestUtils.handleError(mContext);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    enrollInformationList.remove(position);
                    ReToast.show(mContext, "信息已删除...");
                    RequestUtils.handleResponse(response, mContext, () ->
                            ((Activity) mContext).runOnUiThread(() -> {
                                // 通知RecyclerView，删除了指定位置的项
                                notifyItemRemoved(position);
                                // 如果需要更新后续项的位置，调用此方法
                                notifyItemRangeChanged(position, enrollInformationList.size());
                            }));
                }
            });
        } catch (IOException e) {
            ReToast.show(mContext, "服务器出错qwq...");
        }
    }
}
