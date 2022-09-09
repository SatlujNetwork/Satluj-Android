package com.emobx.news.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.R;
import com.emobx.news.Utils.Utils;

public class SetThemeActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_light, tv_dark;
    private NewsPreferences preferences;
    private RadioButton rb_light, rb_dark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_theme);

        init();
        listener();
        updateTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);

    }

    private void init() {
        preferences = new NewsPreferences(this);

        findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
        findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title)).setText("Set theme");

        tv_light = findViewById(R.id.tv_light);
        tv_dark = findViewById(R.id.tv_dark);
        rb_light = findViewById(R.id.rb_light);
        rb_dark = findViewById(R.id.rb_dark);
    }

    private void listener() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        tv_light.setOnClickListener(this);
        tv_dark.setOnClickListener(this);
        rb_light.setOnClickListener(this);
        rb_dark.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_light:
                preferences.setTheme("light");
                sendBroadcast(new Intent("ChangeTheme"));
                updateTheme();
                break;
            case R.id.tv_dark:
                preferences.setTheme("dark");
                sendBroadcast(new Intent("ChangeTheme"));
                updateTheme();
                break;
            case R.id.rb_light:
                preferences.setTheme("light");
                sendBroadcast(new Intent("ChangeTheme"));
                updateTheme();
                break;
            case R.id.rb_dark:
                preferences.setTheme("dark");
                sendBroadcast(new Intent("ChangeTheme"));
                updateTheme();
                break;
        }
    }

    private void updateTheme() {
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            Utils.clearLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_white));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.black));
            findViewById(R.id.ll_main).setBackgroundColor(getResources().getColor(R.color.black));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) findViewById(R.id.tv_light)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) findViewById(R.id.tv_dark)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            rb_light.setChecked(false);
            rb_dark.setChecked(true);
        } else {
            Utils.setLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_black));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.white));
            findViewById(R.id.ll_main).setBackgroundColor(getResources().getColor(R.color.white));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) findViewById(R.id.tv_light)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) findViewById(R.id.tv_dark)).setTextColor(getResources().getColor(R.color.appBlackDark));
            rb_light.setChecked(true);
            rb_dark.setChecked(false);
        }
    }

}