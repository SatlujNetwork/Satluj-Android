package com.emobx.news.Activities;

import androidx.annotation.NonNull;
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
import com.emobx.news.Adapters.BookmarksAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.BookmarkData;
import com.emobx.news.Model.NewsListModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BookmarksActivity extends BaseActivity implements View.OnClickListener, AdapterItemClickListner, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView rv_bookmarks;
    private BookmarksAdapter bookmarksAdapter;
    private NewsPreferences preferences;
    private ArrayList<BookmarkData.Datum> bookmarkList = new ArrayList<>();
    private ApiInterface apiInterface;
    private SwipeRefreshLayout swipeRefresh;
    private String postValue = "0";
    private boolean hasMore = false;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("updateBookmarkList")) {
                callGetBookmarkData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        init();
        listener();
        setAdapter();
        callGetBookmarkData();
    }

    private void callGetBookmarkData() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);
        if (apiInterface == null)
            apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("post_value", postValue);

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<BookmarkData> couponsObservable
                            = s.getBookmarkList(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccess, this::handleError);

    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void OnSuccess(BookmarkData modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                hasMore = modelResult.isHasMore();
                bookmarkList.addAll(modelResult.getData());
                setAdapter();
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            }
        }
    }

    private void init() {
        preferences = new NewsPreferences(this);
        findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title)).setText("Bookmarks");
        findViewById(R.id.iv_back).setVisibility(View.VISIBLE);

        rv_bookmarks = findViewById(R.id.rv_bookmarks);
        swipeRefresh = findViewById(R.id.swipeRefresh);
    }

    private void listener() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        swipeRefresh.setOnRefreshListener(this);

        rv_bookmarks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                if (bookmarkList != null)
                    if (hasMore && position == bookmarkList.size() - 1) {
                        hasMore = false;
                        postValue = position + 1 + "";
                        callGetBookmarkData();
                    }
            }
        });
    }

    private void setAdapter() {
        ArrayList<String> data = new ArrayList<>();
        if (bookmarksAdapter == null) {
            bookmarksAdapter = new BookmarksAdapter(this, bookmarkList, this);
            rv_bookmarks.setLayoutManager(new LinearLayoutManager(this));
            rv_bookmarks.setAdapter(bookmarksAdapter);
        } else
            bookmarksAdapter.notifyDataSetChanged();
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
    public void OnItemClick(String type, int i) {
        switch (type) {
            case "openNewsDetail":
                startActivity(new Intent(this, EntertainmentDetailActivity.class)
                        .putExtra("newsId", bookmarkList.get(i).getNewsId() + ""));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            Utils.clearLightStatusBar(findViewById(android.R.id.content).getRootView(), this);

            findViewById(R.id.rl_main).setBackgroundColor(getResources().getColor(R.color.black));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.black));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_white));
        } else {
            Utils.setLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(getResources().getColor(R.color.white));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.white));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_black));
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("updateBookmarkList");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onRefresh() {
        bookmarkList.clear();
        callGetBookmarkData();
        swipeRefresh.setRefreshing(false);
    }

}