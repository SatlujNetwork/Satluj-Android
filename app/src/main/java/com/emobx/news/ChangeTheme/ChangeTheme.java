package com.emobx.news.ChangeTheme;

import android.app.Activity;
import android.widget.TextView;

import com.emobx.news.R;

import java.util.ArrayList;

public class ChangeTheme {
    public static void changeLightTheme(Activity activity, ArrayList<TextView> arr_tv) {
        for (int i = 0; i < arr_tv.size(); i++) {
            arr_tv.get(i).setTextColor(activity.getResources().getColor(R.color.appBlackDark));
        }
    }

    public static void changeDarkTheme(Activity activity, ArrayList<TextView> arr_tv) {
        for (int i = 0; i < arr_tv.size(); i++) {
            arr_tv.get(i).setTextColor(activity.getResources().getColor(R.color.appBlackxLight));
        }
    }
}
