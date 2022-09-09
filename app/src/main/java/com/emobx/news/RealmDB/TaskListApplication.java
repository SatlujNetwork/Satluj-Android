package com.emobx.news.RealmDB;

import android.app.Application;

import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class TaskListApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        initRealm(new NewsPreferences(this).getRealmVersion());
    }

    public synchronized void initRealm(int version) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name(Constants.REALM_DB_NAME)
                .schemaVersion(Constants.version)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}