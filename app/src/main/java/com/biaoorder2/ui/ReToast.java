package com.biaoorder2.ui;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
public class ReToast {
    public static void show(Activity activity , String text){
        activity.runOnUiThread(()->Toast.makeText(activity, text, Toast.LENGTH_SHORT).show());
    }
    public static void show(Context context , String text){
        ((Activity) context).runOnUiThread(()->Toast.makeText(context, text, Toast.LENGTH_SHORT).show());
    }
}
