package com.emobx.news.tvapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

import com.emobx.news.Activities.SplashActivity;
import com.emobx.news.R;

public class TVActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvactivity);
        startActivity(new Intent(getApplicationContext(), SplashActivity.class));
    }
}