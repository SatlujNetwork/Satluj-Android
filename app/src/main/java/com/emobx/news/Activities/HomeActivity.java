package com.emobx.news.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Application;
import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Fragments.CategoriesFragment;
import com.emobx.news.Fragments.HomeNewFragment;
import com.emobx.news.Fragments.ProfileFragment;
import com.emobx.news.Fragments.VideoFragment;
import com.emobx.news.Model.ForceUpdateModel;
import com.emobx.news.Model.LoginModel;
import com.emobx.news.Model.NotificationCountModel;
import com.emobx.news.Permission.CheckPermissions;
import com.emobx.news.R;
import com.emobx.news.RealmDB.TaskListApplication;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    public Fragment currentFragment;
    private FrameLayout fl_view;
    private RelativeLayout rl_home, rl_video, rl_categories, rl_profile;
    private ImageView iv_home, iv_video, iv_categories, iv_profile;
    private TextView tv_home, tv_video, tv_categories, tv_profile, tv_notification_count;
    private NewsPreferences preferences;
    private BroadcastReceiver receiver;
    private ApiInterface apiInterface;
    private String version = "", appName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_News);
        setContentView(R.layout.activity_home);
        showVersionName();

        new CheckPermissions().checkPermissions(this);

        updateTheme();
        init();
        listener();

    }

    private void updateTheme() {
        preferences = new NewsPreferences(this);
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            Utils.clearLightStatusBar(findViewById(android.R.id.content).getRootView(), this);

            ((ImageView) findViewById(R.id.iv_home_logo)).setImageDrawable(getResources().getDrawable(R.drawable.home_white_logo));
            ((ImageView) findViewById(R.id.iv_notification)).setImageDrawable(getResources().getDrawable(R.drawable.notification_white));
            ((ImageView) findViewById(R.id.iv_search)).setImageDrawable(getResources().getDrawable(R.drawable.search_white_home));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.black));
            findViewById(R.id.ll_bottom_view).setBackgroundColor(getResources().getColor(R.color.black));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.appBlackxLight));

            ((ImageView) findViewById(R.id.iv_home)).setImageDrawable(getResources().getDrawable(R.drawable.deselect_homewhite));
            ((TextView) findViewById(R.id.tv_home)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((ImageView) findViewById(R.id.iv_video)).setImageDrawable(getResources().getDrawable(R.drawable.deselect_videowhite));
            ((TextView) findViewById(R.id.tv_video)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((ImageView) findViewById(R.id.iv_categories)).setImageDrawable(getResources().getDrawable(R.drawable.deselect_categorywhite));
            ((TextView) findViewById(R.id.tv_categories)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((ImageView) findViewById(R.id.iv_profile)).setImageDrawable(getResources().getDrawable(R.drawable.deselect_profilewhite));
            ((TextView) findViewById(R.id.tv_profile)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((ImageView) findViewById(R.id.iv_sort)).setImageDrawable(getResources().getDrawable(R.drawable.sort_white));
        } else {
            Utils.setLightStatusBar(findViewById(android.R.id.content).getRootView(), this);

            ((ImageView) findViewById(R.id.iv_home_logo)).setImageDrawable(getResources().getDrawable(R.drawable.home_logo));
            ((ImageView) findViewById(R.id.iv_notification)).setImageDrawable(getResources().getDrawable(R.drawable.notify));
            ((ImageView) findViewById(R.id.iv_search)).setImageDrawable(getResources().getDrawable(R.drawable.search));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.white));
            findViewById(R.id.ll_bottom_view).setBackgroundColor(getResources().getColor(R.color.white));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.appBlackDark));


            ((ImageView) findViewById(R.id.iv_home)).setImageDrawable(getResources().getDrawable(R.drawable.deselect_home));
            ((TextView) findViewById(R.id.tv_home)).setTextColor(getResources().getColor(R.color.appBlackLight));
            ((ImageView) findViewById(R.id.iv_video)).setImageDrawable(getResources().getDrawable(R.drawable.deselect_video));
            ((TextView) findViewById(R.id.tv_video)).setTextColor(getResources().getColor(R.color.appBlackLight));
            ((ImageView) findViewById(R.id.iv_categories)).setImageDrawable(getResources().getDrawable(R.drawable.deselect_category));
            ((TextView) findViewById(R.id.tv_categories)).setTextColor(getResources().getColor(R.color.appBlackLight));
            ((ImageView) findViewById(R.id.iv_profile)).setImageDrawable(getResources().getDrawable(R.drawable.deselect_profile));
            ((TextView) findViewById(R.id.tv_profile)).setTextColor(getResources().getColor(R.color.appBlackLight));
            ((ImageView) findViewById(R.id.iv_sort)).setImageDrawable(getResources().getDrawable(R.drawable.sort));
        }
    }

    private void init() {

        fl_view = findViewById(R.id.fl_view);
        rl_home = findViewById(R.id.rl_home);
        rl_video = findViewById(R.id.rl_video);
        rl_categories = findViewById(R.id.rl_categories);
        rl_profile = findViewById(R.id.rl_profile);
        iv_home = findViewById(R.id.iv_home);
        iv_video = findViewById(R.id.iv_video);
        iv_categories = findViewById(R.id.iv_categories);
        iv_profile = findViewById(R.id.iv_profile);
        tv_home = findViewById(R.id.tv_home);
        tv_video = findViewById(R.id.tv_video);
        tv_categories = findViewById(R.id.tv_categories);
        tv_profile = findViewById(R.id.tv_profile);
        tv_notification_count = findViewById(R.id.tv_notification_count);

        homeScreen();

    }


    private void listener() {
        rl_home.setOnClickListener(this);
        rl_video.setOnClickListener(this);
        rl_categories.setOnClickListener(this);
        rl_profile.setOnClickListener(this);
        findViewById(R.id.iv_search).setOnClickListener(this);
        findViewById(R.id.iv_notification).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_home:
                setVisibility();
                homeScreen();
                break;
            case R.id.rl_video:
                setVisibility();
                videoScreen();
                break;
            case R.id.rl_categories:
                setVisibility();
                categoriesScreen();
                break;
            case R.id.rl_profile:
                if (Utils.getLoginToken(this).isEmpty())
                    Utils.setLoginPopup(this, preferences);
                else {
                    setVisibility();
                    profileScreen();
                }
                break;
            case R.id.iv_notification:
                startActivity(new Intent(this, NotificationActivity.class));
                break;
            case R.id.iv_search:
                startActivity(new Intent(this, SearchNewsActivity.class));
                break;
            case R.id.iv_sort:
                ((VideoFragment) currentFragment).setFilterPopup();
                break;
        }
    }

    private void setVisibility() {
        findViewById(R.id.iv_home_logo).setVisibility(View.GONE);
        findViewById(R.id.iv_notification).setVisibility(View.GONE);
        findViewById(R.id.iv_search).setVisibility(View.GONE);
        findViewById(R.id.tv_title).setVisibility(View.GONE);
        findViewById(R.id.iv_sort).setVisibility(View.GONE);

        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            iv_home.setImageDrawable(getResources().getDrawable(R.drawable.deselect_homewhite));
            tv_home.setTextColor(getResources().getColor(R.color.appBlackxLight));
            iv_video.setImageDrawable(getResources().getDrawable(R.drawable.deselect_videowhite));
            tv_video.setTextColor(getResources().getColor(R.color.appBlackxLight));
            iv_categories.setImageDrawable(getResources().getDrawable(R.drawable.deselect_categorywhite));
            tv_categories.setTextColor(getResources().getColor(R.color.appBlackxLight));
            iv_profile.setImageDrawable(getResources().getDrawable(R.drawable.deselect_profilewhite));
            tv_profile.setTextColor(getResources().getColor(R.color.appBlackxLight));

        } else {
            iv_home.setImageDrawable(getResources().getDrawable(R.drawable.deselect_home));
            tv_home.setTextColor(getResources().getColor(R.color.appBlackLight));
            iv_video.setImageDrawable(getResources().getDrawable(R.drawable.deselect_video));
            tv_video.setTextColor(getResources().getColor(R.color.appBlackLight));
            iv_categories.setImageDrawable(getResources().getDrawable(R.drawable.deselect_category));
            tv_categories.setTextColor(getResources().getColor(R.color.appBlackLight));
            iv_profile.setImageDrawable(getResources().getDrawable(R.drawable.deselect_profile));
            tv_profile.setTextColor(getResources().getColor(R.color.appBlackLight));
        }
    }

    private void homeScreen() {
        findViewById(R.id.iv_home_logo).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_notification).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_search).setVisibility(View.VISIBLE);

        iv_home.setImageDrawable(getResources().getDrawable(R.drawable.select_home));
        tv_home.setTextColor(getResources().getColor(R.color.appBlue));

        if (!(currentFragment instanceof HomeNewFragment)) {
            currentFragment = new HomeNewFragment();
            moveToFragment(currentFragment);
        }


        callNotificationCount();
    }

    private void videoScreen() {
        findViewById(R.id.iv_sort).setVisibility(View.GONE);
        findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
        tv_notification_count.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tv_title)).setText("Video Articles");

        findViewById(R.id.iv_sort).setOnClickListener(this);

        iv_video.setImageDrawable(getResources().getDrawable(R.drawable.select_video));
        tv_video.setTextColor(getResources().getColor(R.color.appBlue));

        if (!(currentFragment instanceof VideoFragment)) {
            currentFragment = new VideoFragment();
            moveToFragment(currentFragment);
        }
    }

    private void categoriesScreen() {
        findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
        tv_notification_count.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tv_title)).setText("Categories");

        iv_categories.setImageDrawable(getResources().getDrawable(R.drawable.select_category));
        tv_categories.setTextColor(getResources().getColor(R.color.appBlue));

        if (!(currentFragment instanceof CategoriesFragment)) {
            currentFragment = new CategoriesFragment();
            moveToFragment(currentFragment);
        }
    }

    private void profileScreen() {
        findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
        tv_notification_count.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tv_title)).setText("Profile");

        iv_profile.setImageDrawable(getResources().getDrawable(R.drawable.select_profile));
        tv_profile.setTextColor(getResources().getColor(R.color.appBlue));

        if (!(currentFragment instanceof ProfileFragment)) {
            currentFragment = new ProfileFragment();
            moveToFragment(currentFragment);
        }
    }

    private void moveToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_view, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

    }

    @Override
    protected void onPause() {
//        if (receiver != null) {
//            unregisterReceiver(receiver);
//            receiver = null;
//        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);
        callForceUpdate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ChangeTheme");
        filter.addAction("callNotificationCount");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase("android.intent.action.MY_PACKAGE_REPLACED")) {
                    Utils.sessionExpired(HomeActivity.this);
                } else if (intent.getAction().equalsIgnoreCase("ChangeTheme")) {
                    updateTheme();
                    iv_profile.setImageDrawable(getResources().getDrawable(R.drawable.select_profile));
                    tv_profile.setTextColor(getResources().getColor(R.color.appBlue));
                } else if (intent.getAction().equalsIgnoreCase("callNotificationCount")) {
                    callNotificationCount();
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    private void callNotificationCount() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("device_token", preferences.getDeviceToken());
        map.put("login_token", preferences.getUserData().getLoginToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<NotificationCountModel> couponsObservable
                            = s.callNotificationCount(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessNotificationCount, this::handleError);

    }

    private void OnSuccessNotificationCount(NotificationCountModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                if (modelResult.getUnreadNotificationCount() != null) {
                    tv_notification_count.setVisibility(View.VISIBLE);
                    if (modelResult.getUnreadNotificationCount() > 0)
                        tv_notification_count.setText(modelResult.getUnreadNotificationCount() + "");
                    else if (modelResult.getUnreadNotificationCount() > 99)
                        tv_notification_count.setText("99");
                    else
                        tv_notification_count.setVisibility(View.GONE);
                }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentFragment instanceof HomeNewFragment)
            finish();
        else {
            setVisibility();
            homeScreen();
        }
    }

    private void callForceUpdate() {
        if (!Utils.checkNetworkAvailability(this)) {
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

            if (Double.parseDouble(version) >= Double.parseDouble(result.getLatestVersion())) {

            } else if (result.getForceUpdate() == 0)
                showUpdatePopup(false);
            else if (result.getForceUpdate() == 1)
                showUpdatePopup(true);
        }
    }

    private void showVersionName() {
        try {
            PackageManager manager = getPackageManager();
            ApplicationInfo ai = manager.getApplicationInfo(
                    getPackageName(), 0);
            PackageInfo info = manager.getPackageInfo(
                    getPackageName(), 0);
            version = info.versionName;
//            tv_version.setText(version);
            appName = (String) (ai != null ? manager.getApplicationLabel(ai) : "(unknown)");
        } catch (Exception e) {

        }
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

        if (forceUpdate) {
            ((TextView) dialog.findViewById(R.id.tv_msg)).setText(appName + " recommends that you update to the latest version.");
            dialog.findViewById(R.id.tv_cancel).setVisibility(View.GONE);
        } else {
            ((TextView) dialog.findViewById(R.id.tv_msg)).setText(appName + " recommends that you update to the latest version. You can keep using this app while downloading the update.");
            dialog.findViewById(R.id.tv_cancel).setVisibility(View.VISIBLE);

        }

        dialog.findViewById(R.id.tv_update).setOnClickListener(v -> {
            updateApp();
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            dialog.cancel();
        });

    }

    private void updateApp() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    public TaskListApplication getTaskApp() {
        return (TaskListApplication) getApplication();
    }

}