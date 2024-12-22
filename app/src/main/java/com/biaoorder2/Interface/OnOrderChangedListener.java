package com.biaoorder2.Interface;

import com.biaoorder2.bean.VegetableInformation;

import java.util.List;

public interface OnOrderChangedListener {
    void onOrderChanged(List<VegetableInformation> list);
}
