package com.emobx.news.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.LoginModel;
import com.emobx.news.Model.LogoutModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EmailVerificationActivity extends BaseActivity implements TextWatcher {

    private EditText et_otp1, et_otp2, et_otp3, et_otp4;
    private ApiInterface apiInterface;
    private NewsPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        init();
        listener();
        callSendOtpApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);
    }

    private void init() {
        preferences = new NewsPreferences(this);

        et_otp1 = findViewById(R.id.et_otp1);
        et_otp2 = findViewById(R.id.et_otp2);
        et_otp3 = findViewById(R.id.et_otp3);
        et_otp4 = findViewById(R.id.et_otp4);
    }

    private void listener() {
        et_otp1.addTextChangedListener(this);
        et_otp2.addTextChangedListener(this);
        et_otp3.addTextChangedListener(this);
        et_otp4.addTextChangedListener(this);

        findViewById(R.id.tv_update).setOnClickListener(v -> {
            if (et_otp1.getText().toString().isEmpty() &&
                    et_otp2.getText().toString().isEmpty() &&
                    et_otp3.getText().toString().isEmpty() &&
                    et_otp4.getText().toString().isEmpty())
                Toast.makeText(this, "Please enter otp", Toast.LENGTH_SHORT).show();
            else if (et_otp1.getText().toString().isEmpty() ||
                    et_otp2.getText().toString().isEmpty() ||
                    et_otp3.getText().toString().isEmpty() ||
                    et_otp4.getText().toString().isEmpty())
                Toast.makeText(this, "Please enter correct otp", Toast.LENGTH_SHORT).show();
            else
                callEmailVeriApi();
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.hashCode() == et_otp1.getText().hashCode() && et_otp1.length() == 1)
            et_otp2.requestFocus();
        else if (s.hashCode() == et_otp1.getText().hashCode() && et_otp1.length() == 0) {
            et_otp1.requestFocus();
            et_otp1.setSelection(0);
        }
        if (s.hashCode() == et_otp2.getText().hashCode() && et_otp2.length() == 1)
            et_otp3.requestFocus();
        else if (s.hashCode() == et_otp2.getText().hashCode() && et_otp2.length() == 0 && et_otp1.length() != 0) {
            et_otp1.requestFocus();
            et_otp1.setSelection(1);
        }
        if (s.hashCode() == et_otp3.getText().hashCode() && et_otp3.length() == 1)
            et_otp4.requestFocus();
        else if (s.hashCode() == et_otp3.getText().hashCode() && et_otp3.length() == 0 && et_otp2.length() != 0) {
            et_otp2.requestFocus();
            et_otp2.setSelection(1);
        }
        if (s.hashCode() == et_otp4.getText().hashCode() && et_otp4.length() == 1) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else if (s.hashCode() == et_otp4.getText().hashCode() && et_otp4.length() == 0 && et_otp3.length() != 0) {
            et_otp3.requestFocus();
            et_otp3.setSelection(1);
        }


        if (s.hashCode() == et_otp1.getText().hashCode() && et_otp1.length() > 1) {
            if (et_otp2.length() == 0)
                et_otp2.setText(s.toString().charAt(1) + "");

            StringBuilder sb = new StringBuilder(et_otp1.getText().toString());
            sb.deleteCharAt(1);
            et_otp1.setText(sb.toString());

            et_otp2.requestFocus();
            et_otp2.setSelection(1);
        }
        if (s.hashCode() == et_otp2.getText().hashCode() && et_otp2.length() > 1) {
            if (et_otp3.length() == 0)
                et_otp3.setText(s.toString().charAt(1) + "");

            StringBuilder sb = new StringBuilder(et_otp2.getText().toString());
            sb.deleteCharAt(1);
            et_otp2.setText(sb.toString());

            et_otp3.requestFocus();
            et_otp3.setSelection(1);
        }
        if (s.hashCode() == et_otp3.getText().hashCode() && et_otp3.length() > 1) {
            if (et_otp4.length() == 0)
                et_otp4.setText(s.toString().charAt(1) + "");

            StringBuilder sb = new StringBuilder(et_otp3.getText().toString());
            sb.deleteCharAt(1);
            et_otp3.setText(sb.toString());

            et_otp4.requestFocus();
            et_otp4.setSelection(1);
        }
    }

    private void callSendOtpApi() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("email", preferences.getUserData().getData().getEmail() + "");
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<LogoutModel> couponsObservable
                            = s.callSendOtp(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessSendOtp, this::handleError);

    }

    private void OnSuccessSendOtp(LogoutModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            } else
                Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void callEmailVeriApi() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("email", preferences.getUserData().getData().getEmail() + "");
        map.put("otp", et_otp1.getText().toString()
                + et_otp2.getText().toString()
                + et_otp3.getText().toString()
                + et_otp4.getText().toString());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<LoginModel> couponsObservable
                            = s.callEmailVeri(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessRegistration, this::handleError);

    }

    private void OnSuccessRegistration(LoginModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                preferences.setUserData(modelResult);
                startActivity(new Intent(this, HomeActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
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