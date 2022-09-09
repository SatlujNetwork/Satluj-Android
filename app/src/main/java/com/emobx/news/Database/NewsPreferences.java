package com.emobx.news.Database;

import android.content.Context;
import android.content.SharedPreferences;

import com.emobx.news.Model.LoginModel;
import com.google.gson.Gson;


public class NewsPreferences {
    private static final String PREF_NAME = "councilor_shared";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE;

    public NewsPreferences(Context context) {
        this._context = context;
        PRIVATE_MODE = _context.MODE_PRIVATE;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }


    public void cleanData() {
        editor = pref.edit();
        editor.clear();
        editor.apply();
        editor.commit();
    }

    private void openEditor() {
        editor = pref.edit();
    }

    public String getTheme() {
        return pref.getString("theme", "light");
    }

    public void setTheme(String theme) {
        openEditor();
        editor.putString("theme", theme);
        editor.apply();
        editor.commit();
    }

    public LoginModel getUserData() {
        LoginModel modal = new Gson().fromJson(pref.getString("loginData", ""), LoginModel.class);
        if (modal == null)
            modal = new LoginModel();
        return modal;
    }

    public void setUserData(LoginModel model) {
        openEditor();
        editor.putString("loginData", new Gson().toJson(model));
        editor.apply();
        editor.commit();
    }


    public String getDeviceToken() {
        return pref.getString("deviceToken", "abc");
    }

    public void setDeviceToken(String fcm) {
        openEditor();
        editor.putString("deviceToken", fcm);
        editor.apply();
        editor.commit();
    }

    public boolean getProductPurchaseStatus() {
        return pref.getBoolean("productPurchaseStatus", false);
    }

    public void setProductPurchaseStatus(boolean value) {
        openEditor();
        editor.putBoolean("productPurchaseStatus", value);
        editor.apply();
        editor.commit();
    }

    public int getRealmVersion() {
        return pref.getInt("realmVersion", 1);
    }

    public void setRealmVersion(int version) {
        openEditor();
        editor.putInt("realmVersion", version);
        editor.apply();
        editor.commit();
    }
}