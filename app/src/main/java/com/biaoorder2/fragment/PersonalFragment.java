package com.biaoorder2.fragment;

import static android.content.Context.MODE_PRIVATE;

import static com.biaoorder2.pool.ConstantPools.URL_GET_ENROLL;
import static com.biaoorder2.pool.ConstantPools.URL_GET_POWER_LIST;
import static com.biaoorder2.pool.ConstantPools.URL_UPDATE_LOGOUT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.activity.LoginActivity;
import com.biaoorder2.activity.MenuActivity;
import com.biaoorder2.activity.PowerActivity;
import com.biaoorder2.activity.SalesActivity;
import com.biaoorder2.adapter.LRPostListAdapter;
import com.biaoorder2.bean.EnrollInformation;
import com.biaoorder2.bean.OrderUser;
import com.biaoorder2.databinding.FragmentPersonalBinding;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.CustomDialog;

import com.biaoorder2.util.RequestUtils;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class PersonalFragment extends Fragment {

    private FragmentPersonalBinding binding;
    private SharedPreferences preferences;
    public RecyclerView postRecyclerView;
    public List<EnrollInformation> enrollInformationList = new ArrayList<>();
    public List<OrderUser> orderUserList = new ArrayList<>();
    public LRPostListAdapter powerListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentPersonalBinding.inflate(getLayoutInflater());
        preferences = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //当 Fragment 需要创建它的视图层次结构（View hierarchy）时调用, 创建并返回 Fragment 的 UI 布局
        //初始化布局，但不进行视图的具体操作（如数据填充、监听器设置等）
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //当 onCreateView 返回的 View 被创建后立即调用，意味着此时 Fragment 的视图层次结构已经被生成。
        //用于执行与视图操作相关的逻辑，例如设置数据、绑定视图和控件的监听器等。
        super.onViewCreated(view, savedInstanceState);

        Glide.with(requireContext())
                .load(preferences.getString("headIcon", null))
                .circleCrop()// 圆形裁剪
                .into(binding.tvHeadshot);
        binding.tvName.setText(preferences.getString("username", null));

        // 获得职位
        String post = preferences.getString("post", null);
        binding.tvPost.setText(post);

        // 获得消息列表
        if (post != null && (post.equals("管理员") || post.equals("店长"))) {
            getEnrollList(enrollInformationList1 -> {
                int isDone = getIsDone(enrollInformationList);
                binding.tvCard.setText(String.valueOf(isDone));
            });
        }

        // 退出登录
        binding.linearExit.setOnClickListener(v -> CustomDialog.isExit(requireContext(), isExit -> {
            if (isExit) {
                // 更新登录状态
                String id = preferences.getString("id", null);
                logout(id, requireContext());

                // 清除jwt
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finish();
            }
        }));

        // 营业统计
        binding.linearSales.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), SalesActivity.class)));

        // 菜单设置
        binding.linearMenu.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), MenuActivity.class)));

        // 请求处理
        binding.imgTodo.setOnClickListener(v ->
                CustomDialog.showTodo(requireContext(), enrollInformationList));

        // 账户管理
        binding.linerPower.setOnClickListener(v -> {
                    if (post != null && (post.equals("管理员") || post.equals("店长"))) {
                        startActivity(new Intent(requireActivity(), PowerActivity.class));
                    } else {
                        ReToast.show(requireActivity(), "无权限查看...");
                    }
                }
        );


        // 获得职位列表
        getPower(requireContext());
        // 职位列表
        postRecyclerView = binding.recyclerViewPowerList;
        postRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        powerListAdapter = new LRPostListAdapter(orderUserList, requireContext());
        postRecyclerView.setAdapter(powerListAdapter);
    }

    // 获得消息列表
    public void getEnrollList(OnEnrollListLoaded callback) {
        RequestUtils.get1(URL_GET_ENROLL, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                RequestUtils.handleError(requireContext());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                List<EnrollInformation> list = new ArrayList<>();
                String getResponse = RequestUtils.handleGetResponse(response, requireContext());
                try {
                    if (getResponse != null) {
                        JSONObject data = new JSONObject(getResponse);
                        JSONArray jsonArray = data.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString("id");
                            String password = jsonObject.getString("password");
                            String username = jsonObject.getString("username");
                            String eventTime = jsonObject.getString("eventTime");
                            String headIcon = jsonObject.getString("headIcon");
                            String eventIsDone = jsonObject.getString("eventIsDone");
                            list.add(new EnrollInformation(id, password, username, headIcon, eventTime, eventIsDone));
                        }
                        enrollInformationList.clear();
                        enrollInformationList.addAll(list);
                        // 通知回调
                        requireActivity().runOnUiThread(() -> callback.onLoaded(enrollInformationList));
                    }
                } catch (JSONException e) {
                    ReToast.show(requireActivity(), "服务器出错,请联系管理员...");
                }
            }
        });
    }


    // 获得职员信息
    public void getPower(Context mContext) {
        RequestUtils.get1(URL_GET_POWER_LIST, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                RequestUtils.handleError(mContext);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                String getResponse = RequestUtils.handleGetResponse(response, mContext);
                List<OrderUser> orderUsers = new ArrayList<>();
                try {
                    if (getResponse != null) {
                        JSONObject data = new JSONObject(getResponse);
                        JSONArray jsonArray = data.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String username = jsonObject.getString("username");
                            String headIcon = jsonObject.getString("headIcon");
                            String post = jsonObject.getString("post");
                            int online = jsonObject.getInt("online");
                            orderUsers.add(new OrderUser(headIcon, username, post, online));
                        }
                        orderUserList.clear();
                        orderUserList.addAll(orderUsers);

                        // 数据更新后通知适配器更新数据
                        requireActivity().runOnUiThread(() ->
                                powerListAdapter.notifyDataSetChanged());
                    }
                } catch (JSONException e) {
                    ReToast.show(mContext, "服务器出错,请联系管理员...");
                }
            }
        });
    }

    // 获得消息数量
    public int getIsDone(List<EnrollInformation> enrollInformationList) {
        // 未处理事件数量
        int events = 0;
        for (int i = 0; i < enrollInformationList.size(); i++) {
            if (enrollInformationList.get(i).getEventIsDone().equals("0")) {
                events += 1;
            }
        }
        return events;
    }

    // 回调接口
    public interface OnEnrollListLoaded {
        void onLoaded(List<EnrollInformation> enrollInformationList);
    }

    // 退出登录更新消息状态
    public void logout(String id, Context mContext) {
        RequestUtils.get(URL_UPDATE_LOGOUT, "id", id, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                RequestUtils.handleError(requireContext());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                RequestUtils.handleResponse(response, mContext, () ->
                        ReToast.show(requireActivity(), "已退出登录..."));
            }
        });
    }
}
