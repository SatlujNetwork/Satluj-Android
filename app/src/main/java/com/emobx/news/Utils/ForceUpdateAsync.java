/*
package com.emobx.news.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;

import com.emobx.forestphotoshare.R;
import com.emobx.forestphotoshare.activities.LoginActivity;
import com.emobx.forestphotoshare.iGuard.HomeActivity;

import org.json.JSONObject;
import org.jsoup.Jsoup;

*/
/**
 * Created by Bhubnesh Uppal on 03-Jul-19.
 *//*

public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

    private String latestVersion;
    private String currentVersion;
    private Activity context;
    private MyPreferences appSharedPreferences;

    public ForceUpdateAsync(String currentVersion, Activity context) {
        this.currentVersion = currentVersion;
        this.context = context;
        this.appSharedPreferences = new MyPreferences(context);
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        try {
            latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                    .first()
                    .ownText();

           */
/* latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();*//*


        } catch (Exception e) {
            latestVersion = currentVersion;
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (latestVersion != null) {
            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                String[] array = latestVersion.split("\\.");
                if (Integer.valueOf(array[array.length - 1]) % 2 != 0)// if value after decimal is odd then user can skip update
                    showForceUpdateDialog(true);
                else
                    showForceUpdateDialog(false);
            } else {
                goToNextScreen();
            }
        }
        super.onPostExecute(jsonObject);
    }

    private void goToNextScreen() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (appSharedPreferences.getLoginToken().isEmpty())
                context.startActivity(new Intent(context, LoginActivity.class));
            else
                context.startActivity(new Intent(context, HomeActivity.class));
            context.finish();
        }).start();
    }

    public void showForceUpdateDialog(boolean isTemp) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getString(R.string.youAreNotUpdatedTitle));
        alertDialogBuilder.setMessage(context.getString(R.string.youAreNotUpdatedMessage) + " " + latestVersion + " " + context.getString(R.string.youAreNotUpdatedMessage1));
        alertDialogBuilder.setCancelable(false);
        if (isTemp) {
            alertDialogBuilder.setNegativeButton("No", (dialogInterface, i) -> {
                dialogInterface.cancel();
                goToNextScreen();
            });
        }
        alertDialogBuilder.setPositiveButton("Update", (dialogInterface, i) -> {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
            dialogInterface.cancel();
        });
        alertDialogBuilder.show();
    }
}
*/
