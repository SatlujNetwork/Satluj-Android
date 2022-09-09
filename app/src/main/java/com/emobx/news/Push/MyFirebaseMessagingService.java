package com.emobx.news.Push;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.emobx.news.Activities.HomeActivity;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;


@SuppressLint("WrongConstant")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    final int min = 20;
    final int max = 80;
    private NotificationChannel mChannel;
    private NotificationManager notifManager;
    private int random = 0;
    private int randomId = 0;
    private NewsPreferences preferences;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("FCM_TOKEN", s);
        if (s != null && !s.isEmpty())
            Constants.FCM_TOKEN = s;
        preferences = new NewsPreferences(getApplicationContext());
        // preferences.setFcmToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        randomId = new Random().nextInt((max - min) + 1) + min;
//        if (preferences == null)
//            preferences = new NewsPreferences(getApplicationContext());
//        if (preferences.getUserData() != null && preferences.getUserData().getData() != null &&
//                preferences.getUserData().getData().getId() != null)
        if (remoteMessage.getData().size() > 0) {
            buildNotification(remoteMessage.getData());
        }
    }

    private void buildNotification(Map<String, String> notification) {
        JSONObject object = new JSONObject(notification);
        try {
            String action = object.getString("action");
            if (action.equalsIgnoreCase("test")
                        /*|| action.equalsIgnoreCase("event_unsave")
                        || action.equalsIgnoreCase("event_approved")
                        || action.equalsIgnoreCase("event_restricted")
                        || action.equalsIgnoreCase("request_cancelled")
                        || action.equalsIgnoreCase("event_start")
                        || action.equalsIgnoreCase("event_end")
                        || action.equalsIgnoreCase("event_finish")
                        || action.equalsIgnoreCase("event_cancelled")
                        || action.equalsIgnoreCase("event_rating")
                        || action.equalsIgnoreCase("add_friend")
                        || action.equalsIgnoreCase("event_danger")
                        || action.equalsIgnoreCase("new_message")
                        || action.equalsIgnoreCase("event_planned")
                        || action.equalsIgnoreCase("event_unplanned")
                        || action.equalsIgnoreCase("post_like")
                        || action.equalsIgnoreCase("post_comment")
                        || action.equalsIgnoreCase("comment_like")
                        || action.equalsIgnoreCase("user_follow")*/) {
                JustCreateNotification(object);
            } else {
                JustCreateNotification(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void JustCreateNotification(JSONObject object) {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }
        Random rand = new Random();
        int number = rand.nextInt(100) + 1;
        String new_msg = "";
        try {
            new_msg = object.getString("message");
        } catch (Exception e) {
            e.printStackTrace();

        }

        Intent notificationIntent = new Intent(getApplicationContext(), HomeActivity.class);

        sendBroadcast(new Intent("callNotificationCount"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationCompat.Builder builder;
            PendingIntent pendingIntent;
            if (mChannel == null) {
                mChannel = new NotificationChannel
                        ("0", getResources().getString(R.string.app_name), importance);
                mChannel.setDescription(new_msg);
                mChannel.enableVibration(true);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, "0");
            builder.setStyle(new NotificationCompat.BigTextStyle(builder)
                    .bigText(new_msg));
            pendingIntent = PendingIntent.getActivity(this, 1251, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setSmallIcon(R.drawable.app_icon) // required
                    .setContentText(new_msg)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource
                            (getResources(), R.drawable.app_icon))
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION));
            Notification mNotification1 = builder.build();
            notifManager.notify(number, mNotification1);
        } else {
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                    number, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager nm = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Resources res = getApplicationContext().getResources();

            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setStyle(new Notification.BigTextStyle(builder)
                    .bigText(new_msg));
            builder.setContentIntent(contentIntent)

                    .setSmallIcon(R.drawable.app_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.app_icon))
                    .setAutoCancel(true)
                    .setContentText(new_msg)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH);
            Notification n = builder.build();
            if (nm != null) {
                nm.notify(number, n);
            }
        }
    }


}


