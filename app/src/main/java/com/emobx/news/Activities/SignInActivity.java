package com.emobx.news.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.LoginModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_signUp, tv_forgot, tv_signin;
    private ApiInterface apiInterface;
    private EditText et_email, et_password;
    private NewsPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
        listener();
    }

    private void init() {
        preferences = new NewsPreferences(this);

        findViewById(R.id.iv_back_arrow).setVisibility(View.VISIBLE);
        tv_signUp = findViewById(R.id.tv_signUp);
        tv_forgot = findViewById(R.id.tv_forgot);
        tv_signin = findViewById(R.id.tv_signin);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

    }

    private void listener() {
        tv_signUp.setOnClickListener(this);
        tv_forgot.setOnClickListener(this);
        tv_signin.setOnClickListener(this);
        findViewById(R.id.iv_back_arrow).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_arrow:
                onBackPressed();
                break;
            case R.id.tv_signin:
                if (et_email.getText().toString().isEmpty())
                    Toast.makeText(this, getResources().getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
                else if (!Utils.isEmailValid(et_email.getText().toString().trim()))
                    Toast.makeText(this, getResources().getString(R.string.please_enter_correct_email), Toast.LENGTH_SHORT).show();
                else if (et_password.getText().toString().isEmpty())
                    Toast.makeText(this, getResources().getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
                /*else if (et_password.getText().toString().length() < 8)
                    Toast.makeText(this, getResources().getString(R.string.please_enter_correct_pass), Toast.LENGTH_SHORT).show();
                */
                else
                    callLoginApi();
                break;
            case R.id.tv_signUp:
                onBackPressed();
                break;
            case R.id.tv_forgot:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }


    private void callLoginApi() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
//        map.put("name", et_name.getText().toString());
//        map.put("username", );
        map.put("password", et_password.getText().toString());
//        map.put("phone", );
//        map.put("image", );
        map.put("email", et_email.getText().toString());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<LoginModel> couponsObservable
                            = s.callLogin(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessRegistration, this::handleError);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);

    }

    private void OnSuccessRegistration(LoginModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                preferences.setUserData(modelResult);
                if (modelResult.getVerificationStatus() != null)
                    if (modelResult.getVerificationStatus() == 1)
                        startActivity(new Intent(this, HomeActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK));
                    else
                        startActivity(new Intent(this, EmailVerificationActivity.class)
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