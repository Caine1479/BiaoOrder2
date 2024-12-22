package com.biaoorder2.manager;

import static com.biaoorder2.pool.ConstantPools.URL_GET_VEGETABLE;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.biaoorder2.bean.Category;
import com.biaoorder2.bean.VegetableInformation;
import com.biaoorder2.ui.ReToast;
import com.biaoorder2.util.RequestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VegetableManager {
    private static VegetableManager instance;
    private List<VegetableInformation> vegetableList;

    private Map<String, List<VegetableInformation>> vegetableMap;

    // 私有构造函数，确保单例
    private VegetableManager() {
        vegetableList = new ArrayList<>();
        vegetableMap = new HashMap<>();
    }

    public static synchronized VegetableManager getInstance() {
        if (instance == null) {
            instance = new VegetableManager();
        }
        return instance;
    }

    // 添加指定的菜品种类集合
    public void addVegetableList(String category, List<VegetableInformation> vegetableList) {
        vegetableMap.put(category, vegetableList);
    }

    // 删除指定的菜品种类集合
    public void deleteVegetableList(String category) {
        vegetableMap.remove(category);
    }

    // 清空集合
    public void clearVegetableMap() {
        if (vegetableMap != null) {
            vegetableMap.clear();
        }
    }

    // 添加单独项目到指定集合中
    public void addSingleVegetable(String category, VegetableInformation vegetableInformation) {
        List<VegetableInformation> existingList = vegetableMap.get(category);
        if (existingList != null) {
            // 将新项添加到集合中
            existingList.add(vegetableInformation);
        } else {
            // 如果不存在，则创建一个新的集合并添加
            List<VegetableInformation> newList = new ArrayList<>();
            newList.add(vegetableInformation);
            vegetableMap.put(category, newList);
        }
    }

    // 删除指定的单独菜品
    public void deleteVegetable(String category, VegetableInformation vegetableInformation) {
        List<VegetableInformation> existingList = vegetableMap.get(category);
        if (existingList != null) {
            existingList.remove(vegetableInformation);
            // 如果类别中的菜品列表为空，考虑是否要移除该类别
            if (existingList.isEmpty()) {
                vegetableMap.remove(category);
            }
        }
    }

    // 更新指定id的菜品信息
    public void updateVegetable(int id, VegetableInformation vegetableInformation){
        VegetableInformation pre_VegetableInformation = vegetableList.get(id);
        if (pre_VegetableInformation!=null){
        }

    }


    // 获得map中的指定集合
    public List<VegetableInformation> getVegteableList(String category) {
        return vegetableMap.getOrDefault(category, null);
    }


    public List<VegetableInformation> getVegetableList() {
        return vegetableList;
    }

    public void setVegetableList(List<VegetableInformation> vegetableList) {
        this.vegetableList = vegetableList;
    }

    // 添加菜品
    public void addVegetable(VegetableInformation vegetableInformation) {
        this.vegetableList.add(vegetableInformation);
    }

    // 删除总菜品
    public void deleteVegetable(VegetableInformation vegetableInformation) {
        this.vegetableList.remove(vegetableInformation);
    }


    public void getVegetableList_Http(Context context) {
        RequestUtils.get1(URL_GET_VEGETABLE, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                RequestUtils.handleError(context);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                List<VegetableInformation> vegetables = new ArrayList<>();
                // 获取handleGetResponse返回的结果
                String result = RequestUtils.handleGetResponse(response, context);
                if (result != null) {
                    try {
                        JSONObject data = new JSONObject(result);
                        JSONArray jsonArray = data.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String name = jsonObject.getString("name");
                            String price = jsonObject.getString("price");
                            String imageLink = jsonObject.getString("imageLink");
                            int sales = jsonObject.getInt("sales");
                            String category = jsonObject.getString("category");
                            vegetables.add(new VegetableInformation(id, name, price, imageLink, sales, category));
                        }
                        vegetableList.clear();
                        setVegetableList(vegetables);
                    } catch (JSONException e) {
                        ReToast.show((Activity) context, "服务器出错,请联系管理员...");
                    }
                }
            }
        });
    }

    // 将菜品排序分类后，分别装入Map中
    public void classifyList(List<VegetableInformation> vegetableList) {
        if (vegetableList != null) {
            CategoryManager categoryManager = CategoryManager.getInstance();
            List<Category> categoryList = categoryManager.getCategoryList();

            // 根据菜品类别排序
            vegetableList.sort(Comparator.comparing(VegetableInformation::getCategory));
            categoryList.clear();
            String category = "";
            // 初始化集合
            clearVegetableMap();
            // 全部菜品，全部菜品选项
            categoryList.add(new Category("全部菜品"));
            addVegetableList("全部菜品", vegetableList);
            // 根据菜品类别分类
            // 此时循环的是分类后的菜品集合
            for (int i = 0; i < vegetableList.size(); i++) {
                String v_category = vegetableList.get(i).getCategory();
                if (!v_category.equals(category)) {// 菜品种类不同时
                    category = v_category;
                    categoryList.add(new Category(category));
                }
                addSingleVegetable(v_category, vegetableList.get(i));
            }
            // for循环结束后 categoryList整理完成 vegetableList整理完成
        }
    }
}
