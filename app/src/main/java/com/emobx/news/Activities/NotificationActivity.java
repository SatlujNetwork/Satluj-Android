package com.emobx.news.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Adapters.NotificationAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.Model.NotificationListModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotificationActivity extends BaseActivity implements View.OnClickListener, AdapterItemClickListner, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rv_notification;
    private NotificationAdapter notificationAdapter;
    private NewsPreferences preferences;
    private ApiInterface apiInterface;
    private NotificationListModel notificationModelResult;
    private ImageView iv_refresh;
    private SwipeRefreshLayout swipeRefresh;
    private boolean hasMore = false;
    private String postValue = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        init();
        listener();
        callNotificationApi();
    }

    private void init() {
        preferences = new NewsPreferences(this);

        findViewById(R.id.tv_title_left).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title_left)).setText("Notifications");
        findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_refresh).setVisibility(View.VISIBLE);

        rv_notification = findViewById(R.id.rv_notification);
        iv_refresh = findViewById(R.id.iv_refresh);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
    }

    private void setAdapter() {
        if (notificationAdapter == null) {
            notificationAdapter = new NotificationAdapter(this, notificationModelResult, this);
            rv_notification.setLayoutManager(new LinearLayoutManager(this));
            rv_notification.setAdapter(notificationAdapter);
        } else
            notificationAdapter.notifyDataSetChanged();
    }

    private void listener() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        iv_refresh.setOnClickListener(this);

        rv_notification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                if (notificationModelResult != null)
                    if (hasMore && position == notificationModelResult.getData().size() - 1) {
                        hasMore = false;
                        postValue = position + 1 + "";
                        callNotificationApi();
                    }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_refresh:
                callNotificationApi();
                break;
        }
    }

    @Override
    public void OnItemClick(String type, int position) {
        switch (type) {
            case "notificationClick":
                startActivity(new Intent(this, EntertainmentDetailActivity.class)
                        .putExtra("newsId", notificationModelResult.getData().get(position).getId() + "")
                        .putExtra("NotificationNews", notificationModelResult.getData().get(position)));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            Utils.clearLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(this.getResources().getColor(R.color.black));
            findViewById(R.id.rl_top_view).setBackgroundColor(this.getResources().getColor(R.color.black));
            ((TextView) findViewById(R.id.tv_title_left)).setTextColor(this.getResources().getColor(R.color.appBlackxLight));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_white));
            ((ImageView) findViewById(R.id.iv_refresh)).setImageDrawable(getResources().getDrawable(R.drawable.refresh_white));
        } else {
            Utils.setLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(this.getResources().getColor(R.color.white));
            findViewById(R.id.rl_top_view).setBackgroundColor(this.getResources().getColor(R.color.white));
            ((TextView) findViewById(R.id.tv_title_left)).setTextColor(this.getResources().getColor(R.color.appBlackDark));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_black));
            ((ImageView) findViewById(R.id.iv_refresh)).setImageDrawable(getResources().getDrawable(R.drawable.refresh));
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("callNotificationCount");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                callNotificationApi();
            }
        };
        registerReceiver(receiver, filter);
    }

    private void callNotificationApi() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());
        map.put("post_value", postValue);

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<NotificationListModel> couponsObservable
                            = s.getNotificationList(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessNotification, this::handleError);

    }

    private void OnSuccessNotification(NotificationListModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                notificationModelResult = modelResult;
                hasMore = notificationModelResult.getHasMore();
                setAdapter();
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            }
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(false);
        callNotificationApi();
    }
}