package com.emobx.news.Utils;

import static android.content.Context.UI_MODE_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.emobx.news.Activities.HomeActivity;
import com.emobx.news.Activities.SignupSocialActivity;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

public class Utils {

    private static final String TAG = "Utils";
    private static Dialog progressDialog;

    public static boolean checkNetworkAvailability(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            Toast.makeText(context, "No internet connection available", Toast.LENGTH_LONG).show();
            return false;
        }
        NetworkInfo.State network = info.getState();
        if ((network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING))
            return true;
        else {
//            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static void showProgressDialog(Context context) {
        if (progressDialog != null && progressDialog.isShowing())
            return;
        progressDialog = new Dialog(context);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (!((Activity) context).isFinishing()) {
            progressDialog.show();
        }
        progressDialog.setCancelable(false);
    }

    public static void hideProgressDialog() {
        if (progressDialog != null)
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }


    public static void clearAppData(Context context) {
        try {
            clearApplicationData(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public static void sessionExpired(Activity activity) {
        clearAppData(activity);

        NewsPreferences preferences = new NewsPreferences(activity);
        Toast.makeText(activity, "Your Session has expired. Please login again.", Toast.LENGTH_SHORT).show();
        preferences.cleanData();
        activity.startActivity(new Intent(activity, SignupSocialActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK));
        activity.finish();
    }


    public static void setStatusBarTransparent(Activity ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = ctx.getWindow().getDecorView();
            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
                return defaultInsets.replaceSystemWindowInsets(
                        defaultInsets.getSystemWindowInsetLeft(),
                        0,
                        defaultInsets.getSystemWindowInsetRight(),
                        defaultInsets.getSystemWindowInsetBottom());
            });
            ViewCompat.requestApplyInsets(decorView);
            ctx.getWindow().setStatusBarColor(ContextCompat.getColor(ctx, android.R.color.transparent));
        }
    }

    public static void setLightStatusBar(View view, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    public static void clearLightStatusBar(View view, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = view.getSystemUiVisibility();
            flags = 8192 ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.black));

        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String changeDateFormat(String time, String in, String out) {
        if (!time.trim().isEmpty()) {
            try {
                String dateTemp = time.split("T")[0];
                String timeTemp = time.split("T")[1].split("\\.")[0];

                time = dateTemp + " " + timeTemp;

                String inputPattern = in;
                String outputPattern = out;
                SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                outputFormat.setTimeZone(TimeZone.getDefault());
                Date date = null;
                String str = null;

                try {
                    date = inputFormat.parse(time);
                    str = outputFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return str;
            } catch (Exception e) {

            }
        }
        return "";
    }

    public static String getFileName(String s) {
        return (s.substring(s.lastIndexOf('/') + 1));
    }

    public static void setLoginPopup(Activity activity, NewsPreferences preferences) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.login_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
//        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.show();

        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            dialog.findViewById(R.id.ll_popup).setBackgroundColor(activity.getResources().getColor(R.color.black));
            ((TextView) dialog.findViewById(R.id.tv_text)).setTextColor(activity.getResources().getColor(R.color.appBlackxLight));
            ((TextView) dialog.findViewById(R.id.tv_login)).setTextColor(activity.getResources().getColor(R.color.appBlackxLight));
            ((TextView) dialog.findViewById(R.id.tv_cancel)).setTextColor(activity.getResources().getColor(R.color.appBlackxLight));
        } else {
            dialog.findViewById(R.id.ll_popup).setBackgroundColor(activity.getResources().getColor(R.color.white));
            ((TextView) dialog.findViewById(R.id.tv_text)).setTextColor(activity.getResources().getColor(R.color.appBlackDark));
            ((TextView) dialog.findViewById(R.id.tv_login)).setTextColor(activity.getResources().getColor(R.color.appBlackDark));
            ((TextView) dialog.findViewById(R.id.tv_cancel)).setTextColor(activity.getResources().getColor(R.color.appBlackDark));
        }

        dialog.findViewById(R.id.tv_login).setOnClickListener(v -> {
            activity.startActivity(new Intent(activity, SignupSocialActivity.class));
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            dialog.cancel();
        });

    }

    public static void updateOrientation(Activity context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Log.d(TAG, "Running on a TV Device");
        } else {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Log.d(TAG, "Running on a non-TV Device");
        }
    }

    public static void loadImageWithUrl(Activity activity, String url, ImageView imageView) {
        if (url != null && !url.isEmpty())
            if (url.startsWith("http"))
                Picasso.with(activity).load(url).into(imageView);
            else
                Picasso.with(activity).load(Constants.BASE_URl + "/" + url).into(imageView);
    }

    public static String getLoginToken(Activity activity) {
        NewsPreferences preferences = new NewsPreferences(activity);
        if (preferences.getUserData() != null && preferences.getUserData().getLoginToken() != null)
            return preferences.getUserData().getLoginToken();
        else
            return "";
    }

    public static void showToast(Context ac, String msg) {
        Toast toast = new Toast(ac);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.makeText(ac, msg, Toast.LENGTH_SHORT).show();
    }

    public static Realm getRealmInstance(Activity activity) {
        try {
            Realm.init(activity);
            RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                    .name(Constants.REALM_DB_NAME)
                    .schemaVersion(Constants.version)
                    .build();
            Realm.setDefaultConfiguration(realmConfig);

            return Realm.getDefaultInstance();
        } catch (Exception e) {
            sessionExpired(activity);
        }
        return null;
    }

    public static void googleAnalytics(Activity activity) {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        NewsPreferences preferences = new NewsPreferences(activity);

        if (preferences != null
                && preferences.getUserData() != null
                && preferences.getUserData().getData() != null
                && preferences.getUserData().getData().getId() != null
                && preferences.getUserData().getData().getName() != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, preferences.getUserData().getData().getId() + "");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, preferences.getUserData().getData().getName() + "");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }
}
