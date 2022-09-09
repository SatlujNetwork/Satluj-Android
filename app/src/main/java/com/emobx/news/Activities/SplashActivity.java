package com.emobx.news.Activities;

import static com.emobx.news.Utils.Utils.updateOrientation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.ForceUpdateModel;
import com.emobx.news.Model.LoginModel;
import com.emobx.news.Model.PurchaseDetailModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "Splash";
    private NewsPreferences preferences;
    private TextView tv_version;
    private ApiInterface apiInterface;
    private String version = "", appName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences = new NewsPreferences(this);
        tv_version = findViewById(R.id.tv_version);

        showVersionName();
        callForceUpdate();
    }

    private void showVersionName() {
        try {
            PackageManager manager = getPackageManager();
            ApplicationInfo ai = manager.getApplicationInfo(
                    getPackageName(), 0);
            PackageInfo info = manager.getPackageInfo(
                    getPackageName(), 0);
            version = info.versionName;
            tv_version.setText(version);
            appName = (String) (ai != null ? manager.getApplicationLabel(ai) : "(unknown)");
        } catch (Exception e) {

        }
    }


    private void callForceUpdate() {
        if (!Utils.checkNetworkAvailability(this)) {
            setHandler();
            return;
        }
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);


        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<ForceUpdateModel> couponsObservable
                            = s.getForceUpdate(Constants.BASE_URl + "/api/app_versions?device_type=android").subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccess, this::handleError);

    }

    private void OnSuccess(ForceUpdateModel result) {
        Utils.hideProgressDialog();
        if (result != null
                && result.getLatestVersion() != null
                && result.getForceUpdate() != null
                && !version.trim().isEmpty()
                && !result.getLatestVersion().trim().isEmpty()) {

            if (Double.parseDouble(version) >= Double.parseDouble(result.getLatestVersion()))
                setHandler();
            else if (result.getForceUpdate() == 0)
                showUpdatePopup(false);
            else if (result.getForceUpdate() == 1)
                showUpdatePopup(true);
            else
                showUpdatePopup(false);
        }
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void setHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preferences.getUserData() != null
                        && preferences.getUserData().getLoginToken() != null
                        && !preferences.getUserData().getLoginToken().isEmpty()
                        && preferences.getUserData().getData() != null) {

                    LoginModel.Data data = preferences.getUserData().getData();

                    if (data.getId() != null && !data.getId().toString().isEmpty()
                            && data.getName() != null && !data.getName().isEmpty()
                            && data.getEmail() != null && !data.getEmail().isEmpty()) {

                        if (preferences.getUserData().getVerificationStatus() != null) {
                            if (preferences.getUserData().getVerificationStatus() == 1)
                                startActivity(new Intent(SplashActivity.this, HomeActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                Intent.FLAG_ACTIVITY_NEW_TASK));
                            else
                                startActivity(new Intent(SplashActivity.this, EmailVerificationActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                Intent.FLAG_ACTIVITY_NEW_TASK));
                        }

                    } else {
                        startActivity(new Intent(SplashActivity.this, SignupSocialActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, SignupSocialActivity.class));
                    finish();
                }
            }
        }, 3000);
    }

    private void showUpdatePopup(boolean forceUpdate) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.update_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.setCancelable(false);
        dialog.show();

       /* if (preferences.getTheme().equalsIgnoreCase("dark")) {
            dialog.findViewById(R.id.ll_popup).setBackgroundColor(getResources().getColor(R.color.black));
            ((TextView) dialog.findViewById(R.id.tv_text)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) dialog.findViewById(R.id.tv_login)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) dialog.findViewById(R.id.tv_cancel)).setTextColor(getResources().getColor(R.color.appBlackxLight));
        } else {
            dialog.findViewById(R.id.ll_popup).setBackgroundColor(getResources().getColor(R.color.white));
            ((TextView) dialog.findViewById(R.id.tv_text)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) dialog.findViewById(R.id.tv_login)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) dialog.findViewById(R.id.tv_cancel)).setTextColor(getResources().getColor(R.color.appBlackDark));
        }*/

        ((TextView) dialog.findViewById(R.id.tv_msg)).setText(appName + " recommends you to update the latest version of the app.");

        if (forceUpdate) {
            dialog.findViewById(R.id.tv_cancel).setVisibility(View.GONE);
        } else {
            dialog.findViewById(R.id.tv_cancel).setVisibility(View.VISIBLE);
        }

        dialog.findViewById(R.id.tv_update).setOnClickListener(v -> {
            updateApp();
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            dialog.cancel();
            setHandler();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);

    }

    private void updateApp() {
        String appPackageName = "";
        try {
            appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (Exception e) {
            try {
                appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

}
