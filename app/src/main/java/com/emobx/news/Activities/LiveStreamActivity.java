package com.emobx.news.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.R;
import com.emobx.news.Utils.Utils;

public class LiveStreamActivity extends BaseActivity implements View.OnClickListener {

    AlertDialog dialog;
    float x, y;
    private WebView webview;
    private NewsPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);

        init();
        listener();
        getIntentData();

//        setYoutubeVideo();
    }

    private void getIntentData() {
        if (getIntent().hasExtra("videoUrl")) {
            String url = getIntent().getStringExtra("videoUrl");
            ((TextView) findViewById(R.id.tv_title)).setText("Live Video");
            webview.loadUrl(url);
        }else if (getIntent().hasExtra("contactUsLink")) {
            String url = getIntent().getStringExtra("contactUsLink");
            ((TextView) findViewById(R.id.tv_title)).setText("Contact Us");
            webview.loadUrl(url);
        } else if (getIntent().hasExtra("policyLink")) {
            String url = getIntent().getStringExtra("policyLink");
            ((TextView) findViewById(R.id.tv_title)).setText("Privacy Policy");
            webview.loadUrl(url);
        } else if (getIntent().hasExtra("aboutusLink")) {
            String url = getIntent().getStringExtra("aboutusLink");
            ((TextView) findViewById(R.id.tv_title)).setText("About Us");
            webview.loadUrl(url);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

//                showDialog();  // display dialog
                break;
            case MotionEvent.ACTION_MOVE:
//                if (dialog != null)
//                    dialog.dismiss();
                if (dialog == null)
//                if(!dialog.isShowing())
                    showDialog();
                else {
                    WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                    wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                    wmlp.x = (int) x;   //x position
                    wmlp.y = (int) y;   //y position

                    dialog.onWindowAttributesChanged(wmlp);

                }
                Log.e("pos>>>>", (int) x + " , " + (int) y);
                // do something
                break;
            case MotionEvent.ACTION_UP:
                // do somethig
                break;
        }
        return true;
    }

    public void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        dialog.setTitle("my dialog");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = (int) x;   //x position
        wmlp.y = (int) y;   //y position

        if (dialog.isShowing())
            dialog.dismiss();
        dialog.show();
    }


    private void listener() {
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    private void init() {
        preferences = new NewsPreferences(this);
        findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_back).setVisibility(View.VISIBLE);


        webview = (WebView) findViewById(R.id.web_view);
        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 8_3 like Mac OS X) AppleWebKit/600.14 (KHTML, like Gecko) Mobile/12F70");


        webview.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Utils.showProgressDialog(LiveStreamActivity.this);
            }


            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }

            public void onLoadResource(WebView view, String url) { //Doesn't work
                //swipe.setRefreshing(true);
            }

            public void onPageFinished(WebView view, String url) {

                //Hide the SwipeReefreshLayout
                Utils.hideProgressDialog();
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            Utils.clearLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_top).setBackgroundColor(this.getResources().getColor(R.color.black));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(this.getResources().getColor(R.color.appBlackxLight));
        } else {
            Utils.setLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_top).setBackgroundColor(this.getResources().getColor(R.color.white));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(this.getResources().getColor(R.color.appBlackDark));
        }
    }
}