package com.emobx.news.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.LogoutModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    private ApiInterface apiInterface;
    private EditText et_email;
    private TextView tv_signin;
    private NewsPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
        listener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);

    }

    private void init() {
        preferences = new NewsPreferences(this);
        findViewById(R.id.iv_back).setVisibility(View.VISIBLE);

        et_email = findViewById(R.id.et_email);
        tv_signin = findViewById(R.id.tv_signin);
    }

    private void listener() {
        findViewById(R.id.iv_back).setOnClickListener(v -> {
            onBackPressed();
        });
        tv_signin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_signin:
                if (et_email.getText().toString().isEmpty())
                    Toast.makeText(this, getResources().getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
                else if (!Utils.isEmailValid(et_email.getText().toString()))
                    Toast.makeText(this, getResources().getString(R.string.please_enter_correct_email), Toast.LENGTH_SHORT).show();
                else
                    callForgotApi();
                break;
        }
    }

    private void callForgotApi() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("email", et_email.getText().toString());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<LogoutModel> couponsObservable
                            = s.callForgot(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessForgot, this::handleError);

    }

    private void OnSuccessForgot(LogoutModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                Toast.makeText(this, "Reset password link has been sent to your E-mail", Toast.LENGTH_SHORT).show();
                et_email.setText("");
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