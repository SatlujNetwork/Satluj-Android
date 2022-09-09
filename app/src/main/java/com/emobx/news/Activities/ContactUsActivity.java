package com.emobx.news.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.LoginModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ContactUsActivity extends BaseActivity implements View.OnClickListener {

    private NewsPreferences preferences;
    private EditText et_name, et_email, et_comment;
    private TextView tv_submit;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        init();
        listener();
    }

    private void init() {
        preferences = new NewsPreferences(this);

        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_comment = findViewById(R.id.et_comment);
        tv_submit = findViewById(R.id.tv_submit);
        findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title)).setText("Contact Us");
        findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
    }

    private void listener() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        tv_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:
                if (et_name.getText().toString().isEmpty())
                    Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
                else if (et_email.getText().toString().isEmpty())
                    Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
                else if (et_comment.getText().toString().isEmpty())
                    Toast.makeText(this, "Please enter comment", Toast.LENGTH_SHORT).show();
                else
                    callContactUs();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            Utils.clearLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(getResources().getColor(R.color.black));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.black));
            ((TextView) findViewById(R.id.tv_name)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            et_name.setBackgroundColor(getResources().getColor(R.color.black));
            et_name.setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) findViewById(R.id.tv_email)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            et_email.setBackgroundColor(getResources().getColor(R.color.black));
            et_email.setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) findViewById(R.id.tv_text)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            et_comment.setBackground(getResources().getDrawable(R.drawable.corner_round_edit_text_dark));
            et_comment.setTextColor(getResources().getColor(R.color.appBlackxLight));
            findViewById(R.id.view1).setBackgroundColor(getResources().getColor(R.color.viewColorDark));
            findViewById(R.id.view2).setBackgroundColor(getResources().getColor(R.color.viewColorDark));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_white));
        } else {
            Utils.setLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(getResources().getColor(R.color.white));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.white));
            ((TextView) findViewById(R.id.tv_name)).setTextColor(getResources().getColor(R.color.appBlue));
            et_name.setBackgroundColor(getResources().getColor(R.color.white));
            et_name.setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) findViewById(R.id.tv_email)).setTextColor(getResources().getColor(R.color.appBlue));
            et_email.setBackgroundColor(getResources().getColor(R.color.white));
            et_email.setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) findViewById(R.id.tv_text)).setTextColor(getResources().getColor(R.color.appBlue));
            et_comment.setBackground(getResources().getDrawable(R.drawable.corner_round_edit_text));
            et_comment.setTextColor(getResources().getColor(R.color.appBlackDark));
            findViewById(R.id.view1).setBackgroundColor(getResources().getColor(R.color.viewColor));
            findViewById(R.id.view2).setBackgroundColor(getResources().getColor(R.color.viewColor));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_black));
        }
    }

    private void callContactUs() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("name", et_name.getText().toString());
        map.put("email", et_email.getText().toString());
        map.put("message", et_comment.getText().toString());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<LoginModel> couponsObservable
                            = s.callContactUs(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessContactUs, this::handleError);

    }

    private void OnSuccessContactUs(LoginModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                Toast.makeText(this, "Submitted successfully.", Toast.LENGTH_SHORT).show();
                et_name.setText("");
                et_email.setText("");
                et_comment.setText("");
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            } else
                Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}