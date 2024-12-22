package com.biaoorder2.manager;

import com.biaoorder2.bean.Category;
import com.biaoorder2.bean.VegetableInformation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryManager {
    private static CategoryManager instance;

    private List<Category> categoryList;

    public CategoryManager() {
        categoryList = new ArrayList<>();

    }

    public static synchronized CategoryManager getInstance() {
        if (instance == null) {
            instance = new CategoryManager();
        }
        return instance;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    // 将菜品排序分类
    public List<Category> classifyCategory(List<VegetableInformation> vegetableList) {
        if (vegetableList != null) {
            categoryList.clear();
            String category = "";
            // 根据菜品类别获得分类标题
            for (int i = 0; i < vegetableList.size(); i++) {
                String v_category = vegetableList.get(i).getCategory();
                if (!v_category.equals(category)) {// 菜品种类不同时
                    category = v_category;
                    categoryList.add(new Category(category));
                }
            }
        }
        return categoryList;
    }
}
