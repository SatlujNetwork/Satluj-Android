package com.emobx.news.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchaseHistoryParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Adapters.SeasonsAdapter;
import com.emobx.news.Adapters.WebSeriesViewPagerAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.PaymentDetailModel;
import com.emobx.news.Model.PurchaseDetailModel;
import com.emobx.news.Model.PurchaseHistoryModel;
import com.emobx.news.Model.WebSeriesDetailModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class WebSeriesDetailActivity extends BaseActivity implements View.OnClickListener, AdapterItemClickListner {

    private String PRODUCT_ID = "web_series_purchase";
    private ViewPager viewPager;
    private int totalTab = 2;
    private TextView tv_episodes, tv_more_info;
    private String seriesId = "";
    private ApiInterface apiInterface;
    private NewsPreferences preferences;
    private WebSeriesDetailModel.Data webSeriesDetail;
    private ImageView iv_cover_image, iv_trailer;
    private TextView tv_name, tv_seasons, tv_resume_button, tv_detail, tv_release_year, tv_total_episodes, tv_trailer;
    private LinearLayout ll_seasons, ll_start_over;
    private ProgressBar progress_resume;
    private ListPopupWindow seasonPopupWindow;
    private BillingClient billingClient;
    private PaymentDetailModel paymentData;
    private String purchaseToken = "";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("callInAppBilling")) {
                if (intent.hasExtra("webSeriesEpisodeDetail")
                        && intent.hasExtra("episodeId")
                        && intent.hasExtra("seriesId")) {
                    webSeriesDetail.setLastWatch((WebSeriesDetailModel.Data.LastWatch) intent.getSerializableExtra("webSeriesEpisodeDetail"));
                    webSeriesDetail.getLastWatch().setId(Integer.parseInt(intent.getStringExtra("episodeId")));
                    webSeriesDetail.getLastWatch().setSeriesId(Integer.parseInt(intent.getStringExtra("seriesId")));
                    callInAppBilling();
                }
            } else {
                String resumeTime = "", totalTime = "", episodeId = "";
                if (intent.hasExtra("resumeTime")
                        && intent.hasExtra("totalTime")
                        && intent.hasExtra("episodeId")) {
                    resumeTime = intent.getStringExtra("resumeTime");
                    totalTime = intent.getStringExtra("totalTime");
                    episodeId = intent.getStringExtra("episodeId");
                    callStoreSeenTime1(resumeTime, totalTime, episodeId);
                }
            }
        }
    };
    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                Log.e("PAYMENT_STATUS", billingResult.getResponseCode() + " fail");
            } else {
                Log.e("PAYMENT_STATUS", billingResult.getResponseCode() + " fail");
            }
        }
    };
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
            Log.e("IN_APP", "acknowledgePurchaseResponseListener: " + billingResult.getDebugMessage());
        }
    };

    void handlePurchase(Purchase purchase) {
        if (purchase != null && purchase.getPurchaseToken() != null) {
            purchaseToken = purchase.getPurchaseToken();
            Log.e("PAYMENT_STATUS", purchase.getPurchaseToken() + "");
            callPaymentStatusApi("done", purchase.getPurchaseToken() + "");
        }

        Log.e("IN_APP", "getPurchaseState: " + purchase.getPurchaseState());
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            Log.e("IN_APP", "isAcknowledged: " + purchase.isAcknowledged());
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                Log.e("IN_APP", "PurchaseState.PURCHASED: " + Purchase.PurchaseState.PURCHASED);
            }
        }
    }

    static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_series_detail);

