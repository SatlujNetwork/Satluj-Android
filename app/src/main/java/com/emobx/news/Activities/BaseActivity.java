package com.emobx.news.Activities;

import static com.emobx.news.Utils.Utils.updateOrientation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.emobx.news.Utils.Utils;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateOrientation(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);
    }
}
