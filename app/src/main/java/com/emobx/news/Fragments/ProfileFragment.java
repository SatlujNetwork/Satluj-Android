package com.emobx.news.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Activities.BookmarksActivity;
import com.emobx.news.Activities.ContactUsActivity;
import com.emobx.news.Activities.EditProfileActivity;
import com.emobx.news.Activities.HomeActivity;
import com.emobx.news.Activities.LiveStreamActivity;
import com.emobx.news.Activities.SetThemeActivity;
import com.emobx.news.Activities.SignupSocialActivity;
import com.emobx.news.ChangeTheme.ChangeTheme;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.BookmarkData;
import com.emobx.news.Model.LoginModel;
import com.emobx.news.Model.LogoutModel;
import com.emobx.news.Model.NotificationSettingModel;
import com.emobx.news.Model.SingleNewModel;
import com.emobx.news.Model.WebSeriesDetailModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static boolean notification = false;
    private View view;
    private TextView tv_name, tv_email, tv_logout, tv_general_settings, tv_contact, tv_privacy, tv_about,
            tv_bookmarks, tv_getNotification, tv_edit_profile, tv_theme;
    private ArrayList<TextView> arr_tv = new ArrayList<>();
    private ImageView iv_getNotification;
    private Activity activity;
    private NewsPreferences preferences;
    private ApiInterface apiInterface;
    private IntentFilter filter;
    private CircleImageView iv_profile1;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("UpdateProfile")) {
                setProfileData();
            } else if (action.equals(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                //action for phone state changed
            }
        }
    };
    private GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        listener();
        setProfileData();
        return view;
    }

    private void setProfileData() {
        activity = getActivity();
        preferences = new NewsPreferences(activity);
        if (preferences.getUserData() != null)
            if (preferences.getUserData().getData() != null) {
                LoginModel.Data data = preferences.getUserData().getData();
                if (data.getName() != null)
                    tv_name.setText(data.getName());

                if (data.getEmail() != null)
                    tv_email.setText(data.getEmail());

                if (data.getPic() != null)
                    if (!data.getPic().isEmpty())
                        if (data.getPic().startsWith("http"))
                            Picasso.with(activity).load(data.getPic()).into(iv_profile1);
                        else
                            Picasso.with(activity).load(Constants.BASE_URl + "/" + data.getPic()).into(iv_profile1);

                if (preferences.getUserData().getData().getNotification() != null)
                    if (preferences.getUserData().getData().getNotification().toLowerCase().equalsIgnoreCase("on")) {
                        iv_getNotification.setImageDrawable(activity.getResources().getDrawable(R.drawable.select_switch));
                        notification = true;
                    } else {
                        iv_getNotification.setImageDrawable(activity.getResources().getDrawable(R.drawable.no_selectswitch));
                        notification = false;
                    }

            }
    }

    private void init() {
        activity = getActivity();
        preferences = new NewsPreferences(activity);

        iv_profile1 = view.findViewById(R.id.iv_profile1);
        tv_name = view.findViewById(R.id.tv_name);
        tv_email = view.findViewById(R.id.tv_email);
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_general_settings = view.findViewById(R.id.tv_general_settings);
        tv_contact = view.findViewById(R.id.tv_contact);
        tv_privacy = view.findViewById(R.id.tv_privacy);
        tv_about = view.findViewById(R.id.tv_about);
        tv_bookmarks = view.findViewById(R.id.tv_bookmarks);
        tv_edit_profile = view.findViewById(R.id.tv_edit_profile);
        tv_getNotification = view.findViewById(R.id.tv_getNotification);
        iv_getNotification = view.findViewById(R.id.iv_getNotification);
        tv_theme = view.findViewById(R.id.tv_theme);

        arr_tv.add(tv_name);
        arr_tv.add(tv_email);
        arr_tv.add(tv_logout);
        arr_tv.add(tv_general_settings);
        arr_tv.add(tv_privacy);
        arr_tv.add(tv_about);
        arr_tv.add(tv_contact);
        arr_tv.add(tv_bookmarks);
        arr_tv.add(tv_edit_profile);
        arr_tv.add(tv_getNotification);
        arr_tv.add(tv_theme);

        filter = new IntentFilter();
        filter.addAction("UpdateProfile");

        mGoogleApiClient = new GoogleApiClient.Builder(activity) //Use app context to prevent leaks using activity
                //.enableAutoManage(this /* FragmentActivity */, connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void listener() {
        tv_bookmarks.setOnClickListener(this);
        tv_getNotification.setOnClickListener(this);
        iv_getNotification.setOnClickListener(this);
        tv_edit_profile.setOnClickListener(this);
        tv_theme.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        view.findViewById(R.id.tv_contact).setOnClickListener(this);
        view.findViewById(R.id.tv_privacy).setOnClickListener(this);
        view.findViewById(R.id.tv_about).setOnClickListener(this);
        view.findViewById(R.id.tv_delete_account).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit_profile:
                startActivity(new Intent(activity, EditProfileActivity.class));
                break;
            case R.id.tv_contact:
//                startActivity(new Intent(activity, ContactUsActivity.class));
                startActivity(new Intent(activity, LiveStreamActivity.class).putExtra("contactUsLink", Constants.BASE_URl + "/contactus_detail"));
                break;
            case R.id.tv_privacy:
                startActivity(new Intent(activity, LiveStreamActivity.class).putExtra("policyLink", Constants.BASE_URl + "/privacy_policy"));
                break;
            case R.id.tv_about:
                startActivity(new Intent(activity, LiveStreamActivity.class).putExtra("aboutusLink", Constants.BASE_URl + "/about_us"));
                break;
            case R.id.tv_bookmarks:
                startActivity(new Intent(activity, BookmarksActivity.class));
                break;
            case R.id.iv_getNotification:
                if (!notification) {
                    iv_getNotification.setImageDrawable(getResources().getDrawable(R.drawable.select_switch));
                    notification = true;
                } else {
                    iv_getNotification.setImageDrawable(getResources().getDrawable(R.drawable.no_selectswitch));
                    notification = false;
                }
                callNotificationSetting();
                break;
            case R.id.tv_theme:
                startActivity(new Intent(activity, SetThemeActivity.class));
                break;
            case R.id.tv_logout:
                setPopup("Are you sure you want to logout?", "logout");
                break;
            case R.id.tv_delete_account:
                setPopup("Are you sure you want to delete your Account?", "delete");
                break;
        }
    }

    public void setPopup(String msg, String val) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.show();

        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            dialog.findViewById(R.id.ll_popup).setBackgroundColor(activity.getResources().getColor(R.color.black));
            ((TextView) dialog.findViewById(R.id.tv_text)).setTextColor(activity.getResources().getColor(R.color.appBlackxLight));
            ((TextView) dialog.findViewById(R.id.tv_no)).setTextColor(activity.getResources().getColor(R.color.appBlackxLight));
            ((TextView) dialog.findViewById(R.id.tv_yes)).setTextColor(activity.getResources().getColor(R.color.appBlackxLight));
        } else {
            dialog.findViewById(R.id.ll_popup).setBackgroundColor(activity.getResources().getColor(R.color.white));
            ((TextView) dialog.findViewById(R.id.tv_text)).setTextColor(activity.getResources().getColor(R.color.appBlackDark));
            ((TextView) dialog.findViewById(R.id.tv_no)).setTextColor(activity.getResources().getColor(R.color.appBlackDark));
            ((TextView) dialog.findViewById(R.id.tv_yes)).setTextColor(activity.getResources().getColor(R.color.appBlackDark));
        }

        ((TextView) dialog.findViewById(R.id.tv_text)).setText(msg);

        dialog.findViewById(R.id.tv_yes).setOnClickListener(v -> {
            switch (val) {
                case "logout":
                    callLogoutApi();
                    break;
                case "delete":
                    callDeleteAccount();
                    break;
            }
        });

        dialog.findViewById(R.id.tv_no).setOnClickListener(v -> {
            dialog.cancel();
        });

    }

    private void callNotificationSetting() {
        if (!Utils.checkNetworkAvailability(activity)) {
            return;
        }
        Utils.showProgressDialog(activity);
        if (apiInterface == null)
            apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<NotificationSettingModel> couponsObservable
                            = s.setNotificationSetting(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccess, this::handleError);

    }

    private void OnSuccess(NotificationSettingModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                if (preferences.getUserData() != null)
                    if (preferences.getUserData().getData() != null) {
                        LoginModel data = preferences.getUserData();
                        data.getData().setNotification(modelResult.getNotification());
                        preferences.setUserData(data);
                    }
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(activity);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            view.findViewById(R.id.rl_main).setBackgroundColor(activity.getResources().getColor(R.color.black));
            new ChangeTheme().changeDarkTheme(activity, arr_tv);
        } else {
            view.findViewById(R.id.rl_main).setBackgroundColor(activity.getResources().getColor(R.color.white));
            new ChangeTheme().changeLightTheme(activity, arr_tv);
        }

        activity.registerReceiver(receiver, filter);
    }


    private void callLogoutApi() {
        if (!Utils.checkNetworkAvailability(activity)) {
            return;
        }
        Utils.showProgressDialog(activity);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(activity);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<LogoutModel> couponsObservable
                            = s.callLogout(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessLogout, this::handleError);

    }

    private void OnSuccessLogout(LogoutModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                preferences.cleanData();
                Utils.clearAppData(activity);

                //Google SignOut
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                }

                startActivity(new Intent(activity, SignupSocialActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK));
                // activity.finish();
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(activity);
            } else
                Toast.makeText(activity, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(activity, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void callDeleteAccount() {
        if (!Utils.checkNetworkAvailability(activity)) {
            return;
        }
        Utils.showProgressDialog(activity);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(activity);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<WebSeriesDetailModel> couponsObservable
                            = s.deleteAccount(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessDeleteAccount, this::handleError);

    }

    private void OnSuccessDeleteAccount(WebSeriesDetailModel result) {
        Utils.hideProgressDialog();
        if (result != null && result.getErrorCode() != null) {
            if (result.getErrorCode().equalsIgnoreCase("200")) {
                preferences.cleanData();
                startActivity(new Intent(activity, SignupSocialActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK));

                //Google SignOut
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                }
                activity.finish();
            } else if (result.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(activity);
            } else
                Toast.makeText(activity, "Something went wrong.", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(activity, "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(activity, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}