//        getEmail(this);
        init();
        listener();
        getIntentData();
        setTheme();
        setupBillingClient();
    }

    private void checkProductList() {
        billingClient.queryPurchaseHistoryAsync(
                QueryPurchaseHistoryParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                new PurchaseHistoryResponseListener() {
                    public void onPurchaseHistoryResponse(
                            BillingResult billingResult, List purchasesHistoryList) {
                        if (purchasesHistoryList != null)
                            for (Object purchase : purchasesHistoryList) {
                                Log.e("HistoryDetail1>>>", purchase + "");
                                try {
                                    Log.e("productId>>>", purchase.toString());
                                    JSONObject obj;
                                    if (purchase.toString().split("Json:").length > 1)
                                        obj = new JSONObject(purchase.toString().split("Json:")[1]);
                                    else
                                        obj = new JSONObject(purchase.toString());

                                    Log.e("productId>>>", obj.toString());
                                    String productId = "";
                                    productId = obj.getString("productId");

                                    if (productId.equalsIgnoreCase(PRODUCT_ID))
                                        preferences.setProductPurchaseStatus(true);

                                    Log.e("productId>>>", productId + "");
                                } catch (Exception e) {
                                    Log.e("HistoryDetail1>>>", e + "");
                                }
                            }
                    }
                }
        );

        Log.e("ValueResult>>>", preferences.getProductPurchaseStatus() + "");

    }

    private void getIntentData() {
        if (getIntent().hasExtra("seriesId")) {
            seriesId = getIntent().getStringExtra("seriesId");
            callWebSeriesDetailApi();
        }
    }

    private void init() {
        preferences = new NewsPreferences(this);
        preferences.setProductPurchaseStatus(false);
        viewPager = findViewById(R.id.view_pager);

        tv_episodes = findViewById(R.id.tv_episodes);
        tv_more_info = findViewById(R.id.tv_more_info);

        iv_cover_image = findViewById(R.id.iv_cover_image);
        tv_name = findViewById(R.id.tv_name);
        tv_seasons = findViewById(R.id.tv_seasons);
        tv_resume_button = findViewById(R.id.tv_resume_button);
        tv_detail = findViewById(R.id.tv_detail);
        tv_release_year = findViewById(R.id.tv_release_year);
        tv_total_episodes = findViewById(R.id.tv_total_episodes);
        ll_seasons = findViewById(R.id.ll_seasons);
        ll_start_over = findViewById(R.id.ll_start_over);
        progress_resume = findViewById(R.id.progress_resume);
        iv_trailer = findViewById(R.id.iv_trailer);
        tv_trailer = findViewById(R.id.tv_trailer);


    }

    private void listener() {
        findViewById(R.id.iv_back_arrow).setOnClickListener(this);
        tv_episodes.setOnClickListener(this);
        tv_more_info.setOnClickListener(this);
        ll_seasons.setOnClickListener(this);
        ll_start_over.setOnClickListener(this);
        tv_resume_button.setOnClickListener(this);
    }

    private void setViewPager() {
        viewPager.setOffscreenPageLimit(totalTab);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPagePosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        WebSeriesViewPagerAdapter viewPagerAdapter = new WebSeriesViewPagerAdapter(getSupportFragmentManager(), totalTab, webSeriesDetail);

        // set the adapter
        viewPager.setAdapter(viewPagerAdapter);

        // set the current item as 0 (when app opens for first time)
        viewPager.setCurrentItem(0);
    }

    private void setPagePosition(int position) {
        switch (position) {
            case 0:
                if (preferences.getTheme().equalsIgnoreCase("dark")) {
                    findViewById(R.id.view_episodes).setBackgroundColor(getResources().getColor(R.color.white));
                    findViewById(R.id.view_more_info).setBackgroundColor(getResources().getColor(R.color.black));
                } else {
                    findViewById(R.id.view_episodes).setBackgroundColor(getResources().getColor(R.color.appBlue));
                    findViewById(R.id.view_more_info).setBackgroundColor(getResources().getColor(R.color.white));
                }
                break;
            case 1:
                if (preferences.getTheme().equalsIgnoreCase("dark")) {
                    findViewById(R.id.view_more_info).setBackgroundColor(getResources().getColor(R.color.white));
                    findViewById(R.id.view_episodes).setBackgroundColor(getResources().getColor(R.color.black));
                } else {
                    findViewById(R.id.view_more_info).setBackgroundColor(getResources().getColor(R.color.appBlue));
                    findViewById(R.id.view_episodes).setBackgroundColor(getResources().getColor(R.color.white));
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_start_over:
                if (tv_trailer.getText().toString().equalsIgnoreCase("Trailer")) {
                    startActivity(new Intent(this, PlayerActivity.class)
                            .putExtra("videoUrl", webSeriesDetail.getGetTrailer().getVideoUrl()));
                } else if (Utils.getLoginToken(this).isEmpty())
                    Utils.setLoginPopup(this, preferences);
                else
                    callStoreSeenTime();
                break;
            case R.id.tv_episodes:
                setPagePosition(0);
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_more_info:
                setPagePosition(1);
                viewPager.setCurrentItem(1);
                break;
            case R.id.iv_back_arrow:
                onBackPressed();
                break;
            case R.id.ll_seasons:
                showSeasonList();
                break;
            case R.id.tv_resume_button:
                if (Utils.getLoginToken(this).isEmpty())
                    Utils.setLoginPopup(this, preferences);
                else
                    callInAppBilling();
//                startActivity(new Intent(WebSeriesDetailActivity.this, PlayerActivity.class)
//                        .putExtra("webSeriesEpisodeDetail", webSeriesDetail.getLastWatch())
//                        .putExtra("episodeId", webSeriesDetail.getLastWatch().getId().toString())
//                        .putExtra("seriesId", webSeriesDetail.getLastWatch().getSeriesId().toString()));

                break;
        }
    }

    private void callInAppBilling() {
        if (Utils.getLoginToken(this).isEmpty()) {
            Utils.setLoginPopup(this, preferences);
            return;
        }

        Log.e("purchaseToken>>>", purchaseToken + "");
        if (!purchaseToken.trim().isEmpty()) {
            if (webSeriesDetail != null && webSeriesDetail.getLastWatch() != null
                    && webSeriesDetail.getLastWatch().getId() != null
                    && webSeriesDetail.getLastWatch().getSeriesId() != null) {
                startActivity(new Intent(WebSeriesDetailActivity.this, PlayerActivity.class)
                        .putExtra("webSeriesEpisodeDetail", webSeriesDetail.getLastWatch())
                        .putExtra("episodeId", webSeriesDetail.getLastWatch().getId().toString())
                        .putExtra("seriesId", webSeriesDetail.getLastWatch().getSeriesId().toString()));
            }
        } else {
            Log.e("Value>>>", preferences.getProductPurchaseStatus() + "");
            if (preferences.getProductPurchaseStatus()) {
                if (webSeriesDetail != null && webSeriesDetail.getLastWatch() != null
                        && webSeriesDetail.getLastWatch().getId() != null
                        && webSeriesDetail.getLastWatch().getSeriesId() != null) {
                    startActivity(new Intent(WebSeriesDetailActivity.this, PlayerActivity.class)
                            .putExtra("webSeriesEpisodeDetail", webSeriesDetail.getLastWatch())
                            .putExtra("episodeId", webSeriesDetail.getLastWatch().getId().toString())
                            .putExtra("seriesId", webSeriesDetail.getLastWatch().getSeriesId().toString()));
                }
            } else if (billingClient.isReady()) {
                Log.e("BILLING_CLIENT", "Ready");

                List<String> productIds = new ArrayList<>();
                productIds.add(PRODUCT_ID);
                SkuDetailsParams getProductDetailQuery = SkuDetailsParams
                        .newBuilder()
                        .setSkusList(productIds)
                        .setType(BillingClient.SkuType.INAPP)
                        .build();

                billingClient.querySkuDetailsAsync(
                        getProductDetailQuery, new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK &&
                                        list != null) {
                                    if (list.size() > 0) {
                                        SkuDetails itemInfo = list.get(0);
                                        paymentData = new Gson().fromJson(itemInfo.getOriginalJson(), PaymentDetailModel.class);
                                        Log.e("PAYMENT_LIST_SIZE", list.size() + "");
                                        if (paymentData != null)
                                            billingClient.launchBillingFlow(WebSeriesDetailActivity.this, BillingFlowParams.newBuilder().setSkuDetails(itemInfo).build());
                                    }
                                }
                            }
                        }
                );


            } else {
                Log.e("BILLING_CLIENT", "Not Ready");
                Utils.showToast(getApplicationContext(), "This is a Paid Content & Purchasing content is not supported in your device.");
            }
        }
    }

    private String getUserId() {
        if (preferences.getUserData() != null && preferences.getUserData().getData() != null
                && preferences.getUserData().getData().getId() != null)
            return preferences.getUserData().getData().getId().toString();
        else
            return "";
    }

    private void setData() {
        if (webSeriesDetail != null) {
            if (webSeriesDetail.getLastWatch() != null
                    && webSeriesDetail.getLastWatch().getGetUserEpisode() != null
                    && webSeriesDetail.getLastWatch().getGetUserEpisode().getVideoTotalTime() != null
                    && webSeriesDetail.getLastWatch().getGetUserEpisode().getResumeAt() != null) {
                progress_resume.setMax(webSeriesDetail.getLastWatch().getGetUserEpisode().getVideoTotalTime());
                progress_resume.setProgress(webSeriesDetail.getLastWatch().getGetUserEpisode().getResumeAt());
            }

            if (webSeriesDetail.getSeason() != null && webSeriesDetail.getLastWatch() != null && webSeriesDetail.getLastWatch().getGetUserEpisode() != null) {
                tv_resume_button.setText("Resume " + "S " + webSeriesDetail.getSeason() + ", E " + webSeriesDetail.getLastWatch().getPart());

                if (preferences.getTheme().equalsIgnoreCase("dark"))
                    iv_trailer.setImageDrawable(getResources().getDrawable(R.drawable.over_white));
                else
                    iv_trailer.setImageDrawable(getResources().getDrawable(R.drawable.over));

                tv_trailer.setText("Start over");
            } else if (webSeriesDetail.getSeason() != null && webSeriesDetail.getGetEpisodes() != null && webSeriesDetail.getGetEpisodes().size() > 0 && webSeriesDetail.getGetEpisodes().get(0).getSeason() != null) {
                tv_resume_button.setText("Play " + "S " + webSeriesDetail.getSeason() + ", E " + webSeriesDetail.getGetEpisodes().get(0).getPart());

                if (preferences.getTheme().equalsIgnoreCase("dark"))
                    iv_trailer.setImageDrawable(getResources().getDrawable(R.drawable.trailer_white));
                else
                    iv_trailer.setImageDrawable(getResources().getDrawable(R.drawable.trailer));

                tv_trailer.setText("Trailer");
            } else {
                tv_resume_button.setVisibility(View.GONE);
                ll_start_over.setVisibility(View.GONE);
            }

            if (webSeriesDetail.getThumbNail() != null) {
                Utils.loadImageWithUrl(this, webSeriesDetail.getThumbNail() + "", iv_cover_image);

                String imageUrl = webSeriesDetail.getThumbNail();

                if (!webSeriesDetail.getThumbNail().toString().startsWith("http")) {
                    imageUrl = Constants.BASE_URl + "/" + webSeriesDetail.getThumbNail();
                }

                Picasso.with(this)
                        .load(imageUrl)
                        .transform(new BlurTransformation(this, 25, 2))
                        .into(((ImageView) findViewById(R.id.iv_cover_image1)));
            }
            if (webSeriesDetail.getName() != null /*&& webSeriesDetail.getSeason() != null*/)
                tv_name.setText(webSeriesDetail.getName() /*+ " - Season " + webSeriesDetail.getSeason()*/);

            if (webSeriesDetail.getSeason() != null) {
                tv_seasons.setText("Season " + webSeriesDetail.getSeason());
                if (webSeriesDetail.getGetSeason() != null) {
                    for (int i = 0; i < webSeriesDetail.getGetSeason().size(); i++) {
                        if (webSeriesDetail.getSeason() == webSeriesDetail.getGetSeason().get(i).getSeason())
                            webSeriesDetail.getGetSeason().get(i).setSelected(true);
                        else
                            webSeriesDetail.getGetSeason().get(i).setSelected(false);
                    }
                }
            }

            if (webSeriesDetail.getDescription() != null)
                tv_detail.setText(webSeriesDetail.getDescription() + "");

            if (webSeriesDetail.getReleaseYear() != null)
                tv_release_year.setText(webSeriesDetail.getReleaseYear() + "");

            if (webSeriesDetail.getGetEpisodesCount() != null)
                tv_total_episodes.setText(webSeriesDetail.getGetEpisodesCount() + " episodes");
        }
    }

    private void showSeasonList() {
        if (webSeriesDetail != null && webSeriesDetail.getGetSeason() != null) {
            if (seasonPopupWindow == null) {
                seasonPopupWindow = new ListPopupWindow(this);
                seasonPopupWindow.setAnchorView(ll_seasons);
                seasonPopupWindow.setAdapter(new SeasonsAdapter(this, webSeriesDetail.getGetSeason(), this));

                seasonPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                if (preferences.getTheme().equalsIgnoreCase("dark")) {
                    seasonPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.roundcorner_black));
                } else {
                    seasonPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.roundcorner_white));
                }
                seasonPopupWindow.setModal(true);

                seasonPopupWindow.show();
            } else if (seasonPopupWindow.isShowing()) {
                seasonPopupWindow.dismiss();
            } else
                seasonPopupWindow.show();
        }
    }

    private void callWebSeriesDetailApi() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }

        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        if (getUserId().equalsIgnoreCase("")) {
            Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                    .flatMap(s -> {
                        Observable<WebSeriesDetailModel> couponsObservable
                                = s.callWebSeriesDetail(Constants.BASE_URl + "/api/getEpisodesOfWebSeries?login_id=" + "" + "&series_id=" + seriesId,
                                preferences.getUserData().getLoginToken()).subscribeOn(Schedulers.io());
                        return Observable.concatArray(couponsObservable);
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessWebSeriesDetail, this::handleError);
        } else {
            Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                    .flatMap(s -> {
                        Observable<WebSeriesDetailModel> couponsObservable
                                = s.callWebSeriesDetail(Constants.BASE_URl + "/api/getEpisodesOfWebSeries?login_id=" + getUserId() + "&series_id=" + seriesId,
                                preferences.getUserData().getLoginToken()).subscribeOn(Schedulers.io());
                        return Observable.concatArray(couponsObservable);
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessWebSeriesDetail, this::handleError);
        }
    }

    private void OnSuccessWebSeriesDetail(WebSeriesDetailModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                if (modelResult.getData() != null) {
                    webSeriesDetail = modelResult.getData();
                    setData();
                    setViewPager();
                }
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            }
        }
    }

    private void callGetPurchaseToken() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);


        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<PurchaseDetailModel> couponsObservable
                            = s.getPurchaseToken(preferences.getUserData().getLoginToken()).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessGetPurchaseToken, this::handleError);

    }

    private void OnSuccessGetPurchaseToken(PurchaseDetailModel result) {
        Utils.hideProgressDialog();
        if (result != null && result.getErrorCode() != null) {
            if (result.getData() != null && result.getData().getPurchaseToken() != null)
                purchaseToken = result.getData().getPurchaseToken();
        }
    }

    private void callStoreSeenTime() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        if (preferences.getUserData().getData() != null && preferences.getUserData().getData().getId() != null)
            map.put("login_id", preferences.getUserData().getData().getId() + "");
        map.put("start_again", "1");
        map.put("episode_id", "0");
        map.put("series_id", seriesId);
        map.put("resume_at", "0");
        map.put("video_total_time", "1");

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<WebSeriesDetailModel> couponsObservable
                            = s.storeSeenTime(map, preferences.getUserData().getLoginToken()).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessStoreSeenTime, this::handleError);

    }

    private void OnSuccessStoreSeenTime(WebSeriesDetailModel singleNewModel) {
        Utils.hideProgressDialog();
        if (singleNewModel != null && singleNewModel.getErrorCode() != null) {
            if (webSeriesDetail != null && webSeriesDetail.getLastWatch() != null
                    && webSeriesDetail.getLastWatch().getGetUserEpisode() != null)
                webSeriesDetail.getLastWatch().getGetUserEpisode().setResumeAt(0);
            callInAppBilling();

        }
    }

    private void callStoreSeenTime1(String resume, String duration, String episodeId) {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        if (preferences.getUserData().getData() != null && preferences.getUserData().getData().getId() != null)
            map.put("login_id", preferences.getUserData().getData().getId() + "");
        map.put("episode_id", episodeId);
        map.put("series_id", seriesId);
        map.put("resume_at", resume);
        map.put("video_total_time", duration);

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<WebSeriesDetailModel> couponsObservable
                            = s.storeSeenTime(map, preferences.getUserData().getLoginToken()).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessStoreSeenTime1, this::handleError);

    }

    private void OnSuccessStoreSeenTime1(WebSeriesDetailModel singleNewModel) {
        Utils.hideProgressDialog();
        if (singleNewModel != null && singleNewModel.getErrorCode() != null) {
            callWebSeriesDetailApi();
        } /*else*/
//            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    private void callPaymentStatusApi(String status, String token) {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("payment_status", status);
        map.put("product_id", PRODUCT_ID + "");
        map.put("type", "inapp");

        if (paymentData != null && paymentData.getPriceCurrencyCode() != null)
            map.put("price_currency_code", paymentData.getPriceCurrencyCode() + "");
        else
            map.put("price_currency_code", "INR");

        if (paymentData != null && paymentData.getPrice() != null)
            map.put("price", paymentData.getPrice() + "");
        else
            map.put("price", "0");

        if (token != null)
            map.put("purchase_token", token);
        else
            map.put("purchase_token", "abc");


        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<WebSeriesDetailModel> couponsObservable
                            = s.storePaymentDetails(map, preferences.getUserData().getLoginToken()).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessStorePaymentStatusApi, this::handleError);

    }

    private void OnSuccessStorePaymentStatusApi(WebSeriesDetailModel singleNewModel) {
        Utils.hideProgressDialog();
        if (singleNewModel != null && singleNewModel.getErrorCode() != null) {
            if (webSeriesDetail != null && webSeriesDetail.getLastWatch() != null
                    && webSeriesDetail.getLastWatch().getId() != null
                    && webSeriesDetail.getLastWatch().getSeriesId() != null) {
                startActivity(new Intent(WebSeriesDetailActivity.this, PlayerActivity.class)
                        .putExtra("webSeriesEpisodeDetail", webSeriesDetail.getLastWatch())
                        .putExtra("episodeId", webSeriesDetail.getLastWatch().getId().toString())
                        .putExtra("seriesId", webSeriesDetail.getLastWatch().getSeriesId().toString()));
            }
        }
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnItemClick(String type, int position) {
        switch (type) {
            case "seasonSelected":
                if (webSeriesDetail.getGetSeason().get(position).getId() != null)
                    seriesId = webSeriesDetail.getGetSeason().get(position).getId().toString();
                tv_seasons.setText("Season " + webSeriesDetail.getGetSeason().get(position).getSeason());
                showSeasonList();
                callWebSeriesDetailApi();
                break;
        }
    }

    private void setTheme() {
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            Utils.clearLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackground(getResources().getDrawable(R.color.black));
            findViewById(R.id.appbar).setBackground(getResources().getDrawable(R.color.black));
            tv_name.setTextColor(getResources().getColor(R.color.appBlackxLight));
            tv_seasons.setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((ImageView) findViewById(R.id.iv_dropdown)).setImageDrawable(getResources().getDrawable(R.drawable.dropdown_white));
            tv_trailer.setTextColor(getResources().getColor(R.color.appBlackxLight));
            tv_detail.setTextColor(getResources().getColor(R.color.appBlackxLight));
            tv_release_year.setTextColor(getResources().getColor(R.color.appBlackxLight));
            tv_total_episodes.setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((LinearLayout) findViewById(R.id.ll_tab_view)).setBackgroundColor(getResources().getColor(R.color.black));
            tv_episodes.setTextColor(getResources().getColor(R.color.appBlackxLight));
            tv_more_info.setTextColor(getResources().getColor(R.color.appBlackxLight));
            findViewById(R.id.view_episodes).setBackgroundColor(getResources().getColor(R.color.white));
            findViewById(R.id.view_more_info).setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            Utils.setLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackground(getResources().getDrawable(R.color.white));
            findViewById(R.id.appbar).setBackground(getResources().getDrawable(R.color.white));
            tv_name.setTextColor(getResources().getColor(R.color.appBlackDark));
            tv_seasons.setTextColor(getResources().getColor(R.color.appBlackDark));
            ((ImageView) findViewById(R.id.iv_dropdown)).setImageDrawable(getResources().getDrawable(R.drawable.dropdown));
            tv_trailer.setTextColor(getResources().getColor(R.color.appBlackDark));
            tv_detail.setTextColor(getResources().getColor(R.color.appBlackDark));
            tv_release_year.setTextColor(getResources().getColor(R.color.appBlackDark));
            tv_total_episodes.setTextColor(getResources().getColor(R.color.appBlackDark));
            ((LinearLayout) findViewById(R.id.ll_tab_view)).setBackgroundColor(getResources().getColor(R.color.white));
            tv_episodes.setTextColor(getResources().getColor(R.color.appBlackDark));
            tv_more_info.setTextColor(getResources().getColor(R.color.appBlackDark));
            findViewById(R.id.view_episodes).setBackgroundColor(getResources().getColor(R.color.appBlue));
            findViewById(R.id.view_more_info).setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("updateWebSeriesDetailScreen");
        filter.addAction("callInAppBilling");
        registerReceiver(broadcastReceiver, filter);

        if (!Utils.getLoginToken(this).isEmpty())
            callGetPurchaseToken();
    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(getApplicationContext())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    checkProductList();
//                    Toast.makeText(WebSeriesDetailActivity.this, "Success to connect Billing", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(WebSeriesDetailActivity.this, billingResult.getResponseCode() + "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
//                Toast.makeText(WebSeriesDetailActivity.this, "You are disconnect form Billing", Toast.LENGTH_SHORT).show();
            }
        });
    }

